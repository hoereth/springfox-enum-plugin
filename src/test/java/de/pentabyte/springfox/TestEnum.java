package de.pentabyte.springfox;

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
