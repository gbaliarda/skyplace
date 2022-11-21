package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException e) {
        final ErrorDto error = ErrorDto.fromClientErrorException(e);
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}