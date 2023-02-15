package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(final ConstraintViolationException e) {
        final List<ErrorDto> errors = e.getConstraintViolations().stream()
                .map(ErrorDto::fromValidationException).collect(Collectors.toList());

        return ResponseHelpers.fromErrorsDtoAndStatusCode(errors, Response.Status.BAD_REQUEST);
    }
}
