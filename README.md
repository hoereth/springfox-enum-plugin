# springfox-enum-plugin

OpenAPI / Swagger / Springfox has still no way of documenting enumerations in a structured way (as of [OpenAPI 3.0](https://swagger.io/docs/specification/data-models/enums/)). This Plugin introduces the [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotation to automate the current suggestion: "If you need to specify descriptions for enum items, you can do this in the description of the parameter or property" (swagger.io).

## Maven Coordinates

```xml
<dependency>
	<groupId>de.pentabyte</groupId>
	<artifactId>springfox-enum-plugin</artifactId>
	<version>1.1.0</version>
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

The plugin automatically registers with Springfox and will process these proprietary [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotations:

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

Then - whenever this enumeration is used in combination with _@ApiModelProperty_ or _@ApiParam_, the plugin will extend the standard description. Example:

```java
@ApiModelProperty("This is the standard Swagger description for attribute.")
SomeEnum attribute;
```

It effectively produces this description in markup syntax for _attribute_. It will not touch the description if none of the enums are annotated, though.

```
This is the standard Swagger description for attribute.
* A: First option
* B: Second option
* C: _@ApiEnum annotation not available_
```

## Outlook

It seems obvious that this a temporary solution. Once the OpenAPI specs provide a relevant new feature for handling enumerations, it should be fairly simple to drop the usage of this plugin and replace all @ApiEnum annotations with their future counterparts.