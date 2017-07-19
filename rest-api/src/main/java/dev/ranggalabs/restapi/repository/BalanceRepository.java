package dev.ranggalabs.restapi.repository;

import dev.ranggalabs.enitity.Balance;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface BalanceRepository {
    Balance findOneByAccountId(BigDecimal accountId);
    Balance findOneByAccountIdAsync(BigDecimal accountId);
    void update(Balance balance, BigDecimal remainingAmount);
}
