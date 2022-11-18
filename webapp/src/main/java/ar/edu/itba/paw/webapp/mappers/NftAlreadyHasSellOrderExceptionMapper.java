package ar.edu.itba.paw.webapp.mappers;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import ar.edu.itba.paw.exceptions.NftAlreadyHasSellOrderException;
import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;

public class NftAlreadyHasSellOrderExceptionMapper implements ExceptionMapper<NftAlreadyHasSellOrderException> {

    @Override
    public Response toResponse(NftAlreadyHasSellOrderException e) {
        final ResponseErrorDto error = ResponseErrorDto.fromGenericException(e, 403);

        return Response.status(Response.Status.FORBIDDEN)
                .entity(new GenericEntity<ResponseErrorDto>(error){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}