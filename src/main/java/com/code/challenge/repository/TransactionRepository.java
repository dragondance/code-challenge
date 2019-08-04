package com.code.challenge.repository;

import com.code.challenge.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//Interfaz que se conectara con la base de datos y devolvera los datos de una transacción
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Cremos un filtro personalizado para obtener las transacciones por el código iban ordenado por cantidad
    // como la palabra asc es reservada no hemos podido parametrizar la ordenación por SQL.
    @Query(
            value = "SELECT * FROM transaction WHERE account_iban = :iban ORDER BY ammount asc",
            nativeQuery = true
    )
    List<Transaction> filterByIban(@Param("iban") String iban);
}
