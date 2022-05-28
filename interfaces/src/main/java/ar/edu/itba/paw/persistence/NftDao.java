package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Chain;
import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface NftDao {
    Nft create(int nftId, String contractAddr, String nftName, Chain chain, MultipartFile image, User owner, String collection, String description);

    Optional<Nft> getNFTById(int nftId);

    List<Nft> getAllPublications(int page, int pageSize, String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor);

    List<Nft> getAllPublicationsByUser(int page, int pageSize, User user, boolean onlyFaved, boolean onlyOnSale, String sort);

    int getAmountPublications(String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor);

    int getAmountPublicationPagesByUser(int pageSize, User user, User currentUser, boolean onlyFaved, boolean onlyOnSale);

    void delete(int productId);

    boolean isNftCreated(int nftId, String contractAddr, String chain);
}
