package ar.edu.itba.paw.webapp.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsEqualConstraint;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class FieldsEqualConstraintValidator implements
        ConstraintValidator<FieldsEqualConstraint, Object> {

    private String first;
    private String second;
    private String errorMessageTemplate;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(FieldsEqualConstraint arg0) {
        first = arg0.first();
        second = arg0.second();
        errorMessageTemplate = arg0.errorMessageTemplate();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext arg1) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(obj);
        Object f = wrapper.getPropertyValue(first);
        Object s = wrapper.getPropertyValue(second);

        boolean result = f != null && f.equals(s);
        if(!result) {
            arg1.disableDefaultConstraintViolation();
            arg1.buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }

        return result;
    }
}
