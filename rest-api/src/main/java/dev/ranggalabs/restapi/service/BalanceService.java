package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import io.reactivex.Observable;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface BalanceService {
    BaseResponse inquiry(String printNumber);
    BalanceInquiryValidation validationBalanceInquiryByPrintNumber(String printNumber);
    Observable<BalanceInquiryValidation> validationBalanceInquiryByPrintNumberObsV3(String printNumber);
    Mono<BalanceInquiryValidation> validationBalanceInquiryByPrintNumberMono(String printNumber);
    BaseModel update(Balance balance, BigDecimal remainingBalance);
    BaseModel updateAsync(Balance balance, BigDecimal remainingBalance);
    Observable<BaseModel> updateObs(Balance balance, BigDecimal remainingBalance);
    Mono<BaseModel> updateMono(Balance balance, BigDecimal remainingBalance);

    Observable<BaseResponse> inquiryObsV3(String printNumber);
    Mono<BaseResponse> inquiryReactor(String printNumber);
    CompletableFuture<BaseResponse> asyncInquiry(String printNumber);
}
