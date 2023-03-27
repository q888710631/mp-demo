package com.honyee.app.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidator {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    private BeanValidator() {}

    /**
     * 校验对象
     */
    public static String validate(Object t, Class<?>... groups) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> validateResult = validator.validate(t, groups);
        if (validateResult.isEmpty()) {
            return "";
        }

        StringBuilder errors = new StringBuilder();
        for (ConstraintViolation<Object> violation : validateResult) {
            errors.append(violation.getMessage()).append(",");
        }
        if (errors.length() > 0) {
            return errors.substring(0, errors.length() - 1);
        }
        return errors.toString();
    }
}
