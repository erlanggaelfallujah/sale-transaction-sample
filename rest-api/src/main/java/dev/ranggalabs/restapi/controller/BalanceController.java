package dev.ranggalabs.restapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.restapi.service.BalanceService;
import dev.ranggalabs.restapi.service.SaleService;
import dev.ranggalabs.restapi.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutionException;

/**
 * Created by erlangga on 4/25/2017.
 */
@RestController
public class BalanceController {

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private SaleService saleService;

    @RequestMapping(method = RequestMethod.GET, value = "/inquiry/{printNumber}")
    public BaseResponse inquiry(@PathVariable String printNumber){
        return balanceService.inquiry(printNumber);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/sale/{printNumber}")
    public BaseResponse sale(@PathVariable String printNumber, @RequestBody SaleRequest saleRequest){
        return saleService.sale(printNumber,saleRequest);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/async/sale/{printNumber}")
    public DeferredResult<JsonNode> asyncSale(@PathVariable String printNumber, @RequestBody SaleRequest saleRequest){
        DeferredResult<JsonNode> result = new DeferredResult<>();
        saleService.asyncSale(printNumber,saleRequest).whenComplete((baseResponse, throwable) -> {
            if(throwable!=null){
                result.setErrorResult(throwable);
            }else {
                result.setResult(Json.toJson(baseResponse));
            }
        });
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/async/inquiry/{printNumber}")
    public DeferredResult<JsonNode> asyncInquiry(@PathVariable String printNumber){
        DeferredResult<JsonNode> result = new DeferredResult<>();

/*        balanceService.asyncInquiry(printNumber).thenApply(baseResponse -> {
           result.setResult(Json.toJson(baseResponse));
            return result;
        });*/

        balanceService.asyncInquiry(printNumber).whenComplete((baseResponse, throwable) -> {
            if(throwable!=null){
                result.setErrorResult(throwable);
            }else {
                result.setResult(Json.toJson(baseResponse));
            }
        });

        return result;
    }
}
