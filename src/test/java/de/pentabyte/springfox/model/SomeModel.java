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
	Integer attribute2;
	
	@ApiModelProperty(value = "Some description.")
	SomeEnumWithJsonValueAnnotation attribute3;

	public SomeEnum getAttribute() {
		return attribute;
	}

	public void setAttribute(SomeEnum attribute) {
		this.attribute = attribute;
	}

	public Integer getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(Integer attribute2) {
		this.attribute2 = attribute2;
	}

	public SomeEnumWithJsonValueAnnotation getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(SomeEnumWithJsonValueAnnotation attribute3) {
		this.attribute3 = attribute3;
	}

}