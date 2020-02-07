package de.pentabyte.springfox.model;

import com.fasterxml.jackson.annotation.JsonValue;
import de.pentabyte.springfox.ApiEnum;

public enum SomeEnumWithJsonValueAnnotation {
    @ApiEnum("A One")
    A_1,
    @ApiEnum("B Two")
    B_2;

    @JsonValue
    public String toJson() {
        return name().toLowerCase().replace('_', '-');
    }
}