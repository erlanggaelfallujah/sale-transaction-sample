package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Account;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.enitity.Card;
import dev.ranggalabs.restapi.repository.AccountRepository;
import dev.ranggalabs.restapi.repository.BalanceRepository;
import dev.ranggalabs.restapi.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by erlangga on 4/25/2017.
 */
@Service
public class CardServiceImpl extends BaseService implements CardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public BaseResponse balanceInquiry(String printNumber) {
        // validasi card
        Card card = cardRepository.findOneByPrintNumber(printNumber);
        if(card==null){
            return constructBaseResponse(ResponseCode.CARD_NOT_FOUND.getCode(),ResponseCode.CARD_NOT_FOUND.getDetail(),printNumber,null);
        }
        if(!card.isStatus()){
            return constructBaseResponse(ResponseCode.CARD_NOT_ACTIVE.getCode(),ResponseCode.CARD_NOT_ACTIVE.getDetail(),printNumber,null);
        }

        // validasi account
        Account account = accountRepository.findOneByCif(card.getCif());
        if(account==null){
            return constructBaseResponse(ResponseCode.ACCOUNT_NOT_FOUND.getCode(),ResponseCode.ACCOUNT_NOT_FOUND.getDetail(),printNumber,null);
        }
        if(!account.isStatus()){
            return constructBaseResponse(ResponseCode.ACCOUNT_NOT_ACTIVE.getCode(),ResponseCode.ACCOUNT_NOT_ACTIVE.getDetail(),printNumber,null);
        }

        // validasi balance
        Balance balance = balanceRepository.findOneByAccountId(account.getId());
        if(balance==null){
            return constructBaseResponse(ResponseCode.CIF_BALANCE_NOT_FOUND.getCode(),ResponseCode.CIF_BALANCE_NOT_FOUND.getDetail(),printNumber,null);
        }

        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balance.getAmount());
    }

    @Override
    public BaseResponse balanceInquiryCompletableFuture(String printNumber) {
        return null;
    }

    @Override
    public BaseResponse balanceInquiryAsync(String printNumber) {
        return null;
    }
}
