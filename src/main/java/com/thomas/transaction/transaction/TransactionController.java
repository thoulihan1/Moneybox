package com.thomas.transaction.transaction;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Thomas on 7/20/17.
 */

@RestController
@RequestMapping(TransactionController.BASE_URI)
public class TransactionController {

    public static final String BASE_URI = "api/v1/transactions";

    Gson gson = new Gson();

    //Create new transaction
    @RequestMapping(method = POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTransaction(@RequestBody Transaction t) {
        File file = new File("transactions.txt");
        System.out.println("File length: " + file.length());

        ArrayList<Transaction> transactions;

        int maxId = 0;
        if (file.length() == 0 || file.length() == 2) {
            transactions = new ArrayList<Transaction>();
        } else {
            transactions = readTransactions();

            for(Transaction tran: transactions){
                if(tran.getTransactionId()>maxId)
                    maxId = tran.getTransactionId();
            }
        }



        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        t.setCreatedDate(dateFormat.format(date));
        t.setTransactionId(maxId+1);
        transactions.add(t);

        writeTransactions(transactions);

        return new ResponseEntity<String>(gson.toJson(t), HttpStatus.CREATED);
    }

    //Delete a transaction
    @RequestMapping(value = "/{transactionId}", method = DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteTransaction(@PathVariable final int transactionId){

        ArrayList<Transaction> transactions = readTransactions();
        Transaction transactionToDelete = null;
        for(Transaction t : transactions){
            if(t.getTransactionId()==transactionId)
                transactionToDelete = t;
        }

        if(transactionToDelete==null)
            return new ResponseEntity<String>("{\"response\":\"404 - transaction not found\"}", HttpStatus.NOT_FOUND);


        transactions.remove(transactionToDelete);

        writeTransactions(transactions);
        return new ResponseEntity<String>(gson.toJson(transactionToDelete), HttpStatus.OK);
    }

    //Get all transactions
    @RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllTransactions() {

        return new ResponseEntity<String>(gson.toJson((readTransactions())), HttpStatus.OK);
    }

    //Get a transaction
    @RequestMapping(value = "/{transactionId}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTransactionById(@PathVariable final int transactionId){

        ArrayList<Transaction> transactions = readTransactions();

        Transaction toDisplay = null;
        for(Transaction t : transactions){
            if(t.getTransactionId()==transactionId){
                toDisplay = t;
            }
        }

        if(toDisplay!=null){
            return new ResponseEntity<String>(gson.toJson(toDisplay), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("{\"response\":\"404 - transaction not found\"}", HttpStatus.NOT_FOUND);
        }
    }

    //Update a transaction
    @RequestMapping(value = "/{transactionId}", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTransaction(@PathVariable final int transactionId, @RequestBody Transaction t){
        ArrayList<Transaction> transactions = readTransactions();

        Transaction toUpdate = null;

        for(Transaction trans : transactions){
            if(trans.getTransactionId()==transactionId){
                toUpdate = trans;
            }
        }

        if(toUpdate==null)
            return new ResponseEntity<String>("{\"response\":\"404 - transaction not found\"}", HttpStatus.NOT_FOUND);


        transactions.remove(toUpdate);
        toUpdate.setTransactionId(transactionId);

        if(t.getCurrencyCode()!=0){
            toUpdate.setCurrencyCode(t.getCurrencyCode());
        }
        if(t.getTransactionAmount()!=0){
            toUpdate.setTransactionAmount(t.getTransactionAmount());
        }
        if(t.getTransactionDate()!=null){
            toUpdate.setTransactionDate(t.getTransactionDate());
        }
        if(t.getCreatedDate()!=null){
            toUpdate.setCreatedDate(t.getCreatedDate());
        }


        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        toUpdate.setModifiedDate(dateFormat.format(date));

        transactions.add(toUpdate);

        writeTransactions(transactions);

        return new ResponseEntity<String>(gson.toJson(toUpdate), HttpStatus.OK);
    }

    public void writeTransactions(ArrayList<Transaction> transactions){
        JSONArray jsonArray = new JSONArray();
        for(Transaction t : transactions){
            JsonElement n = gson.toJsonTree(t);
            jsonArray.add(n);
        }

        try {
            PrintWriter writer = new PrintWriter("transactions.txt", "UTF-8");
            writer.print(String.valueOf(jsonArray));
            writer.close();
        } catch (IOException e) {

        }

    }

    public ArrayList<Transaction> readTransactions() {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray;
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        try {
            jsonArray = (JSONArray) parser.parse(new FileReader("transactions.txt"));

            Iterator iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                Transaction t = gson.fromJson(iterator.next().toString(), Transaction.class);
                transactions.add(t);
            }
        } catch (IOException e) {
            System.out.println("readTransactions(): " + e.toString());

        } catch (ParseException e) {
            System.out.println("readTransactions(): " + e.toString());
        }
        return transactions;
    }
}
