package ar.edu.itba.paw.webapp.helpers;

public enum UriPrefix {
    BUYORDERS_PREFIX("buyorders"),
    IMAGES_PREFIX("images"),
    NFTS_PREFIX("nfts"),
    PURCHASES_PREFIX("purchases"),
    REVIEWS_PREFIX("reviews"),
    USERS_PREFIX("users"),
    SELLORDERS_PREFIX("sellorders");

    private final String name;
    private final String url;

    UriPrefix(String name) {
        this.name = name;
        this.url = "/api/" + name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
