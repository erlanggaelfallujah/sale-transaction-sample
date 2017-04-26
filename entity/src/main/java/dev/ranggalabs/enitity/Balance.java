package dev.ranggalabs.enitity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by erlangga on 4/25/2017.
 */
public class Balance {
    private BigDecimal id;
    private BigDecimal amount;
    private BigDecimal accountId;
    private Date version;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAccountId() {
        return accountId;
    }

    public void setAccountId(BigDecimal accountId) {
        this.accountId = accountId;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }
}
