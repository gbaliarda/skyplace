package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.StatusPurchase;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

public class PurchaseDto {
    private int id;
    private BigDecimal price;
    private Date buyDate;
    private StatusPurchase status;
    private String txHash;

    private URI self;
    private NftDto nft;         // Nft can be deleted or not -> Not deleted is URI, deleted is DeletedNftDto
    private URI buyer;
    private URI seller;

    private final static String USERS_URI_PREFIX = "users";
    private final static String PURCHASES_URI_PREFIX = "purchases";

    public static PurchaseDto fromPurchase(UriInfo uriInfo, Purchase purchase) {
        final PurchaseDto purchaseDto = new PurchaseDto();

        final UriBuilder purchasesUriBuilder = uriInfo.getBaseUriBuilder().path(PURCHASES_URI_PREFIX)
                .path(String.valueOf(purchase.getId()));

        final UriBuilder buyerUriBuilder = uriInfo.getBaseUriBuilder().path(USERS_URI_PREFIX)
                .path(String.valueOf(purchase.getBuyer().getId()));

        final UriBuilder sellerUriBuilder = uriInfo.getBaseUriBuilder().path(USERS_URI_PREFIX)
                .path(String.valueOf(purchase.getSeller().getId()));

        purchaseDto.id = purchase.getId();
        purchaseDto.price = purchase.getPrice();
        purchaseDto.buyDate = purchase.getBuyDate();
        purchaseDto.status = purchase.getStatus();
        purchaseDto.txHash = purchase.getTxHash();
        purchaseDto.nft = NftDto.fromNft(uriInfo, purchase.getNftsByIdNft());

        purchaseDto.buyer = buyerUriBuilder.build();
        purchaseDto.seller = sellerUriBuilder.build();
        purchaseDto.self = purchasesUriBuilder.build();

        return purchaseDto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public NftDto getNft() {
        return nft;
    }

    public void setNft(NftDto nft) {
        this.nft = nft;
    }

    public URI getBuyer() {
        return buyer;
    }

    public void setBuyer(URI buyer) {
        this.buyer = buyer;
    }

    public URI getSeller() {
        return seller;
    }

    public void setSeller(URI seller) {
        this.seller = seller;
    }

    public StatusPurchase getStatus() {
        return status;
    }

    public void setStatus(StatusPurchase status) {
        this.status = status;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

}
