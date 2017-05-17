package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import dev.ranggalabs.restapi.model.CardValidation;
import dev.ranggalabs.restapi.repository.BalanceRepository;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * Created by erlangga on 4/25/2017.
 */
@Service
public class BalanceServiceImpl extends BaseService implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public BaseResponse inquiry(String printNumber) {
        BalanceInquiryValidation balanceInquiryValidation = validationBalanceInquiryByPrintNumber(printNumber);
        if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
        }
        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount());
    }

    @Override
    public Observable<BaseResponse> inquiryObsV3(String printNumber) {
        return validationBalanceInquiryByPrintNumberObsV3(printNumber).flatMap(new Function<BalanceInquiryValidation, ObservableSource<BaseResponse>>() {
            @Override
            public ObservableSource<BaseResponse> apply(@NonNull BalanceInquiryValidation balanceInquiryValidation) throws Exception {
                if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return Observable.just(constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null));
                }
                return Observable.just(constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount()));
            }
        });
    }

    @Override
    public Observable<BalanceInquiryValidation> validationBalanceInquiryByPrintNumberObsV3(String printNumber) {
        return validationCardByPrintNumberObs(printNumber).flatMap(new Function<CardValidation, ObservableSource<BalanceInquiryValidation>>() {
            @Override
            public ObservableSource<BalanceInquiryValidation> apply(@NonNull CardValidation cardValidation) throws Exception {
                BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
                if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    balanceInquiryValidation.setCode(cardValidation.getCode());
                    balanceInquiryValidation.setMessage(cardValidation.getMessage());
                    return Observable.just(balanceInquiryValidation);
                }
                return validationAccountByAccountIdObs(cardValidation.getAccountId());
            }
        });
    }

    private Observable<BalanceInquiryValidation> validationAccountByAccountIdObs(BigDecimal accountId) {
        return findOneBalanceByAccountIdObs(accountId).flatMap(new Function<Balance, ObservableSource<BalanceInquiryValidation>>() {
            @Override
            public ObservableSource<BalanceInquiryValidation> apply(@NonNull Balance balance) throws Exception {
                BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
                if(balance.getId()==null){
                    balanceInquiryValidation.setCode(ResponseCode.CIF_BALANCE_NOT_FOUND.getCode());
                    balanceInquiryValidation.setMessage(ResponseCode.CIF_BALANCE_NOT_FOUND.getDetail());
                }
                balanceInquiryValidation.setCode(ResponseCode.APPROVED.getCode());
                balanceInquiryValidation.setMessage(ResponseCode.APPROVED.getDetail());
                balanceInquiryValidation.setBalance(balance);
                return Observable.just(balanceInquiryValidation);
            }
        });
    }

    private Observable<Balance> findOneBalanceByAccountIdObs(BigDecimal accountId){
        return Observable.fromCallable(new Callable<Balance>() {
            @Override
            public Balance call() throws Exception {
                Balance balance = balanceRepository.findOneByAccountId(accountId);
                if (balance == null) {
                    return new Balance();
                }
                return balance;
            }
        });
    }

    @Override
    public BalanceInquiryValidation validationBalanceInquiryByPrintNumber(String printNumber) {
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
    public Observable<BaseModel> updateObs(Balance balance, BigDecimal remainingBalance) {
        return Observable.fromCallable(new Callable<BaseModel>() {
            @Override
            public BaseModel call() throws Exception {
                return update(balance,remainingBalance);
            }
        });
    }

    @Override
    @Async
    public CompletableFuture<BaseResponse> asyncInquiry(String printNumber) {
        /*return CompletableFuture.supplyAsync(() -> {
            return inquiry(printNumber);
        });*/

        return CompletableFuture.completedFuture(inquiry(printNumber));
    }
}
