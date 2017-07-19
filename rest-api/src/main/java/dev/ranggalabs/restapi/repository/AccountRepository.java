package dev.ranggalabs.restapi.repository;

import dev.ranggalabs.enitity.Account;

/**
 * Created by erlangga on 4/25/2017.
 */
public interface AccountRepository {
    Account findOneByCif(String cif);
    Account findOneByCifAsync(String cif);
}
