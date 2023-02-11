package ar.edu.itba.paw.webapp.helpers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
}
