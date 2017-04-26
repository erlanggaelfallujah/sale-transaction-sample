package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;

import java.math.BigDecimal;

/**
 * Created by erlangga on 4/26/2017.
 */
public class BaseService {

    protected BaseResponse constructBaseResponse(String code, String message, String printNumber, BigDecimal amount){
        BaseResponse balanceInquiryResponse = new BaseResponse();
        balanceInquiryResponse.setCode(code);
        balanceInquiryResponse.setMessage(message);
        balanceInquiryResponse.setPrintNumber(printNumber);
        balanceInquiryResponse.setBalance(amount);
        return balanceInquiryResponse;
    }

}
