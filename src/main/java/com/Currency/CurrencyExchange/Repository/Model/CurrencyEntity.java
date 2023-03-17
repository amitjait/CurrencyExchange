package com.Currency.CurrencyExchange.Repository.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Currency")
public class CurrencyEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "rate", nullable = false)
    private double rate;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PreviousRatesEntity> previousRatesEntityList;


}
