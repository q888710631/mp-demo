package com.honyee.app.config.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义valid
 */
public class EvenValidator implements ConstraintValidator<EvenNumber, Integer> {
    
    @Override
    public void initialize(EvenNumber constraintAnnotation) {
    
    }
    
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null || integer % 2 != 0) {
            return false;
        }
        return true;
    }
}