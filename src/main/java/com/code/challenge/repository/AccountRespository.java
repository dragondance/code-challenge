package com.code.challenge.repository;

import com.code.challenge.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

// Interfaz para que se conectara con la base de datos y devolverá la información de una cuenta.
public interface AccountRespository extends JpaRepository<Account, String> {

}
