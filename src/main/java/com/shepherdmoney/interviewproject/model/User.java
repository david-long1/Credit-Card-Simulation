package com.shepherdmoney.interviewproject.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@ApiModel(description = "Details about a user")
@Table(name = "MyUser")
public class User {

    @Id
    @ApiModelProperty(notes = "The unique ID of the user")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ApiModelProperty(required = true, notes = "The name of the user")
    private String name;

    @ApiModelProperty(required = true, notes = "The email of the user")
    private String email;

    // TODO: User's credit card
    // HINT: A user can have one or more, or none at all. We want to be able to query credit cards by user
    //       and user by a credit card.
    @ApiModelProperty(required = true, notes = "The credit card(s) of the user")
    @OneToMany(mappedBy = "user")
    private List<CreditCard> creditCards;
}
