package com.shepherdmoney.interviewproject.serviceImpl;

import com.shepherdmoney.interviewproject.exception.BusinessException;
import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.BalanceHistoryRepository;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.response.ResponseEnum;
import com.shepherdmoney.interviewproject.service.CreditCardService;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager; // Inject the EntityManager

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;


    @Override
    public List<CreditCardView> getCreditCardsByUserId(int userId) {
        // Find all credit cards associated with userId
        List<CreditCard> cards = creditCardRepository.findByUserId(userId);
        //map the list of credit cards to list of CreditCardView
        return cards.stream()
                .map(card -> new CreditCardView(card.getIssuanceBank(), card.getNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public Integer getUserByCreditCardNumber(String cardNumber) {
        creditCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new BusinessException(ResponseEnum.CARD_NOT_FOUND));
        return creditCardRepository.findUserByCreditCardNumber(cardNumber)
                .orElseThrow(() -> new BusinessException(ResponseEnum.PARAM_EXCEPTION));
    }

    /**
     * Creates a new credit card record for a user based on the provided payload details.
     * The method will throw a BusinessException with USER_NOT_FOUND if the user id provided
     * in the payload does not correspond to an existing user.
     *
     * @param payload the payload containing details necessary to create a new credit card,
     *                including the user id, card issuance bank, and card number.
     * @return the id of the newly created credit card record.
     * @throws BusinessException if the user id in the payload does not match any existing user.
     */
    @Override
    public Integer createCardForUser(AddCreditCardToUserPayload payload) {
        User user = userRepository.findById(payload.getUserId())
                .orElseThrow(() -> new BusinessException(ResponseEnum.USER_NOT_FOUND));
        CreditCard card = new CreditCard();
        card.setIssuanceBank(payload.getCardIssuanceBank());
        card.setNumber(payload.getCardNumber());
        card.setUser(user);
        card = creditCardRepository.saveAndFlush(card);
        return card.getId();
    }

    @Override
    @Transactional
    public String updateBalanceHistory(UpdateBalancePayload[] payloads) {
        for (UpdateBalancePayload payload : payloads) {
            String number = payload.getCreditCardNumber();
            LocalDate payloadDate = payload.getBalanceDate();
            Double payloadAmount = payload.getBalanceAmount();

            // Find the credit card corresponding to card number in payload
            CreditCard creditCard = creditCardRepository.findByCardNumber(number)
                    .orElseThrow(() -> new BusinessException(ResponseEnum.PARAM_EXCEPTION));

            // If there are gaps between two balance dates, fill the empty date with the balance of the previous date
            fillGapsInBalanceHistory(creditCard);

            // Update all subsequent balances after payload date
            updateBalancesAfterPayloadDate(payloadDate, payloadAmount, creditCard);

            // After each update operation, you can immediately synchronize the persistence context to the database
            entityManager.flush();
            // Clear the persistence context to avoid any potential issues with cached entities
            entityManager.clear();
        }

        return "200 OK";
    }

    /**
     * Helper method to create and configure a new BalanceHistory object.
     *
     * @param date        The date for the balance history entry.
     * @param balance     The balance amount for the entry.
     * @param creditCard  The credit card associated with this balance history.
     * @return A new instance of BalanceHistory with the provided values.
     */
    private BalanceHistory createNewBalanceHistory(LocalDate date, Double balance, CreditCard creditCard) {
        BalanceHistory newHistory = new BalanceHistory();
        newHistory.setDate(date);
        newHistory.setBalance(balance);
        newHistory.setCreditCard(creditCard);
        return newHistory;
    }

    /**
     * Fills gaps in the balance history for a specific credit card. This method ensures that every date
     * between the earliest and latest entries in the balance history has a record. If a date is missing,
     * this method creates a new balance history entry using the balance from the most recent prior date.
     *
     * @param creditCard The credit card for which to fill balance history gaps.
     */
    private void fillGapsInBalanceHistory(CreditCard creditCard) {
        // Retrieve the earliest and latest date in the balance history for the given credit card
        LocalDate startDate = balanceHistoryRepository.findEarliestDateByCreditCard(creditCard)
                .orElseThrow(() -> new BusinessException(ResponseEnum.PARAM_EXCEPTION));
        LocalDate endDate = balanceHistoryRepository.findLatestDateByCreditCard(creditCard)
                .orElseThrow(() -> new BusinessException(ResponseEnum.PARAM_EXCEPTION));
        List<BalanceHistory> gapHistories = new ArrayList<>();

        // We start from the day after the earliest date and go up to the latest date (inclusive)
        for (LocalDate date = startDate.plusDays(1); date.isBefore(endDate); date = date.plusDays(1)) {
            boolean exists = balanceHistoryRepository.existsByCreditCardAndDate(creditCard, date);

            if (!exists) {
                // Find the most recent balance before the gap
                BalanceHistory lastHistory = balanceHistoryRepository.findTopByCreditCardAndDateBeforeOrderByDateDesc(creditCard, date)
                        .orElseThrow(() -> new BusinessException(ResponseEnum.PARAM_EXCEPTION));

                BalanceHistory gapHistory = createNewBalanceHistory(date, lastHistory.getBalance(), creditCard);
                gapHistories.add(gapHistory);
            }
        }
        // Perform a bulk save operation
        balanceHistoryRepository.saveAll(gapHistories);
        balanceHistoryRepository.flush(); // Ensure all pending changes are applied to the database
    }

    /**
     * Updates the balance histories for a given credit card after a specific payload date.
     * If the balance history for the payload date does not exist, it will create a new entry.
     * It then updates all subsequent balance histories to reflect the new payload amount.
     *
     * @param payloadDate    The date from which the balance histories need to be updated.
     * @param payloadAmount  The new transaction amount to be accounted for.
     * @param creditCard     The credit card for which the balance histories are being updated.
     */
    private void updateBalancesAfterPayloadDate(LocalDate payloadDate,
                                                Double payloadAmount, CreditCard creditCard) {
        // Find the history for the payload date or create a new one if it doesn't exist.
        BalanceHistory payloadHistory = balanceHistoryRepository.getByCreditCardAndDate(creditCard, payloadDate)
                .orElseGet(() -> {
                    // Use the helper method to create a new history for payload date if it doesn't exist
                    BalanceHistory newHistory = createNewBalanceHistory(payloadDate, payloadAmount, creditCard);
                    balanceHistoryRepository.save(newHistory);
                    return newHistory;
                });

        // Calculate the difference and update subsequent histories
        double difference = payloadAmount - payloadHistory.getBalance();
        logger.debug("difference is {}", difference);
        if (difference != 0) {
            // Update the balance of the payload date's history.
            payloadHistory.setBalance(payloadAmount);
            // Persist changes and ensure they are immediately written to the database.
            balanceHistoryRepository.save(payloadHistory);
            // Update the balances of all history records after the payload date to account for the new payload amount.
            balanceHistoryRepository.updateBalancesAfterDate(payloadDate, difference, creditCard);
        }
    }

}
