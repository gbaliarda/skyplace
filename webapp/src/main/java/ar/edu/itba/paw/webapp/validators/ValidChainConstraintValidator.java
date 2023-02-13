package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.model.Chain;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidChainConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChainConstraintValidator implements ConstraintValidator<ValidChainConstraint, String> {

    @Autowired
    private MessageSource messageSource;

    private String errorMessageTemplate;

    @Override
    public void initialize(ValidChainConstraint validChainConstraint) {
        errorMessageTemplate = validChainConstraint.errorMessageTemplate();
    }

    @Override
    public boolean isValid(String chain, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = Chain.getChains().contains(chain);

        if (!result) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }

        return result;
    }
}
