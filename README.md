# ✏️ springfox-enum-plugin

OpenAPI / Swagger / Springfox provide no way of documenting enumerations in a structured way (as of [OpenAPI 3.0](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md)). This Springfox plugin introduces the [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotation to automate the current suggestion: _"If you need to specify descriptions for enum items, you can do this in the description of the parameter or property"_ ([swagger.io](https://swagger.io/docs/specification/data-models/enums/)).

## Compatibility with Springfox

Springfox **2.x** <-> springfox-enum-plugin **1.x**.

Springfox **3.x** <-> springfox-enum-plugin **3.x**.

## Maven Coordinates

```xml
<!-- https://mvnrepository.com/artifact/de.pentabyte/springfox-enum-plugin -->
<dependency>
    <groupId>de.pentabyte</groupId>
    <artifactId>springfox-enum-plugin</artifactId>
    <version>3.0.0</version>
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
	A, //
	@ApiEnum("Second Option")
	B, //
	C
}
```

Then - whenever such an enumeration is used in combination with _@ApiModelProperty_ property or _@ApiParam_, the plugin will extend the standard description. Examples:

```java
@Getter
@Setter
...
@ApiModelProperty("Some description.")
SomeEnum attribute;

@ApiModelProperty(value = "Some description.", dataType = "org.example.SomeEnum")
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

### Custom Enum Names

The plugin will also pick up Jackson's custom mapping of enum names like this one:

```java
public enum SomeEnumWithJsonValueAnnotation {
    @ApiEnum("A One")
    A_1, //
    @ApiEnum("B Two")
    B_2;

    @com.fasterxml.jackson.annotation.JsonValue
    /**
     * A_1 and B_2 will be mapped to a-1 and b-2.
     */
    public String toJson() {
        return name().toLowerCase().replace('_', '-');
    }
}
```

## Outlook

It seems obvious that this a temporary solution. Once the OpenAPI specs provide a relevant new feature for handling enumerations, it should be fairly simple to drop the usage of this plugin and replace all [@ApiEnum](src/main/java/de/pentabyte/springfox/ApiEnum.java) annotations with their future counterparts.
