package com.earaya.voodoo.rest;

import com.earaya.voodoo.rest.exceptions.InvalidEntityException;
import com.earaya.voodoo.rest.validation.Editable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JacksonMessageBodyProviderValidationTest {

    public static class Person {
        @NotEmpty
        public String name;
        @Min(10)
        public int age;
    }

    private JacksonMessageBodyProvider jacksonMessageBodyProvider = new JacksonMessageBodyProvider(new ObjectMapper());

    @Test
    public void singleValidateTest() {
        CustomEditable editable = new CustomEditable(Person.class, "name");
        Map<String, String> map = new HashMap<>();
        map.put("name", "value");
        Object o = jacksonMessageBodyProvider.validate(new Annotation[] {editable}, map);
        assertEquals(map, o);
    }

    @Test(expected = InvalidEntityException.class)
    public void singleNotEditableTest() {
        CustomEditable editable = new CustomEditable(Person.class, "name");
        Map<String, Object> map = new HashMap<>();
        map.put("age", 11);
        jacksonMessageBodyProvider.validate(new Annotation[] {editable}, map);
        fail();
    }

    @Test(expected = InvalidEntityException.class)
    public void singleEditableValueFailsValidationTest() {
        CustomEditable editable = new CustomEditable(Person.class, "name");
        Map<String, String> map = new HashMap<>();
        map.put("name", null);
        jacksonMessageBodyProvider.validate(new Annotation[] {editable}, map);
        fail();
    }

    @Test
    public void multipleValuesTest() {
        CustomEditable editable = new CustomEditable(Person.class, "name", "age");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "value");
        map.put("age", 11);
        Object o = jacksonMessageBodyProvider.validate(new Annotation[] {editable}, map);
        assertEquals(map, o);
    }

    @Test(expected = InvalidEntityException.class)
    public void multipleNotEditableTest() {
        CustomEditable editable = new CustomEditable(Person.class);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "value");
        map.put("age", 11);
        jacksonMessageBodyProvider.validate(new Annotation[] {editable}, map);
        fail();
    }

    @Test(expected = InvalidEntityException.class)
    public void multipleInvalidValuesTest() {
        CustomEditable editable = new CustomEditable(Person.class, "name", "age");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "");
        map.put("age", 0);
        jacksonMessageBodyProvider.validate(new Annotation[] {editable}, map);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOtherObjectsNotSupported() {
        CustomEditable editable = new CustomEditable(Person.class, "one", "two");
        jacksonMessageBodyProvider.validate(new Annotation[] {editable}, "Invalid Object");
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullObject() {
        CustomEditable editable = new CustomEditable(Person.class, "one", "two");
        jacksonMessageBodyProvider.validate(new Annotation[] {editable}, null);
        fail();
    }

    private static class CustomEditable implements Editable {

        private String[] fields;
        private Class type;

        public CustomEditable(Class type, String... fields) {
            this.fields = fields;
            this.type = type;
        }

        @Override
        public String[] fields() {
            return this.fields;
        }

        @Override
        public Class type() {
            return this.type;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Editable.class;
        }
    }
}
