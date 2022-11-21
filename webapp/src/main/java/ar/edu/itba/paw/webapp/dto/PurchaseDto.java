package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.StatusPurchase;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

public class PurchaseDto {
    private BigDecimal price;
    private Date buyDate;
    private NftDto nft;
    private UserDto buyer;
    private UserDto seller;
    private StatusPurchase status;
    private String txHash;

    private URI self;

    public static PurchaseDto fromPurchase(UriInfo uriInfo, Purchase purchase) {
        final PurchaseDto purchaseDto = new PurchaseDto();

        final UriBuilder purchasesUriBuilder = uriInfo.getAbsolutePathBuilder().replacePath("purchases")
                .path(String.valueOf(purchase.getId()));

        purchaseDto.price = purchase.getPrice();
        purchaseDto.buyDate = purchase.getBuyDate();
        purchaseDto.nft = NftDto.fromNft(uriInfo, purchase.getNftsByIdNft());
        purchaseDto.buyer = UserDto.fromUser(uriInfo, purchase.getBuyer());
        purchaseDto.seller = UserDto.fromUser(uriInfo, purchase.getSeller());
        purchaseDto.status = purchase.getStatus();
        purchaseDto.txHash = purchase.getTxHash();

        purchaseDto.self = purchasesUriBuilder.build();

        return purchaseDto;
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

    public UserDto getBuyer() {
        return buyer;
    }

    public void setBuyer(UserDto buyer) {
        this.buyer = buyer;
    }

    public UserDto getSeller() {
        return seller;
    }

    public void setSeller(UserDto seller) {
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
