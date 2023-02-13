package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCategoryConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCategoryConstraintValidator implements ConstraintValidator<ValidCategoryConstraint, String> {

    @Autowired
    private MessageSource messageSource;

    private String errorMessageTemplate;

    @Override
    public void initialize(ValidCategoryConstraint validCategoryConstraint) {
        errorMessageTemplate = validCategoryConstraint.errorMessageTemplate();
    }

    @Override
    public boolean isValid(String category, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = Category.getCategories().contains(category);

        if (!result) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }

        return result;
    }
}
