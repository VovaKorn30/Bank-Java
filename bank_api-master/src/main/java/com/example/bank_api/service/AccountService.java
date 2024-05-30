package com.example.bank_api.service;

import com.example.bank_api.dto.AccountDto;

import java.math.BigDecimal;
import java.util.List;


public interface AccountService {


    List<AccountDto> findAll(Long clientId);


    AccountDto findById(Long clientId, Long accountId);


    AccountDto save(Long clientId);


    void updateAccountAddBalanceByCardNumber(Long clientId, String cardNumber, BigDecimal add);
}
