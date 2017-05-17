package dev.ranggalabs.restapi.repository;

import dev.ranggalabs.enitity.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by erlangga on 4/25/2017.
 */
@Repository
public class BalanceRepositoryImpl implements BalanceRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public Balance findOneByAccountId(BigDecimal accountId) {
        String sql = "SELECT * FROM balance WHERE account_id=:accountId";
        List<Balance> balances = null;
        Connection con = sql2o.open();
        try {
            balances = con.createQuery(sql)
                    .addParameter("accountId",accountId)
                    .addColumnMapping("account_id","accountId")
                    .executeAndFetch(Balance.class);
            if(balances==null || balances.isEmpty()){
                return null;
            }
            return balances.get(0);
        }finally {
            con.close();
        }
    }

    @Override
    public void update(Balance balance, BigDecimal remainingAmount) {
        String sql = "UPDATE balance set amount=:remainingAmount, version=now() WHERE id=:id AND version=:version";
        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(sql)
            .addParameter("id", balance.getId())
            .addParameter("remainingAmount", remainingAmount)
            .addParameter("version", balance.getVersion())
            .executeUpdate();
            con.commit();

            if(con.getResult()<1){
                //System.out.println("Locking Exception Occured");
                throw new OptimisticLockingFailureException("Locking Exception Occured");
            }
        }
    }
}
