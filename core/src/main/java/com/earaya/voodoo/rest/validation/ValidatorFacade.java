package com.earaya.voodoo.rest.validation;

import com.earaya.voodoo.rest.Editable;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import javax.validation.*;
import javax.validation.groups.Default;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * A simple facade for Hibernate Validator.
 */
public class ValidatorFacade {
    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator;

    static {
        validator = validatorFactory.getValidator();
    }

    /**
     * Validates the given object, and returns a list of error messages, if any. If the returned
     * list is empty, the object is valid.
     *
     * @param o   a potentially-valid object
     * @param <T> the type of object to validate
     * @return a list of error messages, if any, regarding {@code o}'s validity
     */
    public <T> ImmutableList<String> validate(T o) {
        return validate(o, Default.class);
    }

    /**
     * Validates the given object, and returns a list of error messages, if any. If the returned
     * list is empty, the object is valid.
     *
     * @param o      a potentially-valid object
     * @param groups group or list of groups targeted for validation (default to {@link javax.validation.groups.Default})
     * @param <T>    the type of object to validate
     * @return a list of error messages, if any, regarding {@code o}'s validity
     */
    public <T> ImmutableList<String> validate(T o, Class<?>... groups) {
        Set<String> errors = Sets.newHashSet();

        if (o == null) {
            errors.add("request entity required");
        } else {
            final Set<ConstraintViolation<T>> violations = validator.validate(o, groups);
            errors.addAll(parse(violations));
        }
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }

    public ImmutableList<String> validate(Object value, Editable editableAnnotation) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Only Map types are supported with @Editable");
        }

        final Set<String> errors = Sets.newHashSet();
        final List<String> editableFields = Arrays.asList(editableAnnotation.fields());
        Map map = (Map) value;
        for (Object key : map.keySet()) {
            if (!editableFields.contains(key.toString())) {
                errors.add(key.toString() + " not allowed");
            } else {
                errors.addAll(validateValue(editableAnnotation.type(), key.toString(), value.toString()));
            }
        }
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }

    private <T> ImmutableList<String> validateValue(Class<T> tClass, String attribute, String value) {
        Set<ConstraintViolation<T>> violations = validator.validateValue(tClass, attribute, value);
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(parse(violations)));
    }

    private <T> Set<String> parse(Set<ConstraintViolation<T>> violations) {
        final Set<String> errors = Sets.newHashSet();
        for (ConstraintViolation<T> v : violations) {
            if (v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod) {
                final ImmutableList<Path.Node> nodes = ImmutableList.copyOf(v.getPropertyPath());
                final ImmutableList<Path.Node> usefulNodes = nodes.subList(0, nodes.size() - 1);
                final String msg = v.getMessage().startsWith(".") ? "%s%s" : "%s %s";
                errors.add(format(msg, Joiner.on('.').join(usefulNodes), v.getMessage()).trim());
            } else {
                errors.add(format("%s %s (was %s)",
                        v.getPropertyPath(),
                        v.getMessage(),
                        v.getInvalidValue()));
            }
        }
        return errors;
    }
}
