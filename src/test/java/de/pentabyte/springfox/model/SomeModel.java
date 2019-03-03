package de.pentabyte.springfox.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author michael hoereth
 */
@ApiModel
public class SomeModel {
	@ApiModelProperty("This is the standard Swagger description for attribute.")
	SomeEnum attribute;

	@ApiModelProperty(value = "This is an integer, but will be documented as enum.", dataType = "de.pentabyte.springfox.model.SomeEnum")
	Integer ordinalAttribute;

	public SomeEnum getAttribute() {
		return attribute;
	}

	public void setAttribute(SomeEnum attribute) {
		this.attribute = attribute;
	}

	public Integer getOrdinalAttribute() {
		return ordinalAttribute;
	}

	public void setOrdinalAttribute(Integer ordinalAttribute) {
		this.ordinalAttribute = ordinalAttribute;
	}

}
