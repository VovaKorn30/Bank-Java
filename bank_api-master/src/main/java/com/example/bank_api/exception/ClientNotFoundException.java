package com.example.bank_api.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Не знайден клієнт по id=" + id);
    }
}
