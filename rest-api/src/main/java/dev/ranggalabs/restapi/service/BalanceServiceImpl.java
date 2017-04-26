package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.enitity.Account;
import dev.ranggalabs.enitity.Balance;
import dev.ranggalabs.enitity.Card;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import dev.ranggalabs.restapi.model.CardValidation;
import dev.ranggalabs.restapi.repository.AccountRepository;
import dev.ranggalabs.restapi.repository.BalanceRepository;
import dev.ranggalabs.restapi.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/25/2017.
 */
@Service
public class BalanceServiceImpl extends BaseService implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public BaseResponse inquiry(String printNumber) {
        BalanceInquiryValidation balanceInquiryValidation = validateBalanceInquiry(printNumber);
        if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
        }
        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balanceInquiryValidation.getBalance().getAmount());
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
    public BaseResponse inquiryCompletableFuture(String printNumber) {
        return null;
    }

    @Override
    public BaseResponse inquiryAsync(String printNumber) {
        return null;
    }
}
