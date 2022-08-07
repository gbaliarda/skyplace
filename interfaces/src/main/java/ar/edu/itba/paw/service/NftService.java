package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface NftService {
    Nft create(int nftId, String contractAddr, String nftName, String chain, MultipartFile image, int idOwner, String collection, String description);

    Optional<Nft> getNFTById(int nftId);

    int getPageSize();

    List<Nft> getAll(int page, String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor);

    List<Publication> getAllPublications(int page, String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor);

    List<Publication> getAllPublicationsByUser(int page, User user, String publicationType, String sort);

    int getAmountPublications(String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor);

    int getAmountPublicationPagesByUser(User user, User currentUser, String publicationType);

    void delete(Nft nft);

    boolean isNftCreated(int nftId, String contractAddr, String chain);

    List<Publication> getRecommended(int productId);
}
