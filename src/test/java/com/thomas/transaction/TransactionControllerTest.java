package com.thomas.transaction;

import com.google.gson.Gson;
import com.thomas.transaction.transaction.App;
import com.thomas.transaction.transaction.Transaction;
import com.thomas.transaction.transaction.TransactionController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.PrintWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Thomas on 7/21/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebIntegrationTest
public class TransactionControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    Gson gson = new Gson();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void test() throws Exception {

        String getTransactionNumberUrl = new StringBuilder()
                .append("/")
                .append(TransactionController.BASE_URI)
                .append("/{transactionId}")
                .toString();

        int transactionId = 1;

        mockMvc.perform(get(getTransactionNumberUrl, transactionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();
    }


    @Test
    public void createTransaction() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"currencyCode\": 7}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void createWhenTextFileIsEmpty() throws Exception {

        File file = new File("transactions.txt");
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"currencyCode\": 7}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void createWhenNoTxtFile() throws Exception {
        File file = new File("transactions.txt");
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
        file.delete();

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"currencyCode\": 7}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void deleteTransaction() throws Exception{

        MvcResult result = mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"currencyCode\": 7}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Transaction transaction = gson.fromJson(content, Transaction.class);

        mockMvc.perform(delete("/api/v1/transactions/{transactionId}", transaction.getTransactionId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"currencyCode\": 7}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();

        String getTransactionNumberUrl = new StringBuilder()
                .append("/")
                .append(TransactionController.BASE_URI)
                .append("/{transactionId}")
                .toString();

        int transactionId = transaction.getTransactionId();

        mockMvc.perform(get(getTransactionNumberUrl, transactionId))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }
}
