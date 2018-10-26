package de.pentabyte.springfox;

import org.junit.Assert;
import org.junit.Test;

public class ApiEnumDescriptionPluginTest {
	public enum TestEnum {
		@ApiEnum("First Option")
		A,
		/**
		 * JavaDoc comment.
		 */
		@ApiEnum("Second Option")
		B,
		/**
		 * JavaDoc comment.
		 */
		C
	}

	public enum TestEnum2 {
		A, B
	}

	@Test
	public void testCreateMarkdownDescription() {
		String expected = "* A: First Option\n* B: Second Option\n* C: _@ApiEnum annotation not available_";
		Assert.assertEquals(expected, ApiEnumDescriptionPlugin.createMarkdownDescription(TestEnum.class));

		Assert.assertNull(ApiEnumDescriptionPlugin.createMarkdownDescription(TestEnum2.class));
	}

	@Test
	public void testReadApiDescription() {
		Assert.assertEquals("First Option", ApiEnumDescriptionPlugin.readApiDescription(TestEnum.A));
	}
}
