package com.shepherdmoney.interviewproject.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SortNatural;

import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@ApiModel(description = "Details about a credit card")
public class CreditCard {

    @Id
    @ApiModelProperty(notes = "The unique ID of the credit card")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ApiModelProperty(required = true, notes = "The bank that issued the credit card")
    private String issuanceBank;

    @ApiModelProperty(required = true, notes = "The number of the credit card")
    private String number;

    // TODO: Credit card's owner. For detailed hint, pleaCse see User class
    // Some field here <> owner;

    // TODO: Credit card's balance history. It is a requirement that the dates in the balanceHistory 
    //       list must be in chronological order, with the most recent date appearing first in the list. 
    //       Additionally, the last object in the "list" must have a date value that matches today's date, 
    //       since it represents the current balance of the credit card. For example:
    //       [
    //         {date: '2023-04-10', balance: 800},
    //         {date: '2023-04-11', balance: 1000},
    //         {date: '2023-04-12', balance: 1200},
    //         {date: '2023-04-13', balance: 1100},
    //         {date: '2023-04-16', balance: 900},
    //       ]
    // ADDITIONAL NOTE: For the balance history, you can use any data structure that you think is appropriate.
    //        It can be a list, array, map, pq, anything. However, there are some suggestions:
    //        1. Retrieval of a balance of a single day should be fast
    //        2. Traversal of the entire balance history should be fast
    //        3. Insertion of a new balance should be fast
    //        4. Deletion of a balance should be fast
    //        5. It is possible that there are gaps in between dates (note the 04-13 and 04-16)
    //        6. In the condition that there are gaps, retrieval of "closest" balance date should also be fast. Aka, given 4-15, return 4-16 entry tuple

    // Many-to-one relationship back to User
    @ApiModelProperty(required = true, notes = "The user associated with the credit card")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ApiModelProperty(required = true, notes = "The balance history of the credit card")
    @OneToMany(mappedBy = "creditCard", fetch = FetchType.LAZY)
    @SortNatural
    private SortedSet<BalanceHistory> balanceHistory = new TreeSet<>();
}
