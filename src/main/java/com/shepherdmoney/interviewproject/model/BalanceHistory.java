package com.shepherdmoney.interviewproject.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@ApiModel(description = "Balance history of a credit card")
@RequiredArgsConstructor
@Table(name = "BalanceHistory", indexes = {
        @Index(name = "idx_date", columnList = "date")
})
public class BalanceHistory {

    @Id
    @ApiModelProperty(notes = "The unique ID of the history")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ApiModelProperty(notes = "The date of history")
    private LocalDate date;

    @ApiModelProperty(notes = "The balance")
    private double balance;

    @ApiModelProperty(notes = "The credit card that this balance history is assiciated with")
    @ManyToOne
    @JoinColumn(name = "creditCard_id")
    private CreditCard creditCard;
}
