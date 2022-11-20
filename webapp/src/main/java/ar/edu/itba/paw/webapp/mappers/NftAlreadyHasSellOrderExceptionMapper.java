package ar.edu.itba.paw.webapp.mappers;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import ar.edu.itba.paw.exceptions.NftAlreadyHasSellOrderException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;

import java.util.Collections;

public class NftAlreadyHasSellOrderExceptionMapper implements ExceptionMapper<NftAlreadyHasSellOrderException> {

    @Override
    public Response toResponse(NftAlreadyHasSellOrderException e) {
        final ErrorDto error = ErrorDto.fromGenericException(e, 403, "31");
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));


        return Response.status(Response.Status.FORBIDDEN)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}