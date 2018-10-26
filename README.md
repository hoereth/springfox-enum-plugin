# springfox-enum-plugin

OpenAPI / Swagger / Springfox has still no way of documenting enumerations in a structured way (as of OpenAPI 3.0). This Plugin introduces the @ApiEnum annotation to overcome this problem.

## Maven Coordinates

```xml
<dependency>
	<groupId>de.pentabyte</groupId>
	<artifactId>springfox-enum-plugin</artifactId>
	<version>1.0.0</version>
</dependency>
```

## Usage

This plugin automatically registers with Spring and Springfox and will process these proprietary @de.pentabyte.springfox.ApiEnum annotations:

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

Then - whenever this enumeration is used in combination with @ApiModelProperty, the plugin will extend the standard description.

```java
@ApiModelProperty("This is the standard Swagger description.")
SomeEnum attribute;
```

It effectively produces this description in markup syntax for _attribute_ (and any other attribute of the same type).

```
This is the standard Swagger description.
* A: First option
* B: Second option
* C: _@ApiEnum annotation not available_
```

## Outlook

It seems obvious that this a temporary solution. Once the OpenAPI specs provide a relevant new feature for handling enumerations, it should be fairly simple to drop the usage of this plugin and replace all @ApiEnum annotations with their future counterparts.