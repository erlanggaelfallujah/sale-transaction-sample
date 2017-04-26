package dev.ranggalabs.enitity;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/25/2017.
 */
public class Account {
    private BigDecimal id;
    private String cif;
    private String accountNumber;
    private boolean status;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
