package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(final NotFoundException e) {
        final ResponseErrorDto error = ResponseErrorDto.fromGenericException(e, 404);

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new GenericEntity<ResponseErrorDto>(error){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
