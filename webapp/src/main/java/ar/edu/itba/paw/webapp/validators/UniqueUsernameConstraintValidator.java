package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsernameConstraint, String> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    private String errorMessageTemplate;

    @Override
    public void initialize(UniqueUsernameConstraint uniqueUsernameConstraint) {
        errorMessageTemplate = uniqueUsernameConstraint.errorMessageTemplate();
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = !userService.getUserByUsername(username).isPresent();

        if (!result) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }

        return result;
    }
}
