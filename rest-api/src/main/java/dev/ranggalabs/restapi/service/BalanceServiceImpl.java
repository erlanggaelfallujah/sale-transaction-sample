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
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.*;

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
                System.out.println("inquiryObsV3 flatMap on " + Thread.currentThread().getName());
                if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return Observable.just(constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null));
                }
                return Observable.just(constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount()));
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation());
    }

    @Override
    public Mono<BaseResponse> inquiryReactor(String printNumber) {
        return validationBalanceInquiryByPrintNumberMono(printNumber).map(new java.util.function.Function<BalanceInquiryValidation, BaseResponse>() {
            @Override
            public BaseResponse apply(BalanceInquiryValidation balanceInquiryValidation) {
                if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
                }
                return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount());
            }
        });
    }

    @Override
    public Observable<BalanceInquiryValidation> validationBalanceInquiryByPrintNumberObsV3(String printNumber) {
        return validationCardByPrintNumberObs(printNumber).flatMap(new Function<CardValidation, ObservableSource<BalanceInquiryValidation>>() {
            @Override
            public ObservableSource<BalanceInquiryValidation> apply(@NonNull CardValidation cardValidation) throws Exception {
                System.out.println("validationBalanceInquiryByPrintNumberObsV3 flatMap on " + Thread.currentThread().getName());
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

    @Override
    public Mono<BalanceInquiryValidation> validationBalanceInquiryByPrintNumberMono(String printNumber) {
        return validationCardByPrintNumberMono(printNumber).flatMap(new java.util.function.Function<CardValidation, Mono<? extends BalanceInquiryValidation>>() {
            @Override
            public Mono<? extends BalanceInquiryValidation> apply(CardValidation cardValidation) {
                BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
                if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    balanceInquiryValidation.setCode(cardValidation.getCode());
                    balanceInquiryValidation.setMessage(cardValidation.getMessage());
                    return Mono.just(balanceInquiryValidation);
                }
                return validationAccountByAccountIdMono(cardValidation.getAccountId());
            }
        });
    }

    private Observable<BalanceInquiryValidation> validationAccountByAccountIdObs(BigDecimal accountId) {
        return findOneBalanceByAccountIdObs(accountId).flatMap(new Function<Balance, ObservableSource<BalanceInquiryValidation>>() {
            @Override
            public ObservableSource<BalanceInquiryValidation> apply(@NonNull Balance balance) throws Exception {
                System.out.println("ValidationAccountByAccountIdObs flatMap on " + Thread.currentThread().getName());
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
        }).subscribeOn(Schedulers.io());
    }

    private Mono<BalanceInquiryValidation> validationAccountByAccountIdMono(BigDecimal accountId){
        return findOneBalanceByAccountIdMono(accountId).map(new java.util.function.Function<Balance, BalanceInquiryValidation>() {
            @Override
            public BalanceInquiryValidation apply(Balance balance) {
                BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
                if(balance.getId()==null){
                    balanceInquiryValidation.setCode(ResponseCode.CIF_BALANCE_NOT_FOUND.getCode());
                    balanceInquiryValidation.setMessage(ResponseCode.CIF_BALANCE_NOT_FOUND.getDetail());
                }
                balanceInquiryValidation.setCode(ResponseCode.APPROVED.getCode());
                balanceInquiryValidation.setMessage(ResponseCode.APPROVED.getDetail());
                balanceInquiryValidation.setBalance(balance);
                return balanceInquiryValidation;
            }
        });
    }

    private Observable<Balance> findOneBalanceByAccountIdObs(BigDecimal accountId){
        return Observable.fromCallable(new Callable<Balance>() {
            @Override
            public Balance call() throws Exception {
                System.out.println("Find one balance on " + Thread.currentThread().getName());
                Balance balance = balanceRepository.findOneByAccountId(accountId);
                if (balance == null) {
                    return new Balance();
                }
                return balance;
            }
        });
    }

    private Mono<Balance> findOneBalanceByAccountIdMono(BigDecimal accountId){
        return Mono.fromCallable(new Callable<Balance>() {
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
    public BalanceInquiryValidation validationBalanceInquiryByPrintNumberAsync(String printNumber) {
        CardValidation cardValidation = validationAsync(printNumber);

        BalanceInquiryValidation balanceInquiryValidation = new BalanceInquiryValidation();
        if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            balanceInquiryValidation.setCode(cardValidation.getCode());
            balanceInquiryValidation.setMessage(cardValidation.getMessage());
            return balanceInquiryValidation;
        }

        Balance balance = balanceRepository.findOneByAccountIdAsync(cardValidation.getAccountId());
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
    public BaseModel updateAsync(Balance balance, BigDecimal remainingBalance) {
       CompletableFuture<BaseModel> baseModelCompletableFuture = CompletableFuture.completedFuture(update(balance,remainingBalance));
        try {
            return baseModelCompletableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<BaseModel> updateObs(Balance balance, BigDecimal remainingBalance) {
        return Observable.fromCallable(new Callable<BaseModel>() {
            @Override
            public BaseModel call() throws Exception {
                System.out.println("Update balance on " + Thread.currentThread().getName());
                return update(balance,remainingBalance);
            }
        });
    }

    @Override
    public Mono<BaseModel> updateMono(Balance balance, BigDecimal remainingBalance) {
        return Mono.fromCallable(new Callable<BaseModel>() {
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
