package com.earaya.voodoo.sample;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created with IntelliJ IDEA.
 * SampleUser: earaya
 * Date: 3/8/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleUser {
    public SampleUser() {
    }

    @NotEmpty
    @JsonProperty
    public String name = "test";
}
