package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.SellOrderNotFoundException;
import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class SellOrdeNotFoundExceptionMapper implements ExceptionMapper<SellOrderNotFoundException> {

    @Override
    public Response toResponse(SellOrderNotFoundException e) {
        final ResponseErrorDto error = ResponseErrorDto.fromGenericException(e, 404);

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new GenericEntity<ResponseErrorDto>(error){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
