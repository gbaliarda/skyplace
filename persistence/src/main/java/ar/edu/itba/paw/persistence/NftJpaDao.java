package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Repository
public class NftJpaDao implements NftDao {

    @PersistenceContext
    private EntityManager em;

    private static final String COUNT_ID_QUERY = "SELECT COUNT(nfts.id) " +
            "FROM nfts " +
            "LEFT OUTER JOIN sellorders ON nfts.id=sellorders.id_nft " +
            "JOIN users ON id_owner=users.id ";

    private static final String COUNT_FAVED_ID_QUERY = COUNT_ID_QUERY.concat(
            " LEFT OUTER JOIN favorited ON nfts.id=favorited.id_nft");

    private static final String SELECT_ID_QUERY = "SELECT nfts.id " +
                                                                "FROM nfts " +
                                                                "LEFT OUTER JOIN sellorders ON nfts.id=sellorders.id_nft " +
                                                                "JOIN users ON id_owner=users.id ";
    private static final String SELECT_FAVORITED_ID_QUERY = SELECT_ID_QUERY.concat(
                                        " LEFT OUTER JOIN favorited ON nfts.id=favorited.id_nft");

    private static final String SELECT_FAVED_NFT_IDS_IN_LIST = "SELECT n.id FROM nfts n INNER JOIN favorited f ON (f.id_nft = n.id) WHERE f.user_id = ?1 AND n.id IN ?2";

    /**
     * Creates a new NFT product with the provided data.
     * @param nftId id of the NFT inside its contract.
     * @param contractAddr Address of the smart contract that defines the NFT.
     * @param nftName Name of the NFT.
     * @param chain Chain in which the smart contract lives.
     * @param owner The user that created this NFT.
     * @param collection Name of the collection that this NFT belongs.
     * @param description Description of the NFT
     * @return New nft entity containing all the data
     */
    @Override
    public Nft create(int nftId, String contractAddr, String nftName, Chain chain, int imageId, User owner, String collection, String description) {
        final Nft nft = new Nft(nftId, contractAddr, nftName, chain, imageId, collection, description, owner);
        em.persist(nft);
        return nft;
    }

    @Override
    public Optional<Nft> getNFTById(int id) {
        Nft nft = em.find(Nft.class, id);
        return nft == null || nft.isDeleted() ? Optional.empty() : Optional.of(nft);
    }

    protected Pair<String, Pair<String,Object>> applyFilter(String columnNativeName, String paramName, List<String> filter) {
        if(filter == null || filter.isEmpty())
            return null;
        StringBuilder nativeQuery = new StringBuilder();
        Pair<String, Object> arg = null;
        int filterSize = filter.size();
        List<String> filters = new ArrayList<>();
        nativeQuery.append(" AND ( ");
        for (int i = 0; i < filterSize; i++) {
            nativeQuery.append(String.format(" %s LOWER(%s) LIKE LOWER(:%s) ", i == 0 ? "" : "OR", columnNativeName, paramName.concat(String.valueOf(i))));
            filters.add(filter.get(i));
            if (i==filterSize-1)
                arg = new Pair<>(paramName, filters);
        }

        nativeQuery.append(") ");
        return new Pair<>(nativeQuery.toString(), arg);
    }

    protected Pair<String,List<Pair<String,Object>>> buildFilterQuery(List<String> status, List<String> category, List<String> chain, BigDecimal minPrice, BigDecimal maxPrice, String search, String searchFor, Integer ownerId) {
        StringBuilder nativeQuery = new StringBuilder();
        List<Pair<String,Object>> args = new ArrayList<>();
        nativeQuery.append(" WHERE nfts.is_deleted = false ");

        if(status != null && !status.isEmpty()) {
            StringBuilder statusNativeAux = new StringBuilder();
            final int[] bothStatus = {0};
            status.forEach(s -> {
                if(s.equals("onSale") || s.equals("notSale")) {
                    statusNativeAux.append(String.format(" AND sellorders.id IS %s NULL ", s.equals("onSale") ? "NOT":""));
                    bothStatus[0]++;
                }
            });
            if(bothStatus[0] < 2) {
                nativeQuery.append(statusNativeAux);
            }
        }

        Pair<String,Pair<String,Object>> applyCategoryFilter = applyFilter("sellorders.category", "category", category);
        if(applyCategoryFilter != null) {
            nativeQuery.append(applyCategoryFilter.getLeft());
            args.add(applyCategoryFilter.getRight());
        }

        Pair<String,Pair<String,Object>> applyChainFilter = applyFilter("nfts.chain", "chain", chain);
        if(applyChainFilter != null) {
            nativeQuery.append(applyChainFilter.getLeft());
            args.add(applyChainFilter.getRight());
        }

        if(minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            nativeQuery.append(" AND sellorders.price >= :minPrice ");
            args.add(new Pair<>("minPrice", minPrice));
        }
        if(maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            nativeQuery.append(" AND sellorders.price <=  :maxPrice ");
            args.add(new Pair<>("maxPrice", maxPrice));
        }

        if(search != null && !search.equals("")) {
            if(searchFor != null && searchFor.equals("collection"))
                nativeQuery.append(" AND LOWER(nfts.collection) LIKE LOWER(:search) ");
            else
                nativeQuery.append(" AND LOWER(nfts.nft_name) LIKE '%'||LOWER(:search)||'%' ");
            args.add(new Pair<>("search", search));
        }

        if(ownerId != null) {
            nativeQuery.append(" AND nfts.id_owner = :owner_id ");
            args.add(new Pair<>("owner_id", ownerId));
        }

        return new Pair<>(nativeQuery.toString(), args);
    }

    /**
     * Retrieves a list of all NFTs in a specific page after aplying filters and being sorted.
     * If a certain filter or sort is empty or null, it is ignored.
     * @param page Number of page
     * @param pageSize Amount of NFTs that can a page have
     * @param status Status of the NFT, can be onSale or not.
     * @param category Name of the category that contains the NFTs, if this value is given, retrieves only on sale NFTs
     * @param chain Name of the chain that contains the NFTs
     * @param minPrice The minimum value that the NFT can be being sold for, if this value is given, retrieves only on sale NFTs
     * @param maxPrice The maximum value that the NFT can be being sold for, if this value is given, retrieves only on sale NFTs
     * @param sort Which type of sort to apply, if sort is priceAsc or priceDsc, then only on sale NFTs are retrieved.
     * @param search Name inside a certain NFT name or the exact name of a collection.
     * @param searchFor Clarifies whether the search is for collections or NFTs.
     * @param ownerId User id of the NFT Owner.
     * @return List of all NFTs that matches the conditions given in a specific page.
     */
    @Override
    public List<Nft> getAllPublications(int page, int pageSize, List<String> status, List<String> category, List<String> chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor, Integer ownerId) {
        Pair<String,List<Pair<String,Object>>> filterQuery = buildFilterQuery(status, category, chain, minPrice, maxPrice, search, searchFor, ownerId);
        return executeQueries(SELECT_ID_QUERY,filterQuery, pageSize, page, sort);
    }

    private Pair<String, List<Pair<String,Object>>> buildFilterQueryByUser(User user, boolean onlyFaved, boolean onlyOnSale) {
        StringBuilder nativeQuery = new StringBuilder();
        List<Pair<String,Object>> args = new ArrayList<>();
        nativeQuery.append(" WHERE nfts.is_deleted = false ");
        if(!onlyFaved) {
            nativeQuery.append(" AND nfts.id_owner=:idOwner ");
            args.add(new Pair<>("idOwner",user.getId()));
        } else {
            nativeQuery.append(" AND favorited.user_id=:idUser ");
            args.add(new Pair<>("idUser",user.getId()));
        }
        if(onlyOnSale)
            nativeQuery.append(" AND sellorders.id IS NOT NULL ");

        return new Pair<>(nativeQuery.toString(), args);
    }

    private Pair<String, String> applySort(String sort) {
        if(sort != null) {
            switch (sort) {
                case "priceAsc":
                    return new Pair<>(" ORDER BY sellorders.price ", " ORDER BY nft.sellorder.price ");
                case "priceDsc":
                    return new Pair<>(" ORDER BY sellorders.price DESC ", " ORDER BY nft.sellorder.price DESC ");
                case "noSort":
                    return new Pair<>(" ", " ");
                case "collection":
                    return new Pair<>(" ORDER BY nfts.collection ", " ORDER BY nft.collection ");
                default:
                    return new Pair<>(" ORDER BY nfts.nft_name ", " ORDER BY nft.nftName ");
            }
        }
        return new Pair<>(" ORDER BY nfts.nft_name ", " ORDER BY nft.nftName ");
    }

    /**
     * Retrieves a list of NFTs in a specific page for a certain user.
     * @param page Number of page
     * @param pageSize Amount of NFTs that can a page have
     * @param onlyFaved Whether the list should contain only faved NFTs by this user or not
     * @param onlyOnSale Whether the list should contain only on sale NFTs that this user owns.
     * @param sort Which type of sort to apply, if sort is priceAsc or priceDsc, then only on sale NFTs are retrieved.
     * @return List of all NFTs that matches the conditions given in a specific page.
     */
    @Override
    public List<Nft> getAllPublicationsByUser(int page, int pageSize, User user, boolean onlyFaved, boolean onlyOnSale, String sort) {
        Pair<String, List<Pair<String,Object>>> filterQuery = buildFilterQueryByUser(user, onlyFaved, onlyOnSale);
        return executeQueries(onlyFaved ? SELECT_FAVORITED_ID_QUERY:SELECT_ID_QUERY,filterQuery, pageSize, page, sort);
    }

    private List<Nft> executeQueries(String initalQuery,Pair<String, List<Pair<String,Object>>> filterQuery, int pageSize, int page, String sort) {
        String nativeQuery = filterQuery.getLeft();
        Pair<String,String> sorts = applySort(sort);
        List<Pair<String,Object>> args = filterQuery.getRight();

        if(page <= 0)
            page = 1;
        StringBuilder queryString = new StringBuilder(initalQuery);
        queryString.append(nativeQuery).append(sorts.getLeft()).append(" LIMIT :pageSize OFFSET :pageOffset");
        final Query idQuery = em.createNativeQuery(queryString.toString());
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("pageOffset", pageSize*(page-1));
        for(Pair<String,Object> arg:args) {
            if(arg.getLeft().equals("category") || arg.getLeft().equals("chain")) {
                @SuppressWarnings("unchecked")
                List<String> els = (List<String>) arg.getRight();
                for(int i = 0; i < els.size(); i++)
                    idQuery.setParameter(arg.getLeft().concat(String.valueOf(i)), els.get(i));
            } else
                idQuery.setParameter(arg.getLeft(), arg.getRight());
        }

        @SuppressWarnings("unchecked")
        final List<Integer> ids = (List<Integer>) idQuery.getResultList();

        if(ids.size() == 0)
            return Collections.emptyList();

        final TypedQuery<Nft> query = em.createQuery("FROM Nft AS nft WHERE nft.id IN :ids ".concat(sorts.getRight()), Nft.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    private Integer getIds(String initialQuery, Pair<String, List<Pair<String,Object>>> filterQuery) {
        String nativeQuery = filterQuery.getLeft();
        Pair<String, String> sorts = applySort("noSort");
        List<Pair<String,Object>> args = filterQuery.getRight();

        StringBuilder queryString = new StringBuilder(initialQuery).append(nativeQuery).append(sorts.getLeft());
        final Query countQuery = em.createNativeQuery(queryString.toString());
        for(Pair<String,Object> arg:args) {
            if(arg.getLeft().equals("category") || arg.getLeft().equals("chain")) {
                @SuppressWarnings("unchecked")
                List<String> els = (List<String>) arg.getRight();
                for(int i = 0; i < els.size(); i++)
                    countQuery.setParameter(arg.getLeft().concat(String.valueOf(i)), els.get(i));
            } else
                countQuery.setParameter(arg.getLeft(), arg.getRight());
        }

        BigInteger res = (BigInteger) countQuery.getSingleResult();
        if(res == null)
            return 0;
        return res.intValue();
    }

    /**
     * @return Amount of publications after aplying all filters.
     */
    @Override
    public int getAmountPublications(List<String> status, List<String> category, List<String> chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor) {
        Pair<String, List<Pair<String,Object>>> filterQuery = buildFilterQuery(status, category, chain, minPrice, maxPrice, search, searchFor, null);
        return getIds(COUNT_ID_QUERY, filterQuery);
    }

    /**
     * @return Amount of publications after aplying all filters.
     */
    @Override
    public int getAmountPublicationsByUser(List<String> status, List<String> category, List<String> chain, BigDecimal minPrice, BigDecimal maxPrice, String sort, String search, String searchFor, Integer ownerId) {
        Pair<String, List<Pair<String,Object>>> filterQuery = buildFilterQuery(status, category, chain, minPrice, maxPrice, search, searchFor, ownerId);
        return getIds(COUNT_ID_QUERY, filterQuery);
    }

    /**
     * @return Amount of pages of publications after aplying all filters.
     */
    @Override
    public int getAmountPublicationPagesByUser(int pageSize, User user, User currentUser, boolean onlyFaved, boolean onlyOnSale) {
        Pair<String, List<Pair<String,Object>>> filterQuery = buildFilterQueryByUser(user, onlyFaved, onlyOnSale);
        return (getIds(onlyFaved ? COUNT_FAVED_ID_QUERY:COUNT_ID_QUERY, filterQuery)-1)/pageSize+1;
    }

    @Override
    public Optional<Nft> getNftByPk(int nftContractId, String contractAddr, String chain) {
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.nftId = :nftId AND lower(nft.contractAddr) = lower(:contractAddr) AND nft.chain = :chain", Nft.class);
        query.setParameter("nftId", nftContractId);
        query.setParameter("contractAddr", contractAddr);
        query.setParameter("chain", Chain.valueOf(chain));
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean isNftCreated(int nftId, String contractAddr, String chain) {
        if(!Chain.getChains().contains(chain))
            return false;
        Optional<Nft> maybeNft = getNftByPk(nftId, contractAddr, chain);
        return maybeNft.isPresent() && !maybeNft.get().isDeleted();
    }

    /**
     * Get a random NFT which matches the collection of the productId given.
     * @param tableSize Amount of products that belong to the same collection of the given NFT.
     * @return Optional of a random NFT that belongs to the same collection of the given NFT.
     */
    @Override
    public Optional<Nft> getRandomNftFromCollection(int productId, String collection, int tableSize) {
        int randomIndex = (int) (Math.random()*tableSize);
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.collection=:collection AND nft.id<>:productId AND nft.isDeleted=false",Nft.class);
        query.setFirstResult(randomIndex);
        query.setMaxResults(1);
        query.setParameter("collection",collection);
        query.setParameter("productId",productId);
        return query.getResultList().stream().findFirst();
    }

    /**
     * Get a random NFT which matches the category of the NFT id given.
     * @param category Enum that contains all valid and existing categories that a NFT can have.
     * @param tableSize Amount of products that belong to the same category of the given NFT id.
     * @return Optional of a random NFT that belongs to the same category of the given NFT id.
     */
    @Override
    public Optional<Nft> getRandomNftFromCategory(int productId, Category category, int tableSize) {
        int randomIndex = (int) (Math.random()*tableSize);
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.sellorder.category=:category AND nft.id<>:productId AND nft.isDeleted = false",Nft.class);
        query.setFirstResult(randomIndex);
        query.setMaxResults(1);
        query.setParameter("category",category);
        query.setParameter("productId",productId);
        return query.getResultList().stream().findFirst();
    }

    /**
     * Get a random NFT also sold by the owner of the given NFT id.
     * @param tableSize Amount of products that belong to the same owner of the given NFT id.
     * @return Optional of a random NFT that belongs to the same owner of the given NFT id.
     */
    @Override
    public Optional<Nft> getRandomNftFromOwner(int productId, User owner, int tableSize) {
        int randomIndex = (int) (Math.random()*tableSize);
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.owner.id=:idOwner AND nft.id<>:productId AND nft.isDeleted = false ",Nft.class);
        query.setFirstResult(randomIndex);
        query.setMaxResults(1);
        query.setParameter("idOwner",owner.getId());
        query.setParameter("productId",productId);
        return query.getResultList().stream().findFirst();
    }

    /**
     * Get a random NFT that lives in the same chain as the given NFT id.
     * @param tableSize Amount of products that belong to the same chain of the given NFT id.
     * @return Optional of a random NFT that belongs to the same chain of the given NFT id.
     */
    @Override
    public Optional<Nft> getRandomNftFromChain(int productId, Chain chain, int tableSize) {
        int randomIndex = (int) (Math.random()*tableSize);
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.chain = :chain AND nft.id<>:productId AND nft.isDeleted = false",Nft.class);
        query.setFirstResult(randomIndex);
        query.setMaxResults(1);
        query.setParameter("chain",chain);
        query.setParameter("productId",productId);
        return query.getResultList().stream().findFirst();
    }

    /**
     * Get a random NFT that belongs to a different collection of the NFT id given but belongs to a collection
     * that another user bought from both, the given NFT id collection and this different collection.
     * @param tableSize Amount of products that matches this condition.
     * @return Optional of a random NFT that matches this condition.
     */
    @Override
    public Optional<Nft> getRandomNftFromOtherBuyer(int productId, Nft nft, int currentUserId, int tableSize) {
        int randomIndex = (int) (Math.random()*tableSize);
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.collection<>:collection AND nft.isDeleted = false AND nft.collection IN (SELECT DISTINCT purchase.nftsByIdNft.collection FROM Purchase purchase WHERE purchase.status=:successStatus AND purchase.buyer.id IN (SELECT purchase.buyer.id FROM Purchase purchase WHERE purchase.nftsByIdNft.collection=:collection AND purchase.status=:successStatus AND  purchase.buyer.id<>:currentUserId AND purchase.nftsByIdNft.id<>:productId))", Nft.class);
        query.setFirstResult(randomIndex);
        query.setMaxResults(1);
        query.setParameter("collection",nft.getCollection());
        query.setParameter("productId",productId);
        query.setParameter("currentUserId", currentUserId);
        query.setParameter("successStatus",StatusPurchase.SUCCESS);
        return query.getResultList().stream().findFirst();
    }

    /**
     * Get a complete random NFT different to the NFT id given.
     * @param tableSize Amount of NFTs
     * @return Optional of a complete random NFT.
     */
    @Override
    public Optional<Nft> getRandomNft(int productId, int tableSize) {
        int randomIndex = (int) (Math.random()*tableSize);
        final TypedQuery<Nft> query = em.createQuery("FROM Nft nft WHERE nft.id<>:productId AND nft.isDeleted = false",Nft.class);
        query.setFirstResult(randomIndex);
        query.setMaxResults(1);
        query.setParameter("productId",productId);
        return query.getResultList().stream().findFirst();
    }

    private final static String SELECT_ALL_RANDOM_TABLE_SIZE =
            "SELECT * FROM " +
                    "(SELECT COUNT(*) AS c1 FROM nfts WHERE collection=:collection AND id<>:productId AND nfts.is_deleted = false) AS c1 " +
                    "CROSS JOIN (SELECT COUNT(*) AS c2 FROM nfts JOIN sellorders ON nfts.id=sellorders.id_nft WHERE nfts.id<>:productId AND category=:category AND nfts.is_deleted = false) AS c2 " +
                    "CROSS JOIN (SELECT COUNT(*) AS c3 FROM nfts JOIN users ON id_owner=users.id WHERE nfts.id<>:productId AND id_owner=:idOwner AND nfts.is_deleted = false) AS c3 " +
                    "CROSS JOIN (SELECT COUNT(*) AS c4 FROM nfts WHERE collection<>:collection AND is_deleted = false AND collection IN (SELECT DISTINCT nfts.collection FROM purchases INNER JOIN nfts ON purchases.id_nft=nfts.id WHERE purchases.status=:successStatus AND purchases.id_buyer IN (SELECT purchases.id_buyer FROM purchases INNER JOIN nfts ON purchases.id_nft=nfts.id WHERE nfts.collection=:collection AND purchases.status=:successStatus AND  purchases.id_buyer<>:currentUserId AND nfts.id<>:productId))) AS c4 " +
                    "CROSS JOIN (SELECT COUNT(*) AS c5 FROM nfts WHERE nfts.id<>:productId AND chain=:chain AND nfts.is_deleted = false) AS c5  " +
                    "CROSS JOIN (SELECT COUNT(*) AS c6 FROM nfts WHERE id<>19 AND is_deleted=false) AS c6";


    /**
     * Retrieves an array containg all the amount of NFTs that matches each of the recommended conditions
     * @param nft NFT to use as a guideline for recommended NFTs.
     * @return Array containing each tableSize for each recommended condition.
     */
    @Override
    public int[] getRandomNftTableSizes(Nft nft, int currentUserId) {
        final Query query = em.createNativeQuery(SELECT_ALL_RANDOM_TABLE_SIZE);
        query.setParameter("productId", nft.getId());
        query.setParameter("collection",nft.getCollection());
        query.setParameter("category", nft.getSellOrder() != null ? nft.getSellOrder().getCategory().name() : "");
        query.setParameter("chain",nft.getChain().name());
        query.setParameter("idOwner",nft.getOwner().getId());
        query.setParameter("successStatus",StatusPurchase.SUCCESS.name());
        query.setParameter("currentUserId", currentUserId);
        Object[] out = (Object[]) query.getSingleResult();
        int outLength = out.length;
        int[] res = new int[outLength];
        for(int i = 0; i < outLength; i++)
            res[i] = ((BigInteger)out[i]).intValue();
        return res;
    }

    @Override
    public Optional<Favorited> isNftFavedByUser(int userId, int productId) {
        final TypedQuery<Favorited> query = em.createQuery("FROM Favorited f WHERE f.user.id = :userId AND f.nft.id = :productId",Favorited.class);
        query.setParameter("userId",userId);
        query.setParameter("productId",productId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Nft> getFavedNftsByUser(int userId, List<Integer> nftId) {
        final Query idQuery = em.createNativeQuery(SELECT_FAVED_NFT_IDS_IN_LIST);
        idQuery.setParameter(1,userId);
        idQuery.setParameter(2,nftId);

        @SuppressWarnings("unchecked")
        final List<Integer> ids = (List<Integer>) idQuery.getResultList();

        if(ids.size() == 0)
            return Collections.emptyList();

        final TypedQuery<Nft> query = em.createQuery("FROM Nft AS nft WHERE nft.id IN :ids ", Nft.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    protected static class Pair<T, U> {
        private final T left;
        private final U right;

        Pair(T left, U right) {
            this.left = left;
            this.right = right;
        }

        public T getLeft() {
            return left;
        }

        public U getRight() {
            return right;
        }
    }
}
