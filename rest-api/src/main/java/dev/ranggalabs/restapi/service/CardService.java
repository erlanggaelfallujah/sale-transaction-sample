package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface CardService {
    BaseResponse balanceInquiry(String printNumber);
    BaseResponse balanceInquiryCompletableFuture(String printNumber);
    BaseResponse balanceInquiryAsync(String printNumber);
}
