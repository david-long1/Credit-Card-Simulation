package com.shepherdmoney.interviewproject.service;

import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;

import java.util.List;

public interface CreditCardService {

    /**
     * Get all credit cards of a user according user id
     * @param userId
     * @return A list of CreditCardView which contains the credit card details associated with the given user id
     */
    List<CreditCardView> getCreditCardsByUserId(int userId);

    /**
     * Get user according to a given credit card number
     * @param cardNumber
     * @return The created credit card id
     */
    Integer getUserByCreditCardNumber(String cardNumber);

    /**
     * Update the balance history of credit card(s) based on payloads
     * @param payloads
     * @return 200 OK if success, 400 Bad Request otherwise
     */
    String updateBalanceHistory(UpdateBalancePayload[] payloads);

    /**
     * Create a credit card entity, and then associate that credit card with user with given userId
     * @param payload
     * @return userId if successful
     */
    Integer createCardForUser(AddCreditCardToUserPayload payload);
}
