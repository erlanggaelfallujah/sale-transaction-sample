package dev.ranggalabs.common.util;

/**
 * Created by erlangga on 4/25/2017.
 */
public enum ResponseCode {

    APPROVED("00","Approved."),
    CARD_NOT_FOUND("01","Kartu tidak ditemukan"),
    CARD_NOT_ACTIVE("02","Kartu tidak aktif"),
    ACCOUNT_NOT_FOUND("03","Akun tidak ditemukan"),
    ACCOUNT_NOT_ACTIVE("04","Akun tidak aktif"),
    CIF_BALANCE_NOT_FOUND("05","Cif balance tidak ditemukan"),
    AMOUNT_EMPTY("06","Amount tidak boleh kosong"),
    INSUFFICIENT_BALANCE("07","Balance tidak mencukupi"),
    SYSTEM_ERROR("XX","System Error")
    ;

    private String code, detail;
    ResponseCode(String code, String detail) {
        this.code = code;
        this.detail = detail;
    }
    public String getCode() { return this.code; }
    public String getDetail() { return this.detail;}
}
