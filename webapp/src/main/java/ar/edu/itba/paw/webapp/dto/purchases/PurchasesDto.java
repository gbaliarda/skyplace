package ar.edu.itba.paw.webapp.dto.purchases;
import java.util.List;

public class PurchasesDto {

    private int totalPages;
    private List<PurchaseDto> purchases;

    public static PurchasesDto fromPurchaseList(final List<PurchaseDto> purchases, final int totalPages) {
        final PurchasesDto dto = new PurchasesDto();

        dto.purchases = purchases;
        dto.totalPages = totalPages;

        return dto;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<PurchaseDto> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<PurchaseDto> purchases) {
        this.purchases = purchases;
    }
}
