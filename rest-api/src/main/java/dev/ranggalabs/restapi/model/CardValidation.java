package dev.ranggalabs.restapi.model;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/26/2017.
 */
public class CardValidation extends BaseModel {
    private String cif;
    private BigDecimal accountId;

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public BigDecimal getAccountId() {
        return accountId;
    }

    public void setAccountId(BigDecimal accountId) {
        this.accountId = accountId;
    }
}
