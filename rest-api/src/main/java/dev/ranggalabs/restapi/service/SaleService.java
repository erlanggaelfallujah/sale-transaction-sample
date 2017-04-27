package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Created by erlangga on 4/26/2017.
 */
public interface SaleService {
    BaseResponse sale(String printNumber, SaleRequest saleRequest);
    CompletableFuture<BaseResponse> asyncSale(String printNumber, SaleRequest saleRequest);
}
