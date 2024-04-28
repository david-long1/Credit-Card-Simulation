package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Crud repository to store credit cards
 */
@Repository("CreditCardRepo")
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    @Query("SELECT cc FROM CreditCard cc WHERE cc.number = :cardNumber")
    Optional<CreditCard> findByCardNumber(String cardNumber);

    @Query("SELECT cc.user.id FROM CreditCard cc WHERE cc.number = :cardNumber")
    Optional<Integer> findUserByCreditCardNumber(@Param("cardNumber") String cardNumber);

    @Query("SELECT c FROM CreditCard c WHERE c.user.id = :userId")
    List<CreditCard> findByUserId(@Param("userId") int userId);
}
