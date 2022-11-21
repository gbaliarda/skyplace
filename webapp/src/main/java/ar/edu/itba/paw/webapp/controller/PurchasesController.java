package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.UserNoPermissionException;
import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.PurchaseService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.PurchaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("purchases")
@Component
public class PurchasesController {

    private final UserService userService;
    private final PurchaseService purchaseService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public PurchasesController(UserService userService, PurchaseService purchaseService) {
        this.userService = userService;
        this.purchaseService = purchaseService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getHistoryTransactions( @QueryParam("page") @DefaultValue("1") int page){
        Optional<User> currentUser = userService.getCurrentUser();

        long amountPurchasesPages;
        amountPurchasesPages = purchaseService.getAmountPagesByUserId(currentUser.get().getId());

        if(page > amountPurchasesPages || page < 0 ) {
            throw new NotFoundException();
        }

        List<PurchaseDto> historyPurchases;
        historyPurchases = purchaseService.getAllTransactions(currentUser.get().getId(), page).stream().map(n -> PurchaseDto.fromPurchase(uriInfo, n)).collect(Collectors.toList());

        if(historyPurchases.isEmpty()){
            return Response.noContent().build();
        }
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<PurchaseDto>>(historyPurchases) {});

        //TODO REUSE THIS CODE
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        if (page < amountPurchasesPages)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");
        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", amountPurchasesPages).build(), "last")
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getHistoryTransactionById(@PathParam("id") int purchaseId){
        Optional<User> currentUser = userService.getCurrentUser();

        Optional<PurchaseDto> maybePurchase = purchaseService.getPurchaseById(currentUser.get().getId(), purchaseId).map(n -> PurchaseDto.fromPurchase(uriInfo, n));
        if (!maybePurchase.isPresent()) {
            throw new UserNoPermissionException();
        }
        return Response.ok(maybePurchase.get()).build();
    }

}
