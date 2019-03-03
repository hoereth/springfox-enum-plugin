package de.pentabyte.springfox;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import de.pentabyte.springfox.model.SomeEnum;
import de.pentabyte.springfox.model.SomeEnum2;
import de.pentabyte.springfox.model.SomeModel;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

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
		test(SomeModel.class, "attribute", "This is the standard Swagger description for attribute.\n"
				+ "* A: First Option\n" + "* B: Second Option\n" + "* C: _@ApiEnum annotation not available_");
	}

	@Test
	public void test_apply_ModelPropertyContext_Ordinal() throws NoSuchFieldException, SecurityException {
		test(SomeModel.class, "ordinalAttribute", "This is an integer, but will be documented as enum.\n"
				+ "* A: First Option\n" + "* B: Second Option\n" + "* C: _@ApiEnum annotation not available_");
	}

	private void test(Class<?> clazz, String attributeName, String expectedDescription) {
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
}
