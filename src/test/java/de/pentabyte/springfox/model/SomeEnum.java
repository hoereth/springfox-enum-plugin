package de.pentabyte.springfox.model;

import de.pentabyte.springfox.ApiEnum;

public enum SomeEnum {
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
