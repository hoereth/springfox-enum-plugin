# springfox-enum-plugin

OpenAPI / Swagger / Springfox has still no way of documenting enumerations in a structured way (as of OpenAPI 3.0). This Plugin introduces the [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotation to overcome this problem.

## Maven Coordinates

```xml
<dependency>
	<groupId>de.pentabyte</groupId>
	<artifactId>springfox-enum-plugin</artifactId>
	<version>1.0.2</version>
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

Then - whenever this enumeration is used in combination with _@ApiModelProperty_, the plugin will extend the standard description.

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