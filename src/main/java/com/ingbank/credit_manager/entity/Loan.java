package com.ingbank.credit_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_id_seq_generator")
    @SequenceGenerator(name = "loan_id_seq_generator", sequenceName = "loan_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;
    @Column(precision = 20, scale = 2)
    private BigDecimal loanAmount;
    private Integer numberOfInstallments;
    private LocalDate createDate = LocalDate.now();
    private Boolean isPaid = false;
}
