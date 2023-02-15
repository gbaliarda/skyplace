package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.exceptions.InvalidNftFormException;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class InvalidNftFormExceptionMapper implements ExceptionMapper<InvalidNftFormException> {

    @Override
    public Response toResponse(InvalidNftFormException e) {
        final List<ErrorDto> errors = e.getConstraintViolations().stream().map(ErrorDto::fromValidationException).collect(Collectors.toList());

        return ResponseHelpers.fromErrorsDtoAndStatusCode(errors, Response.Status.BAD_REQUEST);
    }
}
