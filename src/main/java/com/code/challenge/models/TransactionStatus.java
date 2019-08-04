package com.code.challenge.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Modelo para guardar el estado de la transacción
public class TransactionStatus {
    private String reference;
    private Double amount;
    private Double fee;
    private TypeStatus status;
}
