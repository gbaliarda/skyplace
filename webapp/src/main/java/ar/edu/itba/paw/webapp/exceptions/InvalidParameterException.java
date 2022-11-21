package ar.edu.itba.paw.webapp.exceptions;

public class InvalidParameterException extends RuntimeException{

    private static final long serialVersionUID = -2402437139501496045L;
    private static final String MESSAGE = "Invalid parameter sent";
    private final String parameter;

    public InvalidParameterException(String parameter) {
        super(MESSAGE);
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
