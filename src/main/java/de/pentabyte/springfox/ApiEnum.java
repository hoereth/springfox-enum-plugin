package de.pentabyte.springfox;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Adds API descriptions to ENUM values. The custom Springfox plugin
 * {@link ApiEnumDescriptionPlugin} will be able to automatically process this
 * for API documentation purposes.
 * 
 * @author Michael Höreth
 * @since 2018
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ApiEnum {
	/**
	 * @return API description.
	 */
	String value();
}
