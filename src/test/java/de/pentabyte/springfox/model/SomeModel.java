package de.pentabyte.springfox.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author michael hoereth
 */
@ApiModel
public class SomeModel {
	@ApiModelProperty("Some description.")
	SomeEnum attribute;

	@ApiModelProperty(value = "Some description.", dataType = "de.pentabyte.springfox.model.SomeEnum")
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
