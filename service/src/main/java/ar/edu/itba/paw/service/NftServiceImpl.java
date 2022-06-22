package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.NftDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

@Service
public class NftServiceImpl implements NftService {
    private final NftDao nftDao;
    private final int pageSize = 12;
    private final UserService userService;
    private final MailingService mailingService;
    private final ImageService imageService;
    private final FavoriteService favoriteService;

    private final int RECOMMENDED_AMOUNT = 6;

    @Autowired
    public NftServiceImpl(NftDao nftDao, UserService userService, MailingService mailingService, ImageService imageService, FavoriteService favoriteService) {
        this.nftDao = nftDao;
        this.userService = userService;
        this.mailingService = mailingService;
        this.imageService = imageService;
        this.favoriteService = favoriteService;
    }

    @Transactional
    @Override
    public Nft create(int nftId, String contractAddr, String nftName, String chain, MultipartFile image, int idOwner, String collection, String description) {
        if(Arrays.stream(Chain.values()).noneMatch(e -> e.name().equals(chain)))
            throw new InvalidChainException();

        Nft newNft;
        Optional<Nft> maybeNft = nftDao.getNftByPk(nftId, contractAddr, chain);
        User owner = userService.getUserById(idOwner).orElseThrow(UserNotFoundException::new);

        if (maybeNft.isPresent()) {
            newNft = maybeNft.get();
            newNft.setOwner(owner);   // update owner
            newNft.setDeleted(false); // remove soft delete
        } else {
            newNft = nftDao.create(nftId, contractAddr, nftName, Chain.valueOf(chain), image, owner, collection, description);
        }
        Image nftImage = imageService.getImage(newNft.getIdImage()).orElseThrow(ImageNotFoundException::new);
        Locale locale = Locale.forLanguageTag(owner.getLocale());
        mailingService.sendNftCreatedMail(owner.getEmail(), owner.getUsername(), nftId, nftName, contractAddr, nftImage.getImage(), locale, newNft.getId());
        return newNft;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public Optional<Nft> getNFTById(int nftId) {
        return nftDao.getNFTById(nftId);
    }

    @Override
    public List<Publication> getAllPublications(int page, String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor) {
        List<Nft> nfts = nftDao.getAllPublications(page, pageSize, status, category, chain, minPrice, maxPrice, sort, search, searchFor);
        User currentUser = userService.getCurrentUser().orElse(null);
        return createPublicationsWithNfts(nfts, currentUser);
    }

    private List<Publication> createPublicationsWithNfts(List<Nft> nfts, User user) {
        List<Publication> publications = new ArrayList<>();
        for(Nft nft:nfts)
            if(user == null)
                publications.add(new Publication(nft, null));
            else
                publications.add(new Publication(nft, favoriteService.userFavedNft(user.getId(), nft.getId()).orElse(null)));
        return publications;
    }

    @Override
    public List<Publication> getAllPublicationsByUser(int page, User user, String publicationType, String sort) {
        User currentUser = userService.getCurrentUser().orElse(null);

        if (publicationType.equals("favorited") && (currentUser == null || currentUser.getId() != user.getId()))
            return Collections.emptyList();

        switch (publicationType) {
            case "favorited":
                List<Nft> favedNfts = nftDao.getAllPublicationsByUser(page, pageSize, user, true, false, sort);
                return createPublicationsWithNfts(favedNfts, currentUser);
            case "selling":
                List<Nft> sellingNfts = nftDao.getAllPublicationsByUser(page, pageSize, user, false, true, sort);
                return createPublicationsWithNfts(sellingNfts, currentUser);
            case "inventory":
                List<Nft> nfts = nftDao.getAllPublicationsByUser(page, pageSize, user, false, false, sort);
                return createPublicationsWithNfts(nfts, currentUser);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public int getAmountPublications(String status, String category, String chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor) {
        return nftDao.getAmountPublications(status, category, chain, minPrice, maxPrice, sort, search, searchFor);
    }

    @Override
    public int getAmountPublicationPagesByUser(User user, User currentUser, String publicationType) {
        switch (publicationType) {
            case "favorited":
                return nftDao.getAmountPublicationPagesByUser(pageSize, user, currentUser, true, false);
            case "selling":
                return nftDao.getAmountPublicationPagesByUser(pageSize, user, currentUser, false, true);
            case "inventory":
                return nftDao.getAmountPublicationPagesByUser(pageSize, user, currentUser, false, false);
            default:
                return 1;
        }
    }

    @Transactional
    @Override
    public void delete(Nft nft) {
        if (!userService.currentUserOwnsNft(nft.getId()) && !userService.isAdmin())
            throw new UserIsNotNftOwnerException();
        if (nft.getSellOrder() != null)
            throw new NftAlreadyHasSellOrderException();

        User owner = nft.getOwner();
        Image image = imageService.getImage(nft.getIdImage()).orElseThrow(ImageNotFoundException::new);
        nft.setDeleted(true); // soft delete
        Locale locale = Locale.forLanguageTag(owner.getLocale());
        mailingService.sendNftDeletedMail(owner.getEmail(), owner.getUsername(), nft.getNftId(), nft.getNftName(), nft.getContractAddr(), image.getImage(), locale);
    }

    @Override
    public boolean isNftCreated(int contractNftId, String contractAddr, String chain) {
        return nftDao.isNftCreated(contractNftId, contractAddr, chain);
    }

    private Optional<Nft> getRandomNftFromCollection(int productId, String collection, int tableSize) {
        return nftDao.getRandomNftFromCollection(productId, collection, tableSize);
    }

    private Optional<Nft> getRandomNftFromCategory(int productId, Category category, int tableSize) {
        return nftDao.getRandomNftFromCategory(productId, category, tableSize);
    }

    private Optional<Nft> getRandomNftFromOwner(int productId, User owner, int tableSize) {
        return nftDao.getRandomNftFromOwner(productId, owner, tableSize);
    }

    private Optional<Nft> getRandomNftFromChain(int productId, Chain chain, int tableSize) {
        return nftDao.getRandomNftFromChain(productId, chain, tableSize);
    }

    private Optional<Nft> getRandomNftFromOtherBuyer(int productId, Nft nft, int tableSize) {
        return nftDao.getRandomNftFromOtherBuyer(productId, nft, userService.getCurrentUser().map(User::getId).orElse(0),tableSize);
    }

    private Optional<Nft> getRandomNft(int productId, int tableSize) {
        return nftDao.getRandomNft(productId, tableSize);
    }

    private boolean nftAlreadyRecommended(int[] productIds, int productId) {
        return Arrays.stream(productIds).noneMatch(value -> value == productId);
    }

    private Publication createPublicationFromNft(Nft nft, Optional<User> user) {
        Favorited f = favoriteService.userFavedNft(user.map(User::getId).orElse(0), nft.getId()).orElse(null);
        return new Publication(nft, f);
    }

    @Override
    public List<Publication> getRecommended(int productId) {
        Optional<User> currentUser = userService.getCurrentUser();
        Nft nft = getNFTById(productId).orElseThrow(NftNotFoundException::new);
        List<Publication> recommended = new ArrayList<>();
        int[] productIds = new int[RECOMMENDED_AMOUNT];
        int[] randomSizes = nftDao.getRandomNftTableSizes(nft, currentUser.map(User::getId).orElse(0));
        for(int i = 0; i < RECOMMENDED_AMOUNT; i++) {
            RecommendedLabel randToString = RecommendedLabel.getRandomLabel();
            Optional<Nft> nftToAdd;
            switch(randToString) {
                case COLLECTION:
                    nftToAdd = getRandomNftFromCollection(productId, nft.getCollection(), randomSizes[0]);
                    if(nftToAdd.isPresent() && nftAlreadyRecommended(productIds,nftToAdd.get().getId())) {
                        recommended.add(createPublicationFromNft(nftToAdd.get(), currentUser));
                        productIds[i] = nftToAdd.get().getId();
                        break;
                    }
                case CATEGORY:
                    if(nft.getSellOrder() != null) {
                        nftToAdd = getRandomNftFromCategory(productId, nft.getSellOrder().getCategory(), randomSizes[1]);
                        if (nftToAdd.isPresent() && nftAlreadyRecommended(productIds, nftToAdd.get().getId())) {
                            recommended.add(createPublicationFromNft(nftToAdd.get(), currentUser));
                            productIds[i] = nftToAdd.get().getId();
                            break;
                        }
                    }
                case SELLER:
                    nftToAdd = getRandomNftFromOwner(productId, nft.getOwner(), randomSizes[2]);
                    if (nftToAdd.isPresent() && nftAlreadyRecommended(productIds, nftToAdd.get().getId())) {
                        recommended.add(createPublicationFromNft(nftToAdd.get(), currentUser));
                        productIds[i] = nftToAdd.get().getId();
                        break;
                    }
                case OTHER_BUYER:
                    nftToAdd = getRandomNftFromOtherBuyer(productId, nft, randomSizes[3]);
                    if (nftToAdd.isPresent() && nftAlreadyRecommended(productIds, nftToAdd.get().getId())) {
                        recommended.add(createPublicationFromNft(nftToAdd.get(), currentUser));
                        productIds[i] = nftToAdd.get().getId();
                        break;
                    }
                case CHAIN:
                    nftToAdd = getRandomNftFromChain(productId, nft.getChain(), randomSizes[4]);
                    if (nftToAdd.isPresent() && nftAlreadyRecommended(productIds, nftToAdd.get().getId())) {
                        recommended.add(createPublicationFromNft(nftToAdd.get(), currentUser));
                        productIds[i] = nftToAdd.get().getId();
                        break;
                    }
                default:
                    nftToAdd = getRandomNft(productId, randomSizes[5]);
                    if (nftToAdd.isPresent() && nftAlreadyRecommended(productIds, nftToAdd.get().getId())) {
                        recommended.add(createPublicationFromNft(nftToAdd.get(), currentUser));
                        productIds[i] = nftToAdd.get().getId();
                        break;
                    }
            }

        }

        return recommended;
    }


    private enum RecommendedLabel {
        COLLECTION,
        CATEGORY,
        OTHER_BUYER,
        SELLER,
        CHAIN,
        RANDOM;
        /*
            [0:50): nft de esa coleccion
            [50:70): nft de esa categoria
            [70:80): un comprador de esa coleccion, nfts random de otra coleccion que haya comprado
            [80:90): nft del mismo vendedor
            [90:98): nft de esa chain
            [98:100]: nft completamente random
         */

        public static RecommendedLabel getRandomLabel() {
            double rand = Math.random()*100;
            if(rand < 50)
                return COLLECTION;
            else if(rand < 70)
                return CATEGORY;
            else if(rand < 80)
                return OTHER_BUYER;
            else if(rand < 90)
                return SELLER;
            else if(rand < 98)
                return CHAIN;
            else
                return RANDOM;
        }

    }
}