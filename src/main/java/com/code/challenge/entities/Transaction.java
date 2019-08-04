package com.code.challenge.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Entity
// Entidad para guardar los datos de la transacción.
public class Transaction {
    @Id
    private String reference;
    @NotNull
    private String account_iban;
    private Date date;
    @NotNull
    private Double ammount;
    private Double fee;
    private String description;
}
