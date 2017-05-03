package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import dev.ranggalabs.restapi.model.CardValidation;
import io.reactivex.Observable;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface BalanceService {
    BaseResponse inquiry(String printNumber);
    Observable<BaseResponse> inquiryObsV2(String printNumber);
    BalanceInquiryValidation validateBalanceInquiry(String printNumber);
    Observable<BalanceInquiryValidation> validateBalanceInquiryObs(String printNumber);
    BaseModel update(Balance balance, BigDecimal remainingBalance);

    CompletableFuture<BaseResponse> asyncInquiry(String printNumber);
    Observable<BaseResponse> inquiryObs(String printNumber);
    Observable<CardValidation> cardValidationObs(String printNumber);
}
