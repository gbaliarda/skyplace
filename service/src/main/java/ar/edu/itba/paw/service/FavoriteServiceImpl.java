package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.NftNotFoundException;
import ar.edu.itba.paw.model.Favorited;
import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.FavoriteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class FavoriteServiceImpl implements FavoriteService{
    private final FavoriteDao favoriteDao;
    private final NftService nftService;

    @Autowired
    public FavoriteServiceImpl(FavoriteDao favoriteDao, NftService nftService) {
        this.favoriteDao = favoriteDao;
        this.nftService = nftService;
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
    public boolean isNftFavedByUser(int userId, int idNft) {
        return userFavedNft(userId, idNft).isPresent();
    }

    @Override
    public Optional<Favorited> userFavedNft(int userId, int idNft) {
        return favoriteDao.userFavedNft(userId, idNft);
    }
}
