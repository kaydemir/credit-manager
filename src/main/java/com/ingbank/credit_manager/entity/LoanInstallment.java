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
public class LoanInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_installment_id_seq_generator")
    @SequenceGenerator(name = "loan_installment_id_seq_generator", sequenceName = "loan_installment_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loanId", nullable = false)
    private Loan loan;
    @Column(precision = 20, scale = 2)
    private BigDecimal amount;
    @Column(precision = 20, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Boolean isPaid = false;
}
