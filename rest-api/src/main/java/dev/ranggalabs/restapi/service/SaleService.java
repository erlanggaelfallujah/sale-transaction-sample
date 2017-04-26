package dev.ranggalabs.restapi.service;

import dev.ranggalabs.common.dto.BaseResponse;
import dev.ranggalabs.common.dto.SaleRequest;

/**
 * Created by erlangga on 4/26/2017.
 */
public interface SaleService {
    BaseResponse sale(String printNumber, SaleRequest saleRequest);
}
