package ar.edu.itba.paw.webapp.helpers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.NftFiltersDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;

import javax.ws.rs.core.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHelpers {

    public static Response.ResponseBuilder addLinkAttributes(Response.ResponseBuilder responseBuilder, UriInfo uriInfo, int currentPage, int lastPage, Map<String, Object[]> params) {
        if (currentPage <= lastPage && currentPage > 1) {
            UriBuilder prevUriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("page", currentPage - 1);
            for (Map.Entry<String, Object[]> entry:params.entrySet())
                prevUriBuilder.queryParam(entry.getKey(), entry.getValue());
            responseBuilder.link(prevUriBuilder.build(), "prev");
        }

        if (currentPage < lastPage) {
            UriBuilder nextUriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("page", currentPage + 1);
            for (Map.Entry<String, Object[]> entry:params.entrySet())
                nextUriBuilder.queryParam(entry.getKey(), entry.getValue());
            responseBuilder.link(nextUriBuilder.build(), "next");
        }

        UriBuilder firstUriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("page", 1);
        UriBuilder lastUriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage);
        UriBuilder selfUriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("page", currentPage);
        for (Map.Entry<String, Object[]> entry:params.entrySet()) {
            firstUriBuilder.queryParam(entry.getKey(), entry.getValue());
            lastUriBuilder.queryParam(entry.getKey(), entry.getValue());
            selfUriBuilder.queryParam(entry.getKey(), entry.getValue());
        }

        return responseBuilder
                .link(firstUriBuilder.build(), "first")
                .link(lastUriBuilder.build(), "last")
                .link(selfUriBuilder.build(), "self");
    }

    public static Map<String, Object[]> buildQueryParams(NftFiltersDto nftFiltersDto) {
        Map<String, Object[]> queryParams = new HashMap<>();
        if (nftFiltersDto.getCategory() != null &&  nftFiltersDto.getCategory().size() > 0)
            queryParams.put("category", nftFiltersDto.getCategory().toArray());

        if (nftFiltersDto.getChain() != null &&  nftFiltersDto.getChain().size() > 0)
            queryParams.put("chain", nftFiltersDto.getChain().toArray());

        if (nftFiltersDto.getStatus() != null &&  nftFiltersDto.getStatus().size() > 0)
            queryParams.put("status", nftFiltersDto.getStatus().toArray());

        if (nftFiltersDto.getMinPrice() != null)
            queryParams.put("minPrice", new Object[]{nftFiltersDto.getMinPrice()});

        if (nftFiltersDto.getMaxPrice() != null)
            queryParams.put("maxPrice", new Object[]{nftFiltersDto.getMaxPrice()});

        if (nftFiltersDto.getSearch() != null)
            queryParams.put("search", new Object[]{nftFiltersDto.getSearch()});

        if (nftFiltersDto.getSearchFor() != null)
            queryParams.put("searchFor", new Object[]{nftFiltersDto.getSearchFor()});

        if (nftFiltersDto.getSort() != null)
            queryParams.put("sort", new Object[]{nftFiltersDto.getSort()});

        if (nftFiltersDto.getOwnerId() != null)
            queryParams.put("owner", new Object[]{nftFiltersDto.getOwnerId()});

        return queryParams;
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
