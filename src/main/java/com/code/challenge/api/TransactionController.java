package com.code.challenge.api;

import com.code.challenge.entities.Account;
import com.code.challenge.entities.Transaction;
import com.code.challenge.models.TransactionStatus;
import com.code.challenge.models.TypeChannels;
import com.code.challenge.models.TypeStatus;
import com.code.challenge.repository.AccountRespository;
import com.code.challenge.repository.TransactionRepository;
import com.code.challenge.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.abs;


@RestController
public class TransactionController {
    Util utilMethods = new Util();

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRespository accountRespository;

    /**
     * Rest EndPoint para crear una transacción. Espera como entrada una Transacción enviada como JSON en el body
    **/
    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public void create(@RequestBody @Valid Transaction transaction) {
        // Si no recibimos la referencia generamos una nueva
        if (!Optional.ofNullable(transaction.getReference()).isPresent() || transaction.getReference().isEmpty()) {
            generateNewReference(transaction);
        }

        // Comprobamos que la referencia que hemos recivido / generado existe en el sistema
        if (!transactionRepository.findById(transaction.getReference()).isPresent()) {
            // Comprobamos que exista una cuenta sobre la que hacer las operaciones de esa tranferencia
            if (accountRespository.findById(transaction.getAccount_iban()).isPresent()) {
                // Realizamos las operaciones sobre la cuenta
                Account account = accountRespository.getOne(transaction.getAccount_iban());
                Double totalBalance = accountRespository.getOne(transaction.getAccount_iban()).getAmmount() + transaction.getAmmount();
                totalBalance += transaction.getFee() > 0 ? (-1) * transaction.getFee() : transaction.getFee();

                /*
                Si el balance total de la cuenta no es negativa despues de hacer todas la operaciones permitimos
                que se realice la transaccion, guardando los datos de la cuenta actualizados y guardando la transacción
                */
                if (totalBalance >= 0) {
                    transactionRepository.save(transaction);
                    account.setAmmount(totalBalance);
                    accountRespository.save(account);
                }
            }
        }
    }

    /**
     * Rest Endpoint para hacer la búsqueda de las transaciones. Espera como parte de la url parametros opcionales.
     * En este caso el IBAN y como se quiere la ordenación. Si no se le pasa ningún parametro realizará la búsqueda
     * sobre todas las transacciones y ordenará por defecto de forma ascendente.
    **/
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public List<Transaction> search(Optional<String> iban, Optional<String> order) {
        String typeOrder = order.isPresent() ? order.get() : "ASC";

        Comparator<Transaction> compare = Comparator.comparing(Transaction::getAmmount);
        List<Transaction> list;

        if (iban.isPresent()) {
            // Realizamos la busqueda por IBAN
            list = transactionRepository.filterByIban(iban.get());
        } else {
            // Recuperamos todas las transacciones
            list = transactionRepository.findAll();
        }

        // Realizamos la ordenación
        if (typeOrder.toUpperCase().equals("ASC")) {
            list.sort(compare);
        } else {
            list.sort(compare.reversed());
        }

        return list;
    }

    /**
     * Rest Endpoint para obtener el estado de una transacción. Como parte de la url se espera que se envie la
     * referencia y el canal. Este método devuelve el estado de la transacción.
    **/
    @RequestMapping(method = RequestMethod.GET, value = "/status/{reference}/{channel}")
    public TransactionStatus status(@PathVariable("reference") String refernce, @PathVariable("channel") String channel) {
        TransactionStatus transactionStatus = new TransactionStatus();

        // Buscamos la transacción por referencia en nuestro sistema
        Optional<Transaction> transaction = transactionRepository.findById(refernce);
        if (transaction.isPresent()) {

            // Seteamos la propiedades básicas a nuestro objeto de estado de la transacción
            transactionStatus.setReference(transaction.get().getReference());
            transactionStatus.setAmount(transaction.get().getAmmount());

            // Comprobamos el canal recibido para poder actualizar nuestro objeto transactionStatus.
            switch (TypeChannels.valueOf(channel)) {
                case ATM:
                case CLIENT:
                    transactionStatus.setAmount(transactionStatus.getAmount() - abs(transaction.get().getFee()));
                    break;
                case INTERNAL:
                    transactionStatus.setFee(transaction.get().getFee());
                    break;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            try {
                // Tranformamos la hora con el formato dd/MM/yyyy para eliminar de la comprobación la hora
                Date today = formatter.parse(formatter.format(new Date()));
                Date transactionDate = formatter.parse(formatter.format(transaction.get().getDate()));

                /*
                Dependiendo de la hora y el canal para el caso que sea la fecha de la trasacción mayor que la actual.
                Asignando el estado que corresponda
                */
                switch (transactionDate.compareTo(today)) {
                    case -1:
                        transactionStatus.setStatus(TypeStatus.SETTLED);
                        break;
                    case 0:
                        transactionStatus.setStatus(TypeStatus.PENDING);
                        break;
                    case 1:
                        switch (TypeChannels.valueOf(channel)) {
                            case ATM:
                                transactionStatus.setStatus(TypeStatus.PENDING);
                                break;
                            case CLIENT:
                            case INTERNAL:
                                transactionStatus.setStatus(TypeStatus.FUTURE);
                                break;
                        }
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            transactionStatus.setReference(refernce);
            transactionStatus.setStatus(TypeStatus.INVALID);
        }

        return transactionStatus;
    }

    /**
     * Creamos un método recursivo para la generación de una nueva referencia. Como la referencia que se genera es
     * aleatoria comprobamos si esta ya existe en el sistema y de ser así generamos una nueva.
    **/
    private void generateNewReference(Transaction transaction) {
        transaction.setReference(utilMethods.getAlphaNumeric(10));

        if (transactionRepository.findById(transaction.getReference()).isPresent()) {
            generateNewReference(transaction);
        }
    }
}

