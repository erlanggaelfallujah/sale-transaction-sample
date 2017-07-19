package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import io.reactivex.Observable;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * Created by erlangga on 4/26/2017.
 */
public interface SaleService {
    BaseResponse sale(String printNumber, SaleRequest saleRequest);
    CompletableFuture<BaseResponse> asyncSale(String printNumber, SaleRequest saleRequest);
    Observable<BaseResponse> saleObsV3(String printNumber, SaleRequest saleRequest);
    Mono<BaseResponse> saleMono(String printNumber, SaleRequest saleRequest);
}
