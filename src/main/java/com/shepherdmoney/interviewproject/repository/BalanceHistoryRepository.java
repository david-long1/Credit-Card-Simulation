package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository("BalanceHistoryRepo")
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Integer> {
    @Modifying
    @Query("UPDATE BalanceHistory bh SET bh.balance = bh.balance + :difference WHERE bh.date > :date AND bh.creditCard = :creditCard")
    void updateBalancesAfterDate(@Param("date") LocalDate date,
                                 @Param("difference") double difference,
                                 @Param("creditCard") CreditCard creditCard);

    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.creditCard = :creditCard AND bh.date = :date")
    Optional<BalanceHistory> getByCreditCardAndDate(@Param("creditCard") CreditCard creditCard, @Param("date") LocalDate date);

    @Query("SELECT MIN(bh.date) FROM BalanceHistory bh WHERE bh.creditCard = :creditCard")
    Optional<LocalDate> findEarliestDateByCreditCard(@Param("creditCard") CreditCard creditCard);

    boolean existsByCreditCardAndDate(CreditCard creditCard, LocalDate date);

    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.creditCard = :creditCard AND bh.date < :date ORDER BY bh.date DESC LIMIT 1")
    Optional<BalanceHistory> findTopByCreditCardAndDateBeforeOrderByDateDesc(@Param("creditCard") CreditCard creditCard, @Param("date") LocalDate date);
}
