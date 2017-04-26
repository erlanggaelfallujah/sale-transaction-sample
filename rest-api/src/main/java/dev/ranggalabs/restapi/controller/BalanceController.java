package dev.ranggalabs.restapi.controller;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;
import dev.ranggalabs.restapi.service.BalanceService;
import dev.ranggalabs.restapi.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
