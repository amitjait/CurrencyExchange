package com.Currency.CurrencyExchange.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "rate", nullable = false)
    private int rate;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PreviousRatesEntity> previousRatesEntityList;


}
