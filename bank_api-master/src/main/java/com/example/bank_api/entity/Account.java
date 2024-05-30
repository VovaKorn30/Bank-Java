package com.example.bank_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "opening_date")
    private Date openingDate;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "account")
    private List<Card> cards;

    public Account(String number, Date openingDate, BigDecimal balance, List<Card> cards) {
        this.number = number;
        this.openingDate = openingDate;
        this.balance = balance;
        this.cards = cards;
    }

    public Account(Long id, String number, Date openingDate, BigDecimal balance, List<Card> cards) {
        this.id = id;
        this.number = number;
        this.openingDate = openingDate;
        this.balance = balance;
        this.cards = cards;
    }


    @JsonIgnore
    public Client getClient() {
        return client;
    }


    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", openingDate=" + openingDate +
                ", balance=" + balance +
                ", cards=" + cards +
                '}';
    }
}
