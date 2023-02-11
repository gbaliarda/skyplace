package ar.edu.itba.paw.webapp.helpers;

import ar.edu.itba.paw.webapp.exceptions.InvalidNftFormException;
import ar.edu.itba.paw.webapp.form.CreateNftForm;

import javax.validation.*;
import java.util.Set;

public class NftFormValidator {

    public static void validateForm(CreateNftForm form) throws InvalidNftFormException, ValidationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateNftForm>> violations = validator.validate(form);
        if(!violations.isEmpty()){
            throw new InvalidNftFormException(violations);
        }
    }

}
