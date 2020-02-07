package de.pentabyte.springfox.model;

import com.fasterxml.jackson.annotation.JsonValue;
import de.pentabyte.springfox.ApiEnum;

public enum SomeEnumWithJsonValueAnnotation {
    @ApiEnum("A One")
    A_1,
    @ApiEnum("B Two")
    B_2;

    @JsonValue
    /**
     * A_1 and B_2 will be mapped to A-1 and B-2.
     */
    public String toJson() {
        return name().toLowerCase().replace('_', '-');
    }
}