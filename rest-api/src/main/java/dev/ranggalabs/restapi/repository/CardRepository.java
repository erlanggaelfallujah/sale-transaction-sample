package dev.ranggalabs.restapi.repository;

import dev.ranggalabs.enitity.Card;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface CardRepository {
    Card findOneByPrintNumber(String printNumber);
}
