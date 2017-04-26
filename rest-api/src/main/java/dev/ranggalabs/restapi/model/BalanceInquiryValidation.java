package dev.ranggalabs.restapi.model;

import dev.ranggalabs.enitity.Balance;

/**
 * Created by erlangga on 4/26/2017.
 */
public class BalanceInquiryValidation extends BaseModel {

    private Balance balance;

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
