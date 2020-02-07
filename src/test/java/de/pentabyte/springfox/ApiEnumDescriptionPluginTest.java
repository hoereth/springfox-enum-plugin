package de.pentabyte.springfox;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;

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
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

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
		ModelPropertyBuilder builder = new ModelPropertyBuilder();
		TypeResolver resolver = new TypeResolver();
		JavaType type = mapper.constructType(SomeModel.class);
		BeanDescription beanDescription = mapper.getDeserializationConfig().introspect(type);
		boolean found = false;
		for (BeanPropertyDefinition def : beanDescription.findProperties()) {
			if (def.getName().equals(attributeName)) {
				found = true;
				ModelPropertyContext context = new ModelPropertyContext(builder, def, resolver,
						DocumentationType.SWAGGER_2);
				plugin.apply(context);
				ModelProperty property = builder.build();
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
		ParameterBuilder builder = new ParameterBuilder();
		ParameterContext context = new ParameterContext(p, builder, null, null, null);

		plugin.apply(context);
		Assert.assertEquals("Some description.\n" + "* A: First Option\n" + "* B: Second Option\n"
				+ "* C: _@ApiEnum annotation not available_", builder.build().getDescription());
	}
}
