package dev.ranggalabs.restapi.controller;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.restapi.service.BalanceService;
import dev.ranggalabs.restapi.service.SaleService;
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
    public DeferredResult<BaseResponse> asyncSale(@PathVariable String printNumber, @RequestBody SaleRequest saleRequest){
        DeferredResult<BaseResponse> result = new DeferredResult<>();
        saleService.asyncSale(printNumber,saleRequest).whenComplete((baseResponse, throwable) -> {
            if(throwable!=null){
                result.setErrorResult(throwable);
            }else {
                result.setResult(baseResponse);
            }
        });
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/async/inquiry/{printNumber}")
    public DeferredResult<BaseResponse> asyncInquiry(@PathVariable String printNumber){
        DeferredResult<BaseResponse> result = new DeferredResult<>();

/*        balanceService.asyncInquiry(printNumber).thenApply(baseResponse -> {
           result.setResult(Json.toJson(baseResponse));
            return result;
        });*/

        balanceService.asyncInquiry(printNumber).whenComplete((baseResponse, throwable) -> {
            if(throwable!=null){
                result.setErrorResult(throwable);
            }else {
                result.setResult(baseResponse);
            }
        });

        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/obs/inquiry/{printNumber}")
    public DeferredResult<BaseResponse> inquiryObsV3(@PathVariable String printNumber){
        Observable<BaseResponse> o = balanceService.inquiryObsV3(printNumber);
        DeferredResult<BaseResponse> deffered = new DeferredResult<>();
        o.subscribe(m->deffered.setResult(m),e->deffered.setErrorResult(e));
        return deffered;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/obs/sale/{printNumber}")
    public DeferredResult<BaseResponse> saleObsV3(@PathVariable String printNumber, @RequestBody SaleRequest saleRequest){
        DeferredResult<BaseResponse> result = new DeferredResult<>();
        Observable<BaseResponse> o = saleService.saleObsV3(printNumber,saleRequest);
        o.subscribe(m->result.setResult(m),e->result.setErrorResult(e));
        return result;
    }
}
