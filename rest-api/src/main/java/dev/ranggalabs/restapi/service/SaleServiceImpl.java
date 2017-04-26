package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Account;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.enitity.Card;
import dev.ranggalabs.restapi.repository.AccountRepository;
import dev.ranggalabs.restapi.repository.BalanceRepository;
import dev.ranggalabs.restapi.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/26/2017.
 */
@Service
public class SaleServiceImpl extends BaseService implements SaleService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public BaseResponse sale(String printNumber, SaleRequest saleRequest) {
        if (saleRequest.getAmount() == null) {
            return constructBaseResponse(ResponseCode.AMOUNT_EMPTY.getCode(), ResponseCode.AMOUNT_EMPTY.getDetail(), printNumber, saleRequest.getAmount());
        }

        Balance balance = null;
        // validasi card
        Card card = cardRepository.findOneByPrintNumber(printNumber);
        if (card == null) {
            return constructBaseResponse(ResponseCode.CARD_NOT_FOUND.getCode(), ResponseCode.CARD_NOT_FOUND.getDetail(), printNumber, saleRequest.getAmount());
        }
        if (!card.isStatus()) {
            return constructBaseResponse(ResponseCode.CARD_NOT_ACTIVE.getCode(),ResponseCode.CARD_NOT_ACTIVE.getDetail(),printNumber,saleRequest.getAmount());
        }

        // validasi account
        Account account = accountRepository.findOneByCif(card.getCif());
        if (account == null) {
            return constructBaseResponse(ResponseCode.ACCOUNT_NOT_FOUND.getCode(),ResponseCode.ACCOUNT_NOT_FOUND.getDetail(),printNumber,saleRequest.getAmount());
        }
        if (!account.isStatus()) {
            return constructBaseResponse(ResponseCode.ACCOUNT_NOT_ACTIVE.getCode(),ResponseCode.ACCOUNT_NOT_ACTIVE.getDetail(),printNumber,saleRequest.getAmount());
        }

        // validasi balance
        balance = balanceRepository.findOneByAccountId(account.getId());
        if (balance == null) {
            return constructBaseResponse(ResponseCode.CIF_BALANCE_NOT_FOUND.getCode(),ResponseCode.CIF_BALANCE_NOT_FOUND.getDetail(),printNumber,saleRequest.getAmount());
        }

        int resVal1 = balance.getAmount().compareTo(BigDecimal.ZERO);
        if(resVal1==0 || resVal1==-1){
            return constructBaseResponse(ResponseCode.INSUFFICIENT_BALANCE.getCode(),ResponseCode.INSUFFICIENT_BALANCE.getDetail(),printNumber,saleRequest.getAmount());
        }

        // balance kurang dari amount
        int resVal2 = balance.getAmount().compareTo(saleRequest.getAmount());
        if(resVal2==-1){
            return constructBaseResponse(ResponseCode.INSUFFICIENT_BALANCE.getCode(),ResponseCode.INSUFFICIENT_BALANCE.getDetail(),printNumber,saleRequest.getAmount());
        }

        // update balance
        BigDecimal remainingBalance = balance.getAmount().subtract(saleRequest.getAmount());
        try{
            balanceRepository.update(balance,remainingBalance);
        }catch (Exception e) {
            return constructBaseResponse(ResponseCode.SYSTEM_ERROR.getCode(),ResponseCode.SYSTEM_ERROR.getDetail(),printNumber,saleRequest.getAmount());
        }
        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,remainingBalance);
    }
}
