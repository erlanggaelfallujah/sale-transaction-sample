package dev.ranggalabs.common.dto;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/26/2017.
 */
public class SaleRequest {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
