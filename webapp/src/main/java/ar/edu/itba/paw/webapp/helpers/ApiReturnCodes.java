package ar.edu.itba.paw.webapp.helpers;

public enum ApiReturnCodes {
    EMPTY_PARAMETER("10"),
    INVALID_PARAMETER("11"),
    RESOURCE_ALREADY_EXISTS("12"),
    REQUEST_HAS_NO_BODY("13"),
    EXPIRED_JWT("14"),
    MALFORMED_JWT("15"),
    INVALID_USER_PASSWORD("21"),
    USER_IS_NOT_NFT_OWNER("22"),
    NFT_ALREADY_HAS_SELLORDER("31"),
    SELLORDER_ALREADY_HAS_PENDING_BUYORDER("41"),
    BUYORDER_IS_PENDING("42"),
    NO_TRANSACTION_BETWEEN_USERS("51");

    private final String code;

    ApiReturnCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
