package de.pentabyte.springfox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;

import io.swagger.annotations.ApiModelProperty;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Will extend all {@link ApiModelProperty}s' description with a list of ENUM
 * descriptions, if the property is an enum and if at least one of its values is
 * annotated with {@link ApiEnum} to make this work.
 * 
 * @author Michael Höreth
 * @since 2018
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class ApiEnumDescriptionPlugin implements ModelPropertyBuilderPlugin {
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
						Class<?> clazz = field.getType();
						if (clazz.isEnum()) {
							ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
							if (property != null) {
								String description = property.value();
								@SuppressWarnings("unchecked")
								String markdown = createMarkdownDescription((Class<? extends Enum<?>>) clazz);
								if (markdown != null) {
									description += "\n" + markdown;
									context.getBuilder().description(description);
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
	 * Creates a markdown description of all enums of <i>clazz</i> including the
	 * description which is being pulled from {@link ApiEnum}.
	 */
	static String createMarkdownDescription(Class<? extends Enum<?>> clazz) {
		List<String> lines = new ArrayList<>();

		boolean foundAny = false;
		for (Enum<?> enumVal : clazz.getEnumConstants()) {
			String desc = readApiDescription(enumVal);
			if (desc != null) {
				foundAny = true;
			}
			String line = "* " + enumVal.name() + ": " + (desc == null ? "_@ApiEnum annotation not available_" : desc);
			lines.add(line);
		}

		if (foundAny)
			return StringUtils.join(lines, "\n");
		else
			return null;
	}

	/**
	 * @return the value of {@link ApiEnum#value()}, if present for <i>e</i>.
	 */
	static String readApiDescription(Enum<?> e) {
		try {
			ApiEnum annotation = e.getClass().getField(e.name()).getAnnotation(ApiEnum.class);
			if (annotation != null)
				return annotation.value();
		} catch (NoSuchFieldException e1) {
			throw new RuntimeException("impossible?", e1);
		} catch (SecurityException e1) {
			throw new RuntimeException("could not read annotation", e1);
		}
		return null;
	}

}
