package com.earaya.voodoosample;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class User {
    public User() {
    }

    @NotEmpty
    @JsonProperty
    public String name = "test";
}
