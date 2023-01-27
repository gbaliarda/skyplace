package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Favorited;
import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface FavoriteService {
    void addNftFavorite(int productId, User user);

    void removeNftFavorite(int productId, User user);

    int getNftFavorites(int productId);

    Optional<Favorited> userFavedNft(int userId, int nftId);

    boolean isNftFavedByUser(int userId, int nftId);

    int getUserFavoritesAmount(int userId);

    List<Nft> getFavedNftsFromUser(int page, User user, String sort, List<Integer> nftId);
}
