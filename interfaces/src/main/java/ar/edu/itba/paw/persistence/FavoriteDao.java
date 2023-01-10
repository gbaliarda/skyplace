package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Favorited;
import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface FavoriteDao {
    void addNftFavorite(Nft nft, User user);

    void removeNftFavorite(Nft nft, User user);

    Optional<Favorited> userFavedNft(int userId, int idNft);

    int getNftFavorites(int productId);

    int getUserFavoritesAmount(int userId);
}
