package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Account;
import dev.ranggalabs.enitity.Card;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.CardValidation;
import dev.ranggalabs.restapi.repository.AccountRepository;
import dev.ranggalabs.restapi.repository.CardRepository;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

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

    private Observable<CardValidation> validationCardByPrintNumberObs(String printNumber){
        return Observable.create(new ObservableOnSubscribe<CardValidation>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<CardValidation> observableEmitter) throws Exception {
                observableEmitter.onNext(validationCard(printNumber));
                observableEmitter.onComplete();
            }
        });
    }

    protected Observable<CardValidation> validationObs(String printNumber){
        return validationCardByPrintNumberObs(printNumber).map(new Function<CardValidation, CardValidation>() {
            @Override
            public CardValidation apply(@NonNull CardValidation cardValidation) throws Exception {
                if(!cardValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return cardValidation;
                }
                return validationAccount(cardValidation.getCif());
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

    private CardValidation validationAccount(String cif){
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
       return validationAccount(cardValidation.getCif());
    }



}
