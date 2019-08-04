package com.code.challenge.steps;

import com.code.challenge.entities.Transaction;
import com.code.challenge.models.TransactionStatus;
import com.code.challenge.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gherkin.deps.com.google.gson.Gson;
import org.apache.http.HttpResponse;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.abs;

public class statusTransactionStep {

    private Transaction transaction;
    private Boolean isStored;
    private Util util = new Util();
    private String channel;
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private TransactionStatus transactionStatus;

    @Given("^A transaction that is (stored|not stored) in our system$")
    public void a_transaction_that_it_not_stored_in_our_system(String stored) throws Throwable {
        isStored = stored.equals("stored");

        /**
         * Vamos a generar una trasaccion dummy y el Given nos dira si la queremos almacenar o no
        **/
        transaction = new Transaction();
        transaction.setReference("TEST" + util.getAlphaNumeric(4));
        transaction.setAccount_iban("ES6220389439049641732642");
        transaction.setAmmount(5323.42);
        transaction.setFee(23.43);
        transaction.setDescription("Payment for Test");
    }

    @When("^I check the status from (any|CLIENT|ATM|INTERNAL) channel$")
    public void i_check_the_status_from_channel(String ch) throws Throwable {
        // recuperamos el canal para hacer la comprobación del estado
        channel = ch;
    }

    @When("^the transaction date is (before|equals to|greater than) today$")
    public void check_the_transaction_date(String compare) throws Throwable {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);

        // Preparamos una fecha con los diferentes casso de prueba para setear a la transacción
        switch (compare) {
            case "before":
                c.add(Calendar.DATE, -1);
                break;
            case "greater than":
                c.add(Calendar.DATE, 1);
                break;
        }

        transaction.setDate(c.getTime());

        if (isStored == true) {
            // Con tada la información generada podemos guardar la transacción en nuestro sistema
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(transaction);

            HttpPost request = new HttpPost("http://localhost:8080/create");
            StringEntity entity = new StringEntity(json);
            request.addHeader("content-type", "application/json");
            request.setEntity(entity);
            HttpResponse httpresponse = httpClient.execute(request);
        }
    }

    @Then("^The system returns the status '(.*)'$")
    public void the_system_returns_the_status(String status) throws Throwable {
        //Hacemos la llamada para recuperar el etado de la transacción y comparamos los datos obtenidos
        HttpGet request = new HttpGet("http://localhost:8080/status/" + transaction.getReference() + "/" + channel);
        HttpResponse httpresponse = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String body = handler.handleResponse(httpresponse);

        Gson gson=new Gson();
        transactionStatus = gson.fromJson(body, TransactionStatus.class);

        Assert.assertEquals(transactionStatus.getStatus().toString(), status);
    }


    @Then("^the amount substracting the fee$")
    public void the_amount_substracting_the_fee() throws Throwable {
        Double amount = transaction.getAmmount() - abs(transaction.getFee());
        Assert.assertEquals(amount, transactionStatus.getAmount());
    }

    @Then("^the amount And the fee$")
    public void the_amount_And_the_fee() throws Throwable {
        Assert.assertEquals(transaction.getAmmount(), transactionStatus.getAmount());
        Assert.assertEquals(transaction.getFee(), transactionStatus.getFee());
    }
}
