package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.FavoriteService;
import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.NftFiltersDto;
import ar.edu.itba.paw.webapp.dto.NftDto;
import ar.edu.itba.paw.webapp.exceptions.FileSentNotAnImageException;
import ar.edu.itba.paw.webapp.exceptions.NoBodyException;
import ar.edu.itba.paw.webapp.form.CreateNftForm;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/nfts")
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

    @GET
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response listNfts(@QueryParam("page") @DefaultValue("1") final int page, final @BeanParam NftFiltersDto nftFiltersDto) {
        List<NftDto> nftList = nftService.getAll(page, nftFiltersDto.getStatus(), nftFiltersDto.getCategory(), nftFiltersDto.getChain(), nftFiltersDto.getMinPrice(), nftFiltersDto.getMaxPrice(), nftFiltersDto.getSort(), nftFiltersDto.getSearch(), nftFiltersDto.getSearchFor(), nftFiltersDto.getOwnerId())
                .stream().map(n -> NftDto.fromNft(uriInfo, n, favoriteService.getNftFavorites(n.getId()))).collect(Collectors.toList());

        int amountPublications = nftService.getAmountPublicationsByUser(nftFiltersDto.getStatus(), nftFiltersDto.getCategory(), nftFiltersDto.getChain(), nftFiltersDto.getMinPrice(), nftFiltersDto.getMaxPrice(), nftFiltersDto.getSort(), nftFiltersDto.getSearch(), nftFiltersDto.getSearchFor(), nftFiltersDto.getOwnerId());
        int amountPages = nftService.getAmountPages(amountPublications);

        Map<String, Object[]> queryParams = ResponseHelpers.buildQueryParams(nftFiltersDto);

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(nftList) {});
        ResponseHelpers.addTotalPagesHeader(responseBuilder, amountPages);
        ResponseHelpers.addTotalCountHeader(responseBuilder, amountPublications);
        return ResponseHelpers.addLinkAttributes(responseBuilder, uriInfo, page, amountPages, queryParams).build();
    }

    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON })
    @POST
    public Response createNft(@FormDataParam("image") InputStream imageInput,
                              @Valid @FormDataParam("model") CreateNftForm nftForm) {
        if(nftForm == null)
            throw new NoBodyException();
        Optional<User> currentUser = userService.getCurrentUser();
        if(!currentUser.isPresent())
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            Tika tika = new Tika();
            byte[] image = IOUtils.toByteArray(imageInput);
            // TODO: Check if this image checking should be done elsewhere
            String mimeType = tika.detect(image);
            if (!mimeType.startsWith("image/"))
                throw new FileSentNotAnImageException();            // File sent is not an image exception
            final Nft newNft = nftService.create(nftForm.getNftId(), nftForm.getContractAddr(), nftForm.getName(), nftForm.getChain(), image, currentUser.get().getId(), nftForm.getCollection(), nftForm.getDescription());

            final URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(newNft.getId())).build();
            return Response.created(location).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getNft(@PathParam("id") int id) {
        Optional<NftDto> maybeNft = nftService.getNFTById(id).map(n -> NftDto.fromNft(uriInfo, n, favoriteService.getNftFavorites(n.getId())));
        if (maybeNft.isPresent())
            return Response.ok(maybeNft.get()).build();
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
        Optional<Nft> maybeNft = nftService.getNFTById(id);
        if(!maybeNft.isPresent())
            throw new NotFoundException("Nft not found");

        List<NftDto> recommended = nftService.getRecommended(id).stream().map(p -> NftDto.fromNft(uriInfo, p.getNft(), favoriteService.getNftFavorites(p.getNft().getId()))).collect(Collectors.toList());
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(recommended){ });
        return ResponseHelpers.addTotalCountHeader(responseBuilder, recommended.size()).build();
    }

}
