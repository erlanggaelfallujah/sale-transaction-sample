package dev.ranggalabs.restapi.repository;

import dev.ranggalabs.enitity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by erlangga on 4/25/2017.
 */
@Repository
public class CardRepositoryImpl implements CardRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public Card findOneByPrintNumber(String printNumber) {
        String sql = "SELECT * FROM card WHERE print_number=:printNumber";
        List<Card> cards = null;
        Connection con = sql2o.open();
        try {
            cards = con.createQuery(sql).addParameter("printNumber",printNumber).addColumnMapping("print_number","printNumber").executeAndFetch(Card.class);
            if(cards==null || cards.isEmpty()){
                return null;
            }
            return cards.get(0);
        }finally {
            con.close();
        }
    }

    @Override
    @Async
    public Card findOneByPrintNumberAsync(String printNumber) {
        CompletableFuture<Card> cardCompletableFuture = CompletableFuture.completedFuture(findOneByPrintNumber(printNumber));
        try {
            return cardCompletableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
