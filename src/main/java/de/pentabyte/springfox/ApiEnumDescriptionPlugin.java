package de.pentabyte.springfox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Will extend all {@link ApiModelProperty}s' and {@link ApiParam}s' description
 * with a list of ENUM descriptions, if the property is an enum and if at least
 * one of its values is annotated with {@link ApiEnum} to make this work.
 *
 * @author Michael Höreth
 * @since 2018
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class ApiEnumDescriptionPlugin implements ModelPropertyBuilderPlugin, ParameterBuilderPlugin {
	private static Logger LOG = LoggerFactory.getLogger(ApiEnumDescriptionPlugin.class);

	@Override
	public boolean supports(DocumentationType delimiter) {
		return SwaggerPluginSupport.pluginDoesApply(delimiter);
	}

	@Override
	public void apply(ModelPropertyContext context) {
		try {
			Optional<BeanPropertyDefinition> beanDef = context.getBeanPropertyDefinition();
			if (beanDef.isPresent()) {
				AnnotatedField aField = beanDef.get().getField();
				if (aField != null) {
					Field field = aField.getAnnotated();
					if (field != null) {
						ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
						if (property != null) {
							Class<?> clazz = field.getType();
							buildDescription(context, property, clazz);
							String dataType = property.dataType();
							if (StringUtils.isNotBlank(dataType)) {
								try {
									Class<?> clazz2 = Class.forName(dataType);
									buildDescription(context, property, clazz2);
								} catch (ClassNotFoundException e) {
									// Sometimes dataType maybe null or wrong.
									LOG.warn("Cannot find dataType " + dataType);
								}
							}
						}
					}
				}
			}
		} catch (Throwable t) {
			// The exception will be logged, because Springfox will not.
			LOG.warn("Cannot process ApiModelProperty. Will throw new RuntimeException now.", t);
			throw new RuntimeException(t);
		}
	}

	/**
	 * 
	 * If dataType can convert an Enum Class Type, or field type is an Enum
	 * Class Type. Create markdown description.
	 *
	 */
	private void buildDescription(ModelPropertyContext context, ApiModelProperty property, Class<?> clazz) {
		if (clazz.isEnum()) {
			String description = property.value();
			@SuppressWarnings("unchecked")
			String markdown = createMarkdownDescription((Class<? extends Enum<?>>) clazz);
			if (markdown != null) {
				description += "\n" + markdown;
				context.getSpecificationBuilder().description(description);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see springfox.documentation.spi.service.ParameterBuilderPlugin#apply(
	 * springfox.documentation.spi.service.contexts.ParameterContext)
	 */
	@Override
	public void apply(ParameterContext context) {
		try {
			ResolvedMethodParameter param = context.resolvedMethodParameter();

			if (param != null) {
				ResolvedType resType = param.getParameterType();
				if (resType != null) {
					Class<?> clazz = resType.getErasedType();
					if (clazz.isEnum()) {
						Optional<ApiParam> annotation = param.findAnnotation(ApiParam.class);
						if (annotation.isPresent()) {
							String description = annotation.get().value();
							@SuppressWarnings("unchecked")
							String markdown = createMarkdownDescription((Class<? extends Enum<?>>) clazz);
							if (markdown != null) {
								description += "\n" + markdown;
								context.requestParameterBuilder().description(description);
							}
						}
					}
				}
			}
		} catch (Throwable t) {
			// The exception will be logged, because Springfox will not.
			LOG.warn("Cannot process ApiParameter. Will throw new RuntimeException now.", t);
			throw new RuntimeException(t);
		}
	}

	/**
	 * Creates a markdown description of all enums of <i>clazz</i> including the
	 * description which is being pulled from {@link ApiEnum}.
	 */
	static String createMarkdownDescription(Class<? extends Enum<?>> clazz) {

		Optional<Method> jsonValueMethod = findJsonValueAnnotatedMethod(clazz);

		List<String> lines = new ArrayList<>();

		boolean foundAny = false;
		for (Enum<?> enumVal : clazz.getEnumConstants()) {
			String desc = readApiDescription(enumVal);
			if (desc != null) {
				foundAny = true;
			}

			String enumName = jsonValueMethod.map(evaluateJsonValue(enumVal))
					.orElse(enumVal.name());

			String line = "* " + enumName + ": "
					+ (desc == null ? "_@ApiEnum annotation not available_" : desc);
			lines.add(line);
		}

		if (foundAny) {
			return StringUtils.join(lines, "\n");
		} else {
			return null;
		}
	}

	/**
	 * @return the value of {@link ApiEnum#value()}, if present for <i>e</i>.
	 */
	static String readApiDescription(Enum<?> e) {
		try {
			ApiEnum annotation = e.getClass().getField(e.name()).getAnnotation(ApiEnum.class);
			if (annotation != null) {
				return annotation.value();
			}
		} catch (NoSuchFieldException e1) {
			throw new RuntimeException("impossible?", e1);
		} catch (SecurityException e1) {
			throw new RuntimeException("could not read annotation", e1);
		}
		return null;
	}

	private static Optional<Method> findJsonValueAnnotatedMethod(Class<? extends Enum<?>> clazz) {
		for (Method each : clazz.getMethods()) {
			JsonValue jsonValue = AnnotationUtils.findAnnotation(each, JsonValue.class);
			if (jsonValue != null && jsonValue.value()) {
				return Optional.of(each);
			}
		}
		return Optional.empty();
	}

	private static Function<Method, String> evaluateJsonValue(final Object enumConstant) {
		return new Function<Method, String>() {
			@Override
			public String apply(Method input) {
				try {
					return input.invoke(enumConstant).toString();
				} catch (Exception ignored) {
					return "";
				}
			}
		};
	}
}
