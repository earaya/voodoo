package com.earaya.voodoo.rest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditableValidator {

    public ImmutableList<String> validate(Editable editable, Object o) {
        Set<String> errors = Sets.newHashSet();
        List<String> allowed = Arrays.asList(editable.fields());

        if (o == null) {
            errors.add("request entity required");
        } else if (o instanceof Map) {
            Map map = (Map) o;
            for (Object key : map.keySet()) {
                if (!allowed.contains(key.toString())){
                    errors.add(key.toString() + " not allowed");
                }
            }
        } else {
            errors.add("only map types are supported");
        }

        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }
}
