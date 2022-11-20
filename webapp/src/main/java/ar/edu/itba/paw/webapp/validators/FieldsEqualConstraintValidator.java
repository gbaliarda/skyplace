package ar.edu.itba.paw.webapp.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsEqualConstraint;
import org.springframework.beans.BeanWrapperImpl;

public class FieldsEqualConstraintValidator implements
        ConstraintValidator<FieldsEqualConstraint, Object> {

    private String first;
    private String second;

    @Override
    public void initialize(FieldsEqualConstraint arg0) {
        first = arg0.first();
        second = arg0.second();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext arg1) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(obj);
        Object f = wrapper.getPropertyValue(first);
        Object s = wrapper.getPropertyValue(second);
        return f != null && f.equals(s);
    }
}
