package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.List;

public class NftFiltersDto {

    @QueryParam("status")
    private List<String> status;

    @QueryParam("chain")
    private List<String> chain;

    @QueryParam("category")
    private List<String> category;

    @QueryParam("minPrice")
    private BigDecimal minPrice;

    @QueryParam("maxPrice")
    private BigDecimal maxPrice;

    @QueryParam("sort")
    private String sort;

    @QueryParam("search")
    private String search;

    @QueryParam("searchFor")
    private String searchFor;

    @QueryParam("owner")
    private Integer ownerId;

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getChain() {
        return chain;
    }

    public void setChain(List<String> chain) {
        this.chain = chain;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearchFor() {
        return searchFor;
    }

    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
}
