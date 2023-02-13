package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueEmailConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmailConstraint, String> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    private String errorMessageTemplate;

    @Override
    public void initialize(UniqueEmailConstraint uniqueEmailConstraint) {
        errorMessageTemplate = uniqueEmailConstraint.errorMessageTemplate();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = !userService.getUserByEmail(email).isPresent();
        if (!result) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }
        return result;
    }
}
