package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface NftDao {
    Nft create(int nftId, String contractAddr, String nftName, Chain chain, int imageId, User owner, String collection, String description);

    Optional<Nft> getNFTById(int nftId);

    Optional<Nft> getNftByPk(int nftContractId, String contractAddr, String chain);

    List<Nft> getAllPublications(int page, int pageSize, String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor, Integer ownerId);

    List<Nft> getAllPublicationsByUser(int page, int pageSize, User user, boolean onlyFaved, boolean onlyOnSale, String sort);

    int getAmountPublications(String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor);

    int getAmountPublicationPagesByUser(int pageSize, User user, User currentUser, boolean onlyFaved, boolean onlyOnSale);

    boolean isNftCreated(int nftId, String contractAddr, String chain);

    Optional<Nft> getRandomNftFromCollection(int productId, String collection, int tableSize);

    Optional<Nft> getRandomNftFromCategory(int productId, Category category, int tableSize);

    Optional<Nft> getRandomNftFromOwner(int productId, User owner, int tableSize);

    Optional<Nft> getRandomNftFromChain(int productId, Chain chain, int tableSize);

    Optional<Nft> getRandomNftFromOtherBuyer(int productId, Nft nft, int currentUserId, int tableSize);

    Optional<Nft> getRandomNft(int productId, int tableSize);

    int[] getRandomNftTableSizes(Nft nft, int currentUserId);

    Optional<Favorited> isNftFavedByUser(int userId, int productId);
}
