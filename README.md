# ✏️ springfox-enum-plugin

OpenAPI / Swagger / Springfox provide no way of documenting enumerations in a structured way (as of [OpenAPI 3.0](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md)). This Springfox plugin introduces the [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotation to automate the current suggestion: _"If you need to specify descriptions for enum items, you can do this in the description of the parameter or property"_ ([swagger.io](https://swagger.io/docs/specification/data-models/enums/)).

## Compatibility with Springfox

The **1.x** branch of this project will only work with Springfox **2.x**.

## Maven Coordinates

```xml
<!-- https://mvnrepository.com/artifact/de.pentabyte/springfox-enum-plugin -->
<dependency>
    <groupId>de.pentabyte</groupId>
    <artifactId>springfox-enum-plugin</artifactId>
    <version>1.2.0</version>
</dependency>
```

## Usage

Make your Spring application use this component: [ApiEnumDescriptionPlugin](src/main/java/de/pentabyte/springfox/ApiEnumDescriptionPlugin.java). Example:

```java
@Configuration
@Import(ApiEnumDescriptionPlugin.class) // add this line
public void MySpringConfiguration {
	...
}
```

The plugin automatically registers with Springfox (version 2) and will process these proprietary [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotations:

```java
public enum SomeEnum {
	/**
	 * Java Doc comment
	 */
	@ApiEnum("First Option")
	A,
	@ApiEnum("Second Option")
	B,
	C
}
```

Then - whenever such an enumeration is used in combination with _@ApiModelProperty_ or _@ApiParam_, the plugin will extend the standard description. Examples:

```java
@ApiModelProperty("Some description.")
SomeEnum attribute;

@ApiModelProperty(value = "Some description.", dataType = "...SomeEnum")
Integer attribute2;

public void someMethod(@ApiParam("Some description.") SomeEnum param) { ... }
```

It effectively produces this description in markdown syntax for _attribute_, _attribute2_ and _param_. It will not touch the description if none of the enums are annotated, though.

```
Some description.
* A: First option
* B: Second option
* C: _@ApiEnum annotation not available_
```

## Outlook

It seems obvious that this a temporary solution. Once the OpenAPI specs provide a relevant new feature for handling enumerations, it should be fairly simple to drop the usage of this plugin and replace all [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotations with their future counterparts.
