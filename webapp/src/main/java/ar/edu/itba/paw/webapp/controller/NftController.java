package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.FavoriteService;
import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.NftDto;
import ar.edu.itba.paw.webapp.form.CreateNftForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("nfts") // base URL for all endpoints in this file
@Component
public class NftController {

    private final NftService nftService;
    private final UserService userService;
    private final FavoriteService favoriteService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public NftController(final NftService nftService, final UserService userService, final FavoriteService favoriteService) {
        this.nftService = nftService;
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    // GET /nfts
    @GET
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response listNfts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("status") final String status,
            @QueryParam("category") final String category,
            @QueryParam("chain") final String chain,
            @QueryParam("minPrice") final BigDecimal minPrice,
            @QueryParam("maxPrice") final BigDecimal maxPrice,
            @QueryParam("sort") final String sort,
            @QueryParam("search") final String search,
            @QueryParam("searchFor") final String searchFor
    ) {
        List<NftDto> nftList = nftService.getAll(page, status, category, chain, minPrice, maxPrice, sort, search, searchFor)
                .stream().map(n -> NftDto.fromNft(uriInfo, n)).collect(Collectors.toList());

        if (nftList.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(nftList) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = (int) Math.ceil(nftService.getAmountPublications(status, category, chain, minPrice, maxPrice, sort, search, searchFor) / (double) nftService.getPageSize());
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .build();
    }

    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    @POST
    public Response createNft(@Valid final CreateNftForm nftForm) {
        if(nftForm == null)
            throw new BadRequestException("Request must contain a body");
        int ownerId = userService.getCurrentUser().get().getId();
        final Nft newNft = nftService.create(nftForm.getNftId(), nftForm.getContractAddr(), nftForm.getName(), nftForm.getChain(), nftForm.getImage(), ownerId, nftForm.getCollection(), nftForm.getDescription());
        // appends the new ID to the path of this route (/nfts)
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newNft.getId())).build();
        return Response.created(location).build();
    }

    // GET /nfts/{id}
    @GET
    @Path("/{id}")
    public Response getNft(@PathParam("id") int id) {
        Optional<NftDto> maybeNft = nftService.getNFTById(id).map(n -> NftDto.fromNft(uriInfo, n));
        if (maybeNft.isPresent()) {
            return Response.ok(maybeNft.get()).build();
        }
        throw new NotFoundException("Nft not found");
    }

    @DELETE
    @Path("/{id}")
    public Response deleteNft(@PathParam("id") int id) {
        Optional<Nft> maybeNft = nftService.getNFTById(id);
        maybeNft.ifPresent(nftService::delete);
        return Response.noContent().build();
    }

    @GET
    @Path("/favorites")
    public Response listUserFavorites(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("sort") final String sort
    ){
        User user = userService.getCurrentUser().get();

        List<NftDto> userFavorites = nftService.getAllPublicationsByUser(page, user, "favorited", sort)
                .stream().map(Publication::getNft).map(n -> NftDto.fromNft(uriInfo, n)).collect(Collectors.toList());
        if (userFavorites.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(userFavorites) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = nftService.getAmountPublicationPagesByUser(user, user, "favorited");
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .build();
    }

    @PUT
    @Path("/{id}/favorite")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response addUserFavorite(
            @PathParam("id") int nftId
    ) {
        Optional<NftDto> maybeNft = nftService.getNFTById(nftId).map(n -> NftDto.fromNft(uriInfo, n));
        if (!maybeNft.isPresent()) {
            throw new NotFoundException("Nft not found");
        }
        User currentUser = userService.getCurrentUser().get();

        favoriteService.addNftFavorite(nftId, currentUser);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/favorite")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response removeUserFavorite(
            @PathParam("id") int nftId
    ) {
        Optional<NftDto> maybeNft = nftService.getNFTById(nftId).map(n -> NftDto.fromNft(uriInfo, n));
        if (!maybeNft.isPresent()) {
            throw new NotFoundException("Nft not found");
        }
        User user = userService.getCurrentUser().get();

        favoriteService.removeNftFavorite(nftId, user);
        return Response.noContent().build();
    }

}
