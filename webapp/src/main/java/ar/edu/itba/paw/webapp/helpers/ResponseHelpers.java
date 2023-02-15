package ar.edu.itba.paw.webapp.helpers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;

public class ResponseHelpers {

    public static Response.ResponseBuilder addLinkAttributes(Response.ResponseBuilder responseBuilder, UriInfo uriInfo, int currentPage, int lastPage) {
        if (currentPage <= lastPage && currentPage > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", currentPage - 1).build(), "prev");

        if (currentPage < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", currentPage + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", currentPage).build(), "self");
    }

    public static Response.ResponseBuilder addTotalCountHeader(Response.ResponseBuilder responseBuilder, int amountElements) {
        return responseBuilder.header("X-Total-Count", amountElements);
    }

    public static Response.ResponseBuilder addTotalPagesHeader(Response.ResponseBuilder responseBuilder, int amountPages) {
        return responseBuilder.header("X-Total-Pages", amountPages);
    }

    public static Response fromErrorDtoAndStatusCode(ErrorDto error, Response.Status status) {
        return fromErrorsDtoAndStatusCode(Collections.singletonList(error), status);
    }

    public static Response fromErrorsDtoAndStatusCode(List<ErrorDto> errors, Response.Status status) {
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(errors);

        return Response.status(status)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
