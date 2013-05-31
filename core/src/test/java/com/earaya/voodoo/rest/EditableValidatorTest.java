package com.earaya.voodoo.rest;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class EditableValidatorTest {

    private EditableValidator validator = new EditableValidator();

    @Test
    public void singleValidateTest() {
        CustomEditable editable = new CustomEditable("one");
        Map<String, String> map = new HashMap<>();
        map.put("one", "value");
        ImmutableList<String> errors = validator.validate(editable, map);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void multipleValuesTest() {
        CustomEditable editable = new CustomEditable("one", "two");
        Map<String, String> map = new HashMap<>();
        map.put("one", "value");
        map.put("two", "value");
        ImmutableList<String> errors = validator.validate(editable, map);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void multipleValidateTest() {
        CustomEditable editable = new CustomEditable("one", "two");
        Map<String, String> map = new HashMap<>();
        map.put("one", "value");
        ImmutableList<String> errors = validator.validate(editable, map);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void singleInvalidTest() {
        CustomEditable editable = new CustomEditable("one");
        Map<String, String> map = new HashMap<>();
        map.put("two", "value");
        ImmutableList<String> errors = validator.validate(editable, map);
        assertTrue(errors.contains("two not allowed"));
    }

    @Test
    public void multipleInvalidValuesTest() {
        CustomEditable editable = new CustomEditable("one", "two");
        Map<String, String> map = new HashMap<>();
        map.put("three", "value");
        map.put("four", "value");
        ImmutableList<String> errors = validator.validate(editable, map);
        assertTrue(errors.contains("three not allowed"));
        assertTrue(errors.contains("four not allowed"));
    }

    @Test
    public void multipleValidateInvalidTest() {
        CustomEditable editable = new CustomEditable("one", "two");
        Map<String, String> map = new HashMap<>();
        map.put("three", "value");
        ImmutableList<String> errors = validator.validate(editable, map);
        assertTrue(errors.contains("three not allowed"));
    }

    @Test
    public void testOtherObjects() {
        CustomEditable editable = new CustomEditable("one", "two");
        ImmutableList<String> errors = validator.validate(editable, "Invalid Object");
        assertTrue(errors.contains("only map types are supported"));
    }

    @Test
    public void testNullObject() {
        CustomEditable editable = new CustomEditable("one", "two");
        ImmutableList<String> errors = validator.validate(editable, null);
        assertTrue(errors.contains("request entity required"));
    }

    private static class CustomEditable implements Editable {

        private String[] fields;

        public CustomEditable(String... fields) {
            this.fields = fields;
        }

        @Override
        public String[] fields() {
            return this.fields;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Editable.class;
        }
    }
}
