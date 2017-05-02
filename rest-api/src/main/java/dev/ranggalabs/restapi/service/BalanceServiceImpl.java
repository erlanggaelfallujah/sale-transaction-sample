package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import dev.ranggalabs.restapi.model.CardValidation;
import dev.ranggalabs.restapi.repository.BalanceRepository;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by erlangga on 4/25/2017.
 */
@Service
public class BalanceServiceImpl extends BaseService implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    private final ExecutorService customObservableExecutor = Executors.newFixedThreadPool(10);

    @Override
    public BaseResponse inquiry(String printNumber) {
        BalanceInquiryValidation balanceInquiryValidation = validateBalanceInquiry(printNumber);
        if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
        }
        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount());
    }

    @Override
    public Observable<BaseResponse> inquiryObs(String printNumber) {
        return validateBalanceInquiryObs(printNumber).map(new Function<BalanceInquiryValidation, BaseResponse>() {
            @Override
            public BaseResponse apply(@NonNull BalanceInquiryValidation balanceInquiryValidation) throws Exception {
                if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
                }
                return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount());
            }
        });
    }

    @Override
    public Observable<BalanceInquiryValidation> validateBalanceInquiryObs(String printNumber) {
        return validationObs(printNumber).map(new Function<CardValidation, BalanceInquiryValidation>() {
            @Override
            public BalanceInquiryValidation apply(@NonNull CardValidation cardValidation) throws Exception {
                BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
                if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    balanceInquiryValidation.setCode(cardValidation.getCode());
                    balanceInquiryValidation.setMessage(cardValidation.getMessage());
                    return balanceInquiryValidation;
                }

                Balance balance = balanceRepository.findOneByAccountId(cardValidation.getAccountId());
                if(balance==null){
                    balanceInquiryValidation.setCode(ResponseCode.CIF_BALANCE_NOT_FOUND.getCode());
                    balanceInquiryValidation.setMessage(ResponseCode.CIF_BALANCE_NOT_FOUND.getDetail());
                    return balanceInquiryValidation;
                }

                balanceInquiryValidation.setCode(ResponseCode.APPROVED.getCode());
                balanceInquiryValidation.setMessage(ResponseCode.APPROVED.getDetail());
                balanceInquiryValidation.setBalance(balance);
                return balanceInquiryValidation;
            }
        });
    }

    @Override
    public BalanceInquiryValidation validateBalanceInquiry(String printNumber) {
        CardValidation cardValidation = validation(printNumber);

        BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
        if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            balanceInquiryValidation.setCode(cardValidation.getCode());
            balanceInquiryValidation.setMessage(cardValidation.getMessage());
            return balanceInquiryValidation;
        }

        Balance balance = balanceRepository.findOneByAccountId(cardValidation.getAccountId());
        if(balance==null){
            balanceInquiryValidation.setCode(ResponseCode.CIF_BALANCE_NOT_FOUND.getCode());
            balanceInquiryValidation.setMessage(ResponseCode.CIF_BALANCE_NOT_FOUND.getDetail());
            return balanceInquiryValidation;
        }

        balanceInquiryValidation.setCode(ResponseCode.APPROVED.getCode());
        balanceInquiryValidation.setMessage(ResponseCode.APPROVED.getDetail());
        balanceInquiryValidation.setBalance(balance);
        return balanceInquiryValidation;
    }

    @Override
    public BaseModel update(Balance balance, BigDecimal remainingBalance) {
        BaseModel baseModel = new BaseModel();
        try{
            balanceRepository.update(balance,remainingBalance);
            baseModel.setCode(ResponseCode.APPROVED.getCode());
            baseModel.setMessage(ResponseCode.APPROVED.getDetail());
        }catch (Exception e) {
            baseModel.setCode(ResponseCode.SYSTEM_ERROR.getCode());
            baseModel.setMessage(ResponseCode.SYSTEM_ERROR.getDetail());
        }
        return baseModel;
    }

    @Override
    @Async
    public CompletableFuture<BaseResponse> asyncInquiry(String printNumber) {
        /*return CompletableFuture.supplyAsync(() -> {
            return inquiry(printNumber);
        });*/

        return CompletableFuture.completedFuture(inquiry(printNumber));
    }

/*    @Override
    public Observable<BaseResponse> asyncInquiryObs(String printNumber) {
        return Observable.<BaseResponse>create(s -> {
           s.onNext(inquiry(printNumber));
            s.onCompleted();
        }).onErrorReturn(throwable -> {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setCode(ResponseCode.SYSTEM_ERROR.getCode());
            baseResponse.setMessage(ResponseCode.SYSTEM_ERROR.getDetail());
            return baseResponse;
        }).subscribeOn(Schedulers.from(customObservableExecutor));
    }*/

    @Override
    public Observable<BaseResponse> asyncInquiryObs(String printNumber) {
        return Observable.create(s -> {
            s.onNext(inquiry(printNumber));
            s.onComplete();
        });
    }

    @Override
    public Observable<CardValidation> asyncCardValidation(String printNumber) {
        return validationObs(printNumber).subscribeOn(Schedulers.from(customObservableExecutor));
    }

}
