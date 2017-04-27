package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Created by erlangga on 4/26/2017.
 */
@Service
public class SaleServiceImpl extends BaseService implements SaleService {

    @Autowired
    private BalanceService balanceService;

    @Override
    @Async
    public BaseResponse sale(String printNumber, SaleRequest saleRequest) {
        if (saleRequest.getAmount() == null) {
            return constructBaseResponse(ResponseCode.AMOUNT_EMPTY.getCode(), ResponseCode.AMOUNT_EMPTY.getDetail(), printNumber, saleRequest.getAmount());
        }

        BalanceInquiryValidation balanceInquiryValidation = balanceService.validateBalanceInquiry(printNumber);
        if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
        }

        BigDecimal balance = balanceInquiryValidation.getBalance().getAmount();
        int resVal1 = balance.compareTo(BigDecimal.ZERO);
        if(resVal1==0 || resVal1==-1){
            return constructBaseResponse(ResponseCode.INSUFFICIENT_BALANCE.getCode(),ResponseCode.INSUFFICIENT_BALANCE.getDetail(),printNumber,saleRequest.getAmount());
        }

        // balance kurang dari amount
        int resVal2 = balance.compareTo(saleRequest.getAmount());
        if(resVal2==-1){
            return constructBaseResponse(ResponseCode.INSUFFICIENT_BALANCE.getCode(),ResponseCode.INSUFFICIENT_BALANCE.getDetail(),printNumber,saleRequest.getAmount());
        }

        // update balance
        BigDecimal remainingBalance = balance.subtract(saleRequest.getAmount());
        BaseModel baseModel = balanceService.update(balanceInquiryValidation.getBalance(),remainingBalance);
        if(!baseModel.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(baseModel.getCode(),baseModel.getMessage(),printNumber,saleRequest.getAmount());
        }

        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,remainingBalance);
    }

    @Override
    @Async
    public CompletableFuture<BaseResponse> asyncSale(String printNumber, SaleRequest saleRequest) {
        return CompletableFuture.completedFuture(sale(printNumber,saleRequest));
    }
}
