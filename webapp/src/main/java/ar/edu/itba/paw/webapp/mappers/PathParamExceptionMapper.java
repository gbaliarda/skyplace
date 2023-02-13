package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import org.glassfish.jersey.server.ParamException;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class PathParamExceptionMapper implements ExceptionMapper<ParamException.PathParamException> {

    @Override
    public Response toResponse(ParamException.PathParamException e) {
        // TODO: Check if this response is thrown
        final ErrorDto error = ErrorDto.fromGenericException(e, 404, "ParamException.PathParamException");
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
