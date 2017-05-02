package dev.ranggalabs.restapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.restapi.model.CardValidation;
import dev.ranggalabs.restapi.service.BalanceService;
import dev.ranggalabs.restapi.service.SaleService;
import dev.ranggalabs.restapi.util.Json;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

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

    @RequestMapping(method = RequestMethod.POST, value = "/async-obs/sale/{printNumber}")
    public DeferredResult<BaseResponse> asyncSaleObs(@PathVariable String printNumber, @RequestBody SaleRequest saleRequest){
        Observable<BaseResponse> baseResponseObservable = saleService.asyncSaleObs(printNumber,saleRequest);
        DeferredResult<BaseResponse> baseResponseDeferredResult = new DeferredResult<>();
        baseResponseObservable.subscribe(s->baseResponseDeferredResult.setResult(s),e->baseResponseDeferredResult.setErrorResult(e));
        return baseResponseDeferredResult;
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

    @RequestMapping(method = RequestMethod.GET, value = "/async-obs/inquiry/{printNumber}")
    public DeferredResult<BaseResponse> asyncInquiryObs(@PathVariable String printNumber){
        Observable<BaseResponse> o = balanceService.asyncInquiryObs(printNumber);
        DeferredResult<BaseResponse> deffered = new DeferredResult<>();
        o.subscribe(m->deffered.setResult(m),e->deffered.setErrorResult(e));
        return deffered;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/async-obs/validation/{printNumber}")
    public DeferredResult<CardValidation> asyncCardValidationObs(@PathVariable String printNumber){
        Observable<CardValidation> cardValidation = balanceService.asyncCardValidation(printNumber);
        DeferredResult<CardValidation> deffered = new DeferredResult<>();
        cardValidation.subscribe(m->deffered.setResult(m),e->deffered.setErrorResult(e));
        return deffered;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/v2/async-obs/inquiry/{printNumber}")
    public DeferredResult<BaseResponse> asyncInquiryObsV2(@PathVariable String printNumber){
        Observable<BaseResponse> o = balanceService.inquiryObs(printNumber);
        DeferredResult<BaseResponse> deffered = new DeferredResult<>();
        o.subscribe(m->deffered.setResult(m),e->deffered.setErrorResult(e));
        return deffered;
    }

}
