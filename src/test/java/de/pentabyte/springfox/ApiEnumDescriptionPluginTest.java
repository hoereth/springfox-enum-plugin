package de.pentabyte.springfox;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import de.pentabyte.springfox.model.SomeController;
import de.pentabyte.springfox.model.SomeEnum;
import de.pentabyte.springfox.model.SomeEnum2;
import de.pentabyte.springfox.model.SomeModel;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

public class ApiEnumDescriptionPluginTest {
	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void test_createMarkdownDescription() {
		String expected = "* A: First Option\n* B: Second Option\n* C: _@ApiEnum annotation not available_";
		Assert.assertEquals(expected, ApiEnumDescriptionPlugin.createMarkdownDescription(SomeEnum.class));

		Assert.assertNull(ApiEnumDescriptionPlugin.createMarkdownDescription(SomeEnum2.class));
	}

	@Test
	public void test_readApiDescription() {
		Assert.assertEquals("First Option", ApiEnumDescriptionPlugin.readApiDescription(SomeEnum.A));
	}

	@Test
	public void test_apply_ModelPropertyContext() throws NoSuchFieldException, SecurityException {
		test_modelProperty(SomeModel.class, "attribute", "Some description.\n" + "* A: First Option\n"
				+ "* B: Second Option\n" + "* C: _@ApiEnum annotation not available_");
	}

	@Test
	public void test_apply_ModelPropertyContext_Ordinal() throws NoSuchFieldException, SecurityException {
		test_modelProperty(SomeModel.class, "attribute2", "Some description.\n" + "* A: First Option\n"
				+ "* B: Second Option\n" + "* C: _@ApiEnum annotation not available_");
	}
	
	@Test
	public void test_apply_ModelPropertyContext_JsonValue() throws NoSuchFieldException, SecurityException {
		test_modelProperty(SomeModel.class, "attribute3", "Some description.\n" + "* a-1: A One\n"
				+ "* b-2: B Two");
	}

	private void test_modelProperty(Class<?> clazz, String attributeName, String expectedDescription) {
		ApiEnumDescriptionPlugin plugin = new ApiEnumDescriptionPlugin();
		TypeResolver resolver = new TypeResolver();
		JavaType type = mapper.constructType(SomeModel.class);
		BeanDescription beanDescription = mapper.getDeserializationConfig().introspect(type);
		boolean found = false;
		for (BeanPropertyDefinition def : beanDescription.findProperties()) {
			if (def.getName().equals(attributeName)) {
				found = true;

				PropertySpecificationBuilder builder = new PropertySpecificationBuilder(attributeName);
				ModelPropertyContext context = new ModelPropertyContext(
								new ModelPropertyBuilder(),
						        def,
						        resolver,
						        null,
						        builder);
				plugin.apply(context);
				PropertySpecification property = builder.build();
				Assert.assertEquals(expectedDescription, property.getDescription());
			}
		}
		if (!found) {
			throw new RuntimeException(
					"could not find BeanPropertyDefinition for " + clazz.getName() + " : " + attributeName);
		}
	}

	@Test
	public void test_apply_ParameterContext() throws NoSuchMethodException, SecurityException {
		ApiEnumDescriptionPlugin plugin = new ApiEnumDescriptionPlugin();

		Method method = SomeController.class.getDeclaredMethod("someMethod", SomeEnum.class);
		MethodParameter mp = new MethodParameter(method, 0);
		TypeResolver resolver = new TypeResolver();
		ResolvedType type = resolver.resolve(SomeEnum.class);
		ResolvedMethodParameter p = new ResolvedMethodParameter("param", mp, type);
		OperationContext operationContext = new OperationContext(null, null, null, 0) {

			@Override
			public Set<MediaType> consumes() {
				return new HashSet<>();
			}
			
		};
		ParameterContext context = new ParameterContext(p, null, null, operationContext, 0);

		plugin.apply(context);
		Assert.assertEquals("Some description.\n" + "* A: First Option\n" + "* B: Second Option\n"
				+ "* C: _@ApiEnum annotation not available_", context.requestParameterBuilder().build().getDescription());
	}
}
