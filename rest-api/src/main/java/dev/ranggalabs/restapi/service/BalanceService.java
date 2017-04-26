package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface BalanceService {
    BaseResponse inquiry(String printNumber);
    BalanceInquiryValidation validateBalanceInquiry(String printNumber);
    BaseModel update(Balance balance, BigDecimal remainingBalance);

    BaseResponse inquiryCompletableFuture(String printNumber);
    BaseResponse inquiryAsync(String printNumber);
}
