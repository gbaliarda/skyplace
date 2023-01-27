package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.NftNotFoundException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.Favorited;
import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.FavoriteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class FavoriteServiceImpl implements FavoriteService{
    private final FavoriteDao favoriteDao;
    private final NftService nftService;
    private final UserService userService;

    @Autowired
    public FavoriteServiceImpl(FavoriteDao favoriteDao, NftService nftService, UserService userService) {
        this.favoriteDao = favoriteDao;
        this.nftService = nftService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public void addNftFavorite(int productId, User user) {
        if(!isNftFavedByUser(user.getId(), productId)) {
            Nft nft = nftService.getNFTById(productId).orElseThrow(NftNotFoundException::new);
            favoriteDao.addNftFavorite(nft, user);
        }
    }

    @Transactional
    @Override
    public void removeNftFavorite(int productId, User user) {
        if(isNftFavedByUser(user.getId(), productId)) {
            Nft nft = nftService.getNFTById(productId).orElseThrow(NftNotFoundException::new);
            favoriteDao.removeNftFavorite(nft, user);
        }
    }

    @Override
    public int getNftFavorites(int productId) {
        return favoriteDao.getNftFavorites(productId);
    }

    @Override
    public int getUserFavoritesAmount(int userId) {
        User maybeUser = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        return favoriteDao.getUserFavoritesAmount(maybeUser.getId());
    }

    @Override
    public List<Nft> getFavedNftsFromUser(int page, User user, String sort, List<Integer> nftId) {
        if (nftId.isEmpty())
            return nftService.getAllPublicationsByUser(page, user, "favorited", sort).stream().map(Publication::getNft).collect(Collectors.toList());
        return nftService.getFavedNftsByUser(user, nftId);
    }

    @Override
    public boolean isNftFavedByUser(int userId, int idNft) {
        return userFavedNft(userId, idNft).isPresent();
    }

    @Override
    public Optional<Favorited> userFavedNft(int userId, int idNft) {
        return favoriteDao.userFavedNft(userId, idNft);
    }
}
