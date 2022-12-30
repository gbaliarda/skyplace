package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.service.FavoriteService;
import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.nfts.NftDto;
import ar.edu.itba.paw.webapp.dto.nfts.NftsDto;
import ar.edu.itba.paw.webapp.exceptions.NoBodyException;
import ar.edu.itba.paw.webapp.form.CreateNftForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
            @QueryParam("chain") final String chain,
            @QueryParam("sort") final String sort,
            @QueryParam("search") final String search,
            @QueryParam("searchFor") final String searchFor,
            @QueryParam("owner") final Integer ownerId
    ) {
        List<NftDto> nftList = nftService.getAll(page, status, null, chain, null, null, sort, search, searchFor, ownerId)
                .stream().map(n -> NftDto.fromNft(uriInfo, n, favoriteService.getNftFavorites(n.getId()))).collect(Collectors.toList());

        NftsDto nfts = NftsDto.fromNftList(nftList,
                nftService.getAmountPublicationsByUser(status, null, chain, null, null, sort, search, searchFor, ownerId));

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<NftsDto>(nfts) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = (int) Math.ceil(nftService.getAmountPublications(status, null, chain, null, null, sort, search, searchFor) / (double) nftService.getPageSize());
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
            throw new NoBodyException();
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
        Optional<NftDto> maybeNft = nftService.getNFTById(id).map(n -> NftDto.fromNft(uriInfo, n, favoriteService.getNftFavorites(n.getId())));
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

}
