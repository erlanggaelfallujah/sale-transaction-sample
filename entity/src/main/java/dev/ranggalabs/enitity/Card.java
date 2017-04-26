package dev.ranggalabs.enitity;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/25/2017.
 */
public class Card {
    private BigDecimal id;
    private String printNumber;
    private String cif;
    private boolean status;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getPrintNumber() {
        return printNumber;
    }

    public void setPrintNumber(String printNumber) {
        this.printNumber = printNumber;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
