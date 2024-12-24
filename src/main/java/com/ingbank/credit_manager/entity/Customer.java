package com.ingbank.credit_manager.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq_generator")
    @SequenceGenerator(name = "customer_id_seq_generator", sequenceName = "customer_id_seq", allocationSize = 1)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @Column(precision = 20, scale = 2)
    private BigDecimal creditLimit;
    @Column(precision = 20, scale = 2)
    private BigDecimal usedCreditLimit = BigDecimal.ZERO;
}
