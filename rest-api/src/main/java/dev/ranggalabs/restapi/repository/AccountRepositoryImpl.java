package dev.ranggalabs.restapi.repository;

import dev.ranggalabs.enitity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * Created by erlangga on 4/25/2017.
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @Autowired
    private Sql2o sql2o;

    @Override
    public Account findOneByCif(String cif) {
        String sql = "SELECT * FROM account WHERE cif=:cif";
        List<Account> accounts = null;

        Connection con = sql2o.open();
        try {
            accounts = con.createQuery(sql)
                    .addParameter("cif",cif)
                    .addColumnMapping("account_number","accountNumber")
                    .executeAndFetch(Account.class);
            if(accounts==null || accounts.isEmpty()){
                return null;
            }
            return accounts.get(0);
        }finally {
            con.close();
        }
    }
}
