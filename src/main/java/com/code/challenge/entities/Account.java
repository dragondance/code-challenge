package com.code.challenge.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
// Considemos que debemos de tener guardada la cuenta para poder realizar las operaciones de la transacción sobre ella
public class Account {
    @Id
    private String iban;
    private Double ammount;
}
