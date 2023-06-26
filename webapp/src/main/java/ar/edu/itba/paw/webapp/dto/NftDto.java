package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Nft;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class NftDto {

    private Integer id;
    private int nftId;
    private String contractAddr;
    private String nftName;
    private String collection;
    private String description;
    private String chain;
    private Integer favorites;

    // hyperlinks to itself and to other entities
    private URI self;
    private URI image;
    private URI owner;
    private URI sellorder;
    private URI recommendations;

    // returns the DTO representation of the Nft
    public static NftDto fromNft(final UriInfo uriInfo, final Nft nft, final Integer nftFavorites) {
        final NftDto dto = new NftDto();

        final UriBuilder nftUriBuilder = uriInfo.getBaseUriBuilder().path("nfts")
                .path(String.valueOf(nft.getId()));
        final UriBuilder userUriBuilder = uriInfo.getBaseUriBuilder().path("users")
                .path(String.valueOf(nft.getOwner().getId()));
        final UriBuilder imageUriBuilder = uriInfo.getBaseUriBuilder().path("images")
                .path(String.valueOf(nft.getIdImage()));
        final UriBuilder recommendationsUriBuilder = uriInfo.getBaseUriBuilder().path("nfts")
                .path(String.valueOf(nft.getId())).path("recommendations");

        if(!nft.isDeleted()) {
            dto.self = nftUriBuilder.build();
            dto.id = nft.getId();
            dto.favorites = nftFavorites;
            dto.owner = userUriBuilder.build();
            dto.recommendations = recommendationsUriBuilder.build();

            if (nft.getSellOrder() != null) {
                final UriBuilder sellorderUriBuilder = uriInfo.getBaseUriBuilder().path("sellorders")
                        .path(String.valueOf(nft.getSellOrder().getId()));
                dto.sellorder = sellorderUriBuilder.build();
            }
        }

        dto.image = imageUriBuilder.build();

        dto.collection = nft.getCollection();
        dto.contractAddr = nft.getContractAddr();
        dto.description = nft.getDescription();
        dto.chain = nft.getChain().name();
        dto.nftId = nft.getNftId();
        dto.nftName = nft.getNftName();

        return dto;
    }

    public static NftDto fromNft(final UriInfo uriInfo, final Nft nft) {
        return fromNft(uriInfo, nft, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNftId() {
        return nftId;
    }

    public void setNftId(int nftId) {
        this.nftId = nftId;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public String getNftName() {
        return nftName;
    }

    public void setNftName(String nftName) {
        this.nftName = nftName;
    }

    public URI getImage() {
        return image;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getSellorder() {
        return sellorder;
    }

    public void setSellorder(URI sellorder) {
        this.sellorder = sellorder;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public URI getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(URI recommendations) {
        this.recommendations = recommendations;
    }
}
