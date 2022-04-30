package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface NftService {
    Optional<Nft> create(long nft_id, String contract_addr, String nft_name, String chain, MultipartFile image, long id_owner, String collection, String description, String[] properties);

    Optional<Nft> getNFTById(String nft_id);

    Optional<List<Publication>> getAllPublications(int page, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, User currentUser);

    Optional<List<Publication>> getAllPublicationsByUser(int page, User user, User currentUser, boolean onlyFaved, boolean onlyOnSale);

}
