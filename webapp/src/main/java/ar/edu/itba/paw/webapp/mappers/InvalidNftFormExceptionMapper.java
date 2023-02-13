package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import ar.edu.itba.paw.webapp.exceptions.InvalidNftFormException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
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
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(errors);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
