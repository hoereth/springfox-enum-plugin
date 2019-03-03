package de.pentabyte.springfox.model;

import io.swagger.annotations.ApiParam;

/**
 * @author michael hoereth
 *
 */
public class SomeController {
	public void someMethod(@ApiParam("Some description.") SomeEnum param) {
		// NOP
	}
}
