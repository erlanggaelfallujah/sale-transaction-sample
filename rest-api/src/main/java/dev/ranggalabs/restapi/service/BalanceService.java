package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface BalanceService {
    BaseResponse inquiry(String printNumber);
    BalanceInquiryValidation validateBalanceInquiry(String printNumber);
    BaseModel update(Balance balance, BigDecimal remainingBalance);

    CompletableFuture<BaseResponse> asyncInquiry(String printNumber);
}
