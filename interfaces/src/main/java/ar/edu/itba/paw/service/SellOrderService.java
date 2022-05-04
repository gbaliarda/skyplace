package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.SellOrder;

import java.math.BigDecimal;
import java.util.Optional;

public interface SellOrderService {

    Optional<SellOrder> create(BigDecimal price, String idNft, String category);

    Optional<SellOrder> getOrderById(long id);

    void update(long id, String category, BigDecimal price);

    void delete(long id);

    long getNftWithOrder(String id);

}
