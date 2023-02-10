package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.service.FavoriteService;
import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.NftDto;
import ar.edu.itba.paw.webapp.exceptions.NoBodyException;
import ar.edu.itba.paw.webapp.form.CreateNftForm;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
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
            @QueryParam("status") final List<String> status,
            @QueryParam("chain") final List<String> chain,
            @QueryParam("category") final List<String> category,
            @QueryParam("minPrice") final BigDecimal minPrice,
            @QueryParam("maxPrice") final BigDecimal maxPrice,
            @QueryParam("sort") final String sort,
            @QueryParam("search") final String search,
            @QueryParam("searchFor") final String searchFor,
            @QueryParam("owner") final Integer ownerId
    ) {
        List<NftDto> nftList = nftService.getAll(page, status, category, chain, minPrice, maxPrice, sort, search, searchFor, ownerId)
                .stream().map(n -> NftDto.fromNft(uriInfo, n, favoriteService.getNftFavorites(n.getId()))).collect(Collectors.toList());

        int amountPublications = nftService.getAmountPublicationsByUser(status, category, chain, minPrice, maxPrice, sort, search, searchFor, ownerId);
        int amountPages = amountPublications == 0 ? 1 : (amountPublications + nftService.getPageSize() - 1) / nftService.getPageSize();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(nftList) {}).header("X-Total-Count", amountPublications).header("X-Total-Pages", amountPages);
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = (int) Math.ceil(nftService.getAmountPublications(status, null, chain, null, null, sort, search, searchFor) / (double) nftService.getPageSize());
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", page).build(), "self")
                .build();
    }

    @Consumes({ MediaType.MULTIPART_FORM_DATA, })
    @POST
    public Response createNft(@ModelAttribute @FormDataParam("model") FormDataBodyPart model,
                              @FormDataParam("image") InputStream imageInput) {
        if(model == null)
            throw new NoBodyException();
        if(!userService.getCurrentUser().isPresent())
            return Response.status(Response.Status.UNAUTHORIZED).build();
        int ownerId = userService.getCurrentUser().get().getId();
        try {
            model.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            CreateNftForm nftForm = model.getValueAs(CreateNftForm.class);
            byte[] image = IOUtils.toByteArray(imageInput);
            final Nft newNft = nftService.create(nftForm.getNftId(), nftForm.getContractAddr(), nftForm.getName(), nftForm.getChain(), image, ownerId, nftForm.getCollection(), nftForm.getDescription());
            // appends the new ID to the path of this route (/nfts)
            final URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(newNft.getId())).build();
            return Response.created(location).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
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

    @GET
    @Path("/{id}/recommendations")
    public Response listNftRecommendations(@PathParam("id") int id) {
        /*
        Optional<Nft> maybeNft = nftService.getNFTById(id);
        if(!maybeNft.isPresent()) {
            throw new NotFoundException("Nft not found");
        }
        */
        List<NftDto> recommended = nftService.getRecommended(id).stream().map(p -> NftDto.fromNft(uriInfo, p.getNft(), favoriteService.getNftFavorites(p.getNft().getId()))).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<NftDto>>(recommended){ }).header("X-Total-Count", recommended.size()).build();
    }

}
