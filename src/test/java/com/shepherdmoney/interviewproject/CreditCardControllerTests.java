package com.shepherdmoney.interviewproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shepherdmoney.interviewproject.controller.CreditCardController;
import com.shepherdmoney.interviewproject.exception.BusinessException;
import com.shepherdmoney.interviewproject.response.ResponseEnum;
import com.shepherdmoney.interviewproject.service.CreditCardService;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CreditCardControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CreditCardService creditCardService;

    @InjectMocks
    private CreditCardController creditCardController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(creditCardController).build();
    }

    // Example test for addCreditCardToUser
    @Test
    public void addCreditCardToUser_Successful() throws Exception {
        // Arrange
        AddCreditCardToUserPayload payload = new AddCreditCardToUserPayload();
        given(creditCardService.createCardForUser(any(AddCreditCardToUserPayload.class))).willReturn(1);

        // Act & Assert
        mockMvc.perform(post("/credit-card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void addCreditCardToUser_Failure() throws Exception {
        // Arrange
        AddCreditCardToUserPayload payload = new AddCreditCardToUserPayload();
        given(creditCardService.createCardForUser(any(AddCreditCardToUserPayload.class)))
                .willThrow(new BusinessException(ResponseEnum.USER_NOT_FOUND));

        // Act & Assert
        mockMvc.perform(post("/credit-card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(payload)))
                .andExpect(status().isBadRequest());
    }

    // Test cases for getAllCardOfUser

    @Test
    public void getAllCardOfUser_Successful() throws Exception {
        // Arrange
        int userId = 1;
        List<CreditCardView> creditCardViews = new ArrayList<>();
        creditCardViews.add(new CreditCardView("Bank of America", "123"));
        given(creditCardService.getCreditCardsByUserId(userId)).willReturn(creditCardViews);

        // Act & Assert
        mockMvc.perform(get("/credit-card:all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists()); // Check that an element exists in the response array
    }

    @Test
    public void getAllCardOfUser_NoCards() throws Exception {
        // Arrange
        int userId = 1;
        given(creditCardService.getCreditCardsByUserId(userId)).willReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/credit-card:all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().string("[]")); // Expecting an empty array
    }

    // Test cases for getUserIdForCreditCard

    @Test
    public void getUserIdForCreditCard_Successful() throws Exception {
        // Arrange
        String cardNumber = "1234";
        int userId = 1;
        given(creditCardService.getUserByCreditCardNumber(cardNumber)).willReturn(userId);

        // Act & Assert
        mockMvc.perform(get("/credit-card:user-id")
                        .param("creditCardNumber", cardNumber))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(userId)));
    }

    @Test
    public void getUserIdForCreditCard_NotFound() throws Exception {
        // Arrange
        String cardNumber = "1234";
        given(creditCardService.getUserByCreditCardNumber(cardNumber))
                .willThrow(new BusinessException(ResponseEnum.PARAM_EXCEPTION));

        // Act & Assert
        mockMvc.perform(get("/credit-card:user-id")
                        .param("creditCardNumber", cardNumber))
                .andExpect(status().isBadRequest());
    }

    // Test cases for updateCreditCardBalance

    @Test
    public void updateCreditCardBalance_Successful() throws Exception {
        // Arrange
        UpdateBalancePayload[] payloads = { new UpdateBalancePayload() }; // Populate with real data as needed
        given(creditCardService.updateBalanceHistory(payloads)).willReturn("Update successful");

        // Act & Assert
        mockMvc.perform(post("/credit-card:update-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(payloads)))
                .andExpect(status().isOk())
                .andExpect(content().string("Update successful"));
    }

    @Test
    public void updateCreditCardBalance_Failure() throws Exception {
        // Arrange
        UpdateBalancePayload[] payloads = { new UpdateBalancePayload() }; // Populate with real data as needed
        given(creditCardService.updateBalanceHistory(payloads))
                .willThrow(new BusinessException(ResponseEnum.PARAM_EXCEPTION));

        // Act & Assert
        mockMvc.perform(post("/credit-card:update-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(payloads)))
                .andExpect(status().isBadRequest());
    }


    // Utility method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

