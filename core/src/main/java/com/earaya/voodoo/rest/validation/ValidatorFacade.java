package com.earaya.voodoo.rest.validation;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import javax.validation.*;
import javax.validation.groups.Default;
import java.util.Set;

import static java.lang.String.format;

/**
 * A simple façade for Hibernate Validator.
 */
public class ValidatorFacade {
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR;

    static {
        VALIDATOR = VALIDATOR_FACTORY.getValidator();
    }

    /**
     * Validates the given object, and returns a list of error messages, if any. If the returned
     * list is empty, the object is valid.
     *
     * @param o      a potentially-valid object
     * @param <T>    the type of object to validate
     * @return a list of error messages, if any, regarding {@code o}'s validity
     */
    public <T> ImmutableList<String> validate(T o) {
        return validate(o, Default.class);
    }

    /**
     * Validates the given object, and returns a list of error messages, if any. If the returned
     * list is empty, the object is valid.
     * @param o a potentially-valid object
     * @param groups group or list of groups targeted for validation (default to {@link javax.validation.groups.Default})
     * @param <T> the type of object to validate
     * @return a list of error messages, if any, regarding {@code o}'s validity
     */
    public <T> ImmutableList<String> validate(T o, Class<?>... groups) {
        final Set<String> errors = Sets.newHashSet();

        if (o == null) {
            errors.add("request entity required");
        }
        else {
            final Set<ConstraintViolation<T>> violations = VALIDATOR.validate(o,groups);
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
        }
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }
}
