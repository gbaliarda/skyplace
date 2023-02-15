package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(final NotFoundException e) {
        final String errorMessage = messageSource.getMessage("NotFoundException.message", null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromClientErrorException(e, errorMessage);

        return ResponseHelpers.fromErrorDtoAndStatusCode(error, Response.Status.NOT_FOUND);
    }
}
