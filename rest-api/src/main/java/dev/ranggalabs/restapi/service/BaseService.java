package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Account;
import dev.ranggalabs.enitity.Card;
import dev.ranggalabs.restapi.model.CardValidation;
import dev.ranggalabs.restapi.repository.AccountRepository;
import dev.ranggalabs.restapi.repository.CardRepository;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

/**
 * Created by erlangga on 4/26/2017.
 */
public class BaseService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;

    protected BaseResponse constructBaseResponse(String code, String message, String printNumber, BigDecimal amount){
        BaseResponse balanceInquiryResponse = new BaseResponse();
        balanceInquiryResponse.setCode(code);
        balanceInquiryResponse.setMessage(message);
        balanceInquiryResponse.setPrintNumber(printNumber);
        balanceInquiryResponse.setBalance(amount);
        return balanceInquiryResponse;
    }

    protected Observable<CardValidation> validationCardByPrintNumberObs(String printNumber){
        return validationCardObservable(printNumber).flatMap(new Function<CardValidation, ObservableSource<CardValidation>>() {
            @Override
            public ObservableSource<CardValidation> apply(@NonNull CardValidation cardValidation) throws Exception {
                System.out.println("ValidationCardByPrintNumberObs flatMap on " + Thread.currentThread().getName());
                if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return Observable.just(cardValidation);
                }
                return validationAccountByCifObs(cardValidation.getCif());
            }
        });
    }

    private Observable<CardValidation> validationCardObservable(String printNumber){
        return findOneCardByPrintNumberObs(printNumber).flatMap(new Function<Card, ObservableSource<CardValidation>>() {
            @Override
            public ObservableSource<CardValidation> apply(@NonNull Card card) throws Exception {
                System.out.println("ValidationCardObservable flatMap on " + Thread.currentThread().getName());
                CardValidation cardValidation = new CardValidation();
                if(card.getId()==null){
                    cardValidation.setMessage(ResponseCode.CARD_NOT_FOUND.getDetail());
                    cardValidation.setCode(ResponseCode.CARD_NOT_FOUND.getCode());
                }else if(!card.isStatus()){
                    cardValidation.setMessage(ResponseCode.CARD_NOT_ACTIVE.getDetail());
                    cardValidation.setCode(ResponseCode.CARD_NOT_ACTIVE.getCode());
                }else {
                    cardValidation.setCif(card.getCif());
                    cardValidation.setCode(ResponseCode.APPROVED.getCode());
                    cardValidation.setMessage(ResponseCode.APPROVED.getDetail());
                }
                return Observable.just(cardValidation);
            }
        });
    }

    private Observable<Card> findOneCardByPrintNumberObs(String printNumber){
        return Observable.fromCallable(new Callable<Card>() {
            @Override
            public Card call() throws Exception {
                System.out.println("Find one card on " + Thread.currentThread().getName());
                Card card = cardRepository.findOneByPrintNumber(printNumber);
                if(card==null){
                    return new Card();
                }
                return card;
            }
        });
    }

    private CardValidation validationCard(String printNumber){
        CardValidation cardValidation = new CardValidation();

        // validasi card
        Card card = cardRepository.findOneByPrintNumber(printNumber);
        if(card==null){
            cardValidation.setMessage(ResponseCode.CARD_NOT_FOUND.getDetail());
            cardValidation.setCode(ResponseCode.CARD_NOT_FOUND.getCode());
            return cardValidation;
        }
        if(!card.isStatus()){
            cardValidation.setMessage(ResponseCode.CARD_NOT_ACTIVE.getDetail());
            cardValidation.setCode(ResponseCode.CARD_NOT_ACTIVE.getCode());
            return cardValidation;
        }

        cardValidation.setCif(card.getCif());
        cardValidation.setCode(ResponseCode.APPROVED.getCode());
        cardValidation.setMessage(ResponseCode.APPROVED.getDetail());
        return cardValidation;
    }

    private Observable<Account> findOneAccountByCifObs(String cif){
        return Observable.fromCallable(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                System.out.println("Find one account on " + Thread.currentThread().getName());
                Account account = accountRepository.findOneByCif(cif);
                if(account==null){
                    return new Account();
                }
                return account;
            }
        });
    }

    private Observable<CardValidation> validationAccountByCifObs(String cif){
        return findOneAccountByCifObs(cif).flatMap(new Function<Account, ObservableSource<CardValidation>>() {
            @Override
            public ObservableSource<CardValidation> apply(@NonNull Account account) throws Exception {
                System.out.println("validationAccountByCifObs flatMap on " + Thread.currentThread().getName());
                CardValidation cardValidation = new CardValidation();
                if(account.getId()==null){
                    cardValidation.setMessage(ResponseCode.ACCOUNT_NOT_FOUND.getDetail());
                    cardValidation.setCode(ResponseCode.ACCOUNT_NOT_FOUND.getCode());
                }else if(!account.isStatus()){
                    cardValidation.setMessage(ResponseCode.ACCOUNT_NOT_ACTIVE.getDetail());
                    cardValidation.setCode(ResponseCode.ACCOUNT_NOT_ACTIVE.getCode());
                }else {
                    cardValidation.setMessage(ResponseCode.APPROVED.getDetail());
                    cardValidation.setCode(ResponseCode.APPROVED.getCode());
                    cardValidation.setAccountId(account.getId());
                    cardValidation.setCif(cif);
                }
                return Observable.just(cardValidation);
            }
        });
    }

    private CardValidation validationAccountByCif(String cif){
        CardValidation cardValidation = new CardValidation();
        Account account = accountRepository.findOneByCif(cif);
        if(account==null){
            cardValidation.setMessage(ResponseCode.ACCOUNT_NOT_FOUND.getDetail());
            cardValidation.setCode(ResponseCode.ACCOUNT_NOT_FOUND.getCode());
            return cardValidation;
        }
        if(!account.isStatus()){
            cardValidation.setMessage(ResponseCode.ACCOUNT_NOT_ACTIVE.getDetail());
            cardValidation.setCode(ResponseCode.ACCOUNT_NOT_ACTIVE.getCode());
            return cardValidation;
        }

        cardValidation.setMessage(ResponseCode.APPROVED.getDetail());
        cardValidation.setCode(ResponseCode.APPROVED.getCode());
        cardValidation.setAccountId(account.getId());
        cardValidation.setCif(cif);

        return cardValidation;
    }

    protected CardValidation validation(String printNumber){
       CardValidation cardValidation = validationCard(printNumber);
        if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            return cardValidation;
        }
       return validationAccountByCif(cardValidation.getCif());
    }
}
