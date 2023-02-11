package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.form.CreateNftForm;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class InvalidNftFormException extends RuntimeException {

    private final Set<ConstraintViolation<CreateNftForm>> constraintViolations;

    public InvalidNftFormException(Set<ConstraintViolation<CreateNftForm>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public Set<ConstraintViolation<CreateNftForm>> getConstraintViolations() {
        return constraintViolations;
    }
}
