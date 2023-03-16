package com.Currency.CurrencyExchange.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString
@Table(name = "Previous_Rates")
public class PreviousRatesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date", columnDefinition = "DATE")
    private String date;

    @Column(name = "previous_rate", nullable = false)
    private int rate;

    @ManyToOne
    @JsonIgnore
    private CurrencyEntity currency;
}
