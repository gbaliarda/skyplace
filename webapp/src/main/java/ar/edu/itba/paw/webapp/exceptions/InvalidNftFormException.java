package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.form.CreateNftForm;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class InvalidNftFormException extends RuntimeException {

    private final Set<ConstraintViolation<CreateNftForm>> constraintViolations;
    private static final String MESSAGE = "InvalidNftFormException.message";

    public InvalidNftFormException(Set<ConstraintViolation<CreateNftForm>> constraintViolations) {
        super(MESSAGE);
        this.constraintViolations = constraintViolations;
    }

    public Set<ConstraintViolation<CreateNftForm>> getConstraintViolations() {
        return constraintViolations;
    }
}
