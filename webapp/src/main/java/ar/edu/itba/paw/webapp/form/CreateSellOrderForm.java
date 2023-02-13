package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidCategoryConstraint;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateSellOrderForm {

    @Min(value=0L)
    @NotNull
    private int nftId;

    @ValidCategoryConstraint(errorMessageTemplate = "InvalidCategoryException.message")
    private String category;

    @Digits(integer=18, fraction=18)
    @DecimalMin(value="0.000000000000000001")
    @NotNull
    private BigDecimal price;

    public int getNftId() {
        return nftId;
    }

    public void setNftId(int nftId) {
        this.nftId = nftId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price == null ? BigDecimal.ZERO : price.stripTrailingZeros();
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
