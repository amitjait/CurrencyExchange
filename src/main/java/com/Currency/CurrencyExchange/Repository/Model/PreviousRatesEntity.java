package com.Currency.CurrencyExchange.Repository.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private String id;

    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "previous_rate", nullable = false)
    private double rate;

    @ManyToOne
    @JsonIgnore
    private CurrencyEntity currency;
}
