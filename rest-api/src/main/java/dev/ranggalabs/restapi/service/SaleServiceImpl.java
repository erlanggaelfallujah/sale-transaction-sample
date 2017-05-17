package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.common.util.ResponseCode;
import dev.ranggalabs.restapi.model.BalanceInquiryValidation;
import dev.ranggalabs.restapi.model.BaseModel;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
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
    public BaseResponse sale(String printNumber, SaleRequest saleRequest) {
        if (saleRequest.getAmount() == null) {
            return constructBaseResponse(ResponseCode.AMOUNT_EMPTY.getCode(), ResponseCode.AMOUNT_EMPTY.getDetail(), printNumber, saleRequest.getAmount());
        }

        BalanceInquiryValidation balanceInquiryValidation = balanceService.validationBalanceInquiryByPrintNumber(printNumber);
        if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null);
        }

        BaseResponse baseResponse = validate(balanceInquiryValidation,saleRequest,printNumber);
        if(!baseResponse.getCode().equals(ResponseCode.APPROVED.getCode())){
            return baseResponse;
        }

        return processSale(balanceInquiryValidation,saleRequest,baseResponse,printNumber);
    }

    @Override
    @Async
    public CompletableFuture<BaseResponse> asyncSale(String printNumber, SaleRequest saleRequest) {
        return CompletableFuture.completedFuture(sale(printNumber,saleRequest));
    }

    private BaseResponse validate(BalanceInquiryValidation balanceInquiryValidation, SaleRequest saleRequest, String printNumber){
        BigDecimal balance = balanceInquiryValidation.getBalance().getAmount();
        int resVal1 = balance.compareTo(BigDecimal.ZERO);
        if(resVal1==0 || resVal1==-1){
            return constructBaseResponse(ResponseCode.INSUFFICIENT_BALANCE.getCode(),ResponseCode.INSUFFICIENT_BALANCE.getDetail(),printNumber,saleRequest.getAmount());
        }
        int resVal2 = balance.compareTo(saleRequest.getAmount());
        if(resVal2==-1){
            return constructBaseResponse(ResponseCode.INSUFFICIENT_BALANCE.getCode(),ResponseCode.INSUFFICIENT_BALANCE.getDetail(),printNumber,saleRequest.getAmount());
        }
        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,balance);
    }

    private BaseResponse processSale(BalanceInquiryValidation balanceInquiryValidation, SaleRequest saleRequest, BaseResponse baseResponse, String printNumber){
        // update balance
        BigDecimal remainingBalance = baseResponse.getBalance().subtract(saleRequest.getAmount());
        BaseModel baseModel = balanceService.update(balanceInquiryValidation.getBalance(),remainingBalance);
        if(!baseModel.getCode().equals(ResponseCode.APPROVED.getCode())){
            return constructBaseResponse(baseModel.getCode(),baseModel.getMessage(),printNumber,saleRequest.getAmount());
        }
        return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,remainingBalance);
    }

    private Observable<BaseResponse> processSaleObs(BalanceInquiryValidation balanceInquiryValidation, SaleRequest saleRequest, BaseResponse baseResponse, String printNumber){
        BigDecimal remainingBalance = baseResponse.getBalance().subtract(saleRequest.getAmount());
        return balanceService.updateObs(balanceInquiryValidation.getBalance(),remainingBalance).map(new Function<BaseModel, BaseResponse>() {
            @Override
            public BaseResponse apply(@NonNull BaseModel baseModel) throws Exception {
                if(!baseModel.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return constructBaseResponse(baseModel.getCode(),baseModel.getMessage(),printNumber,saleRequest.getAmount());
                }
                return constructBaseResponse(ResponseCode.APPROVED.getCode(),ResponseCode.APPROVED.getDetail(),printNumber,remainingBalance);
            }
        });
    }

    @Override
    public Observable<BaseResponse> saleObsV3(String printNumber, SaleRequest saleRequest) {
        Observable<BalanceInquiryValidation> balanceInquiryValidation = balanceService.validationBalanceInquiryByPrintNumberObsV3(printNumber);
        return balanceInquiryValidation.flatMap(new Function<BalanceInquiryValidation, ObservableSource<BaseResponse>>() {
            @Override
            public ObservableSource<BaseResponse> apply(@NonNull BalanceInquiryValidation balanceInquiryValidation) throws Exception {
                if(!balanceInquiryValidation.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return Observable.just(constructBaseResponse(balanceInquiryValidation.getCode(),balanceInquiryValidation.getMessage(),printNumber,null));
                }

                BaseResponse baseResponse = validate(balanceInquiryValidation,saleRequest,printNumber);
                if(!baseResponse.getCode().equals(ResponseCode.APPROVED.getCode())){
                    return Observable.just(baseResponse);
                }

                return processSaleObs(balanceInquiryValidation,saleRequest,baseResponse,printNumber);
            }
        });
    }

}
