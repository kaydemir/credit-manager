package com.ingbank.credit_manager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ingbank.credit_manager.entity.Customer;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LoanInstallmentRepositoryTest {

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @BeforeEach
    void setup() {
        Loan loan1 = new Loan();
        Customer customer = new Customer();
        customer.setId(1L);
        loan1.setCustomer(customer);
        loan1.setLoanAmount(BigDecimal.valueOf(1500));
        loan1.setIsPaid(false);
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        Customer customer2 = new Customer();
        customer2.setId(2L);
        loan2.setCustomer(customer2);
        loan2.setLoanAmount(BigDecimal.valueOf(2000));
        loan2.setIsPaid(false);
        loanRepository.save(loan2);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setLoan(loan1);
        installment1.setAmount(BigDecimal.valueOf(1000));
        installment1.setDueDate(LocalDate.now().plusDays(10));
        installment1.setIsPaid(false);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setLoan(loan1);
        installment2.setAmount(BigDecimal.valueOf(500));
        installment2.setDueDate(LocalDate.now().plusDays(20));
        installment2.setIsPaid(true);

        LoanInstallment installment3 = new LoanInstallment();
        installment3.setLoan(loan2);
        installment3.setAmount(BigDecimal.valueOf(1500));
        installment3.setDueDate(LocalDate.now().plusDays(30));
        installment3.setIsPaid(false);

        loanInstallmentRepository.saveAll(List.of(installment1, installment2, installment3));
    }

    @AfterEach
    void tearDown() {
        loanInstallmentRepository.deleteAll();
        loanRepository.deleteAll();
    }

    @Test
    void testFindByLoanId() {
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(1L);

        assertThat(installments).hasSize(2);
        assertThat(installments).extracting(LoanInstallment::getAmount)
                .containsExactlyInAnyOrder(BigDecimal.valueOf(1000), BigDecimal.valueOf(500));
    }
}
