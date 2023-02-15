package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.glassfish.jersey.server.ParamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PathParamExceptionMapper implements ExceptionMapper<ParamException.PathParamException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(ParamException.PathParamException e) {
        final ErrorDto error = ErrorDto.fromGenericException(e, 404, messageSource.getMessage("PathParamException.message", null, LocaleContextHolder.getLocale()));

        return ResponseHelpers.fromErrorDtoAndStatusCode(error, Response.Status.NOT_FOUND);
    }
}
