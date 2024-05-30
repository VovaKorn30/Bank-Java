package com.example.bank_api.service;

import com.example.bank_api.dto.CardDto;


public interface CardService {


    CardDto save(Long clientId, Long accountId);


    void delete(Long clientId, Long cardId);
}
