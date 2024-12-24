package com.ingbank.credit_manager.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.ingbank.credit_manager.beans.PaymentResult;
import com.ingbank.credit_manager.entity.Customer;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;
import com.ingbank.credit_manager.exception.CustomerCreditLimitExceededException;
import com.ingbank.credit_manager.exception.LoanInstallmentsMoreThan3MonthsCannotBePaidException;
import com.ingbank.credit_manager.exception.LoanIsAlreadyFullyPaidException;
import com.ingbank.credit_manager.exception.NotFoundException;
import com.ingbank.credit_manager.repository.CustomerRepository;
import com.ingbank.credit_manager.repository.LoanRepository;
import com.ingbank.credit_manager.request.CreateLoanRequest;
import com.ingbank.credit_manager.request.PayLoanRequest;
import com.ingbank.credit_manager.service.LoanInstallmentService;
import com.ingbank.credit_manager.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentService loanInstallmentService;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository,
                           CustomerRepository customerRepository,
                           LoanInstallmentService loanInstallmentService) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.loanInstallmentService = loanInstallmentService;
        log.trace("{} initialized", this.getClass().getName());
    }

    public Loan createLoan(CreateLoanRequest request) {
        Long customerId = request.getCustomerId();
        BigDecimal amount = request.getAmount();
        Integer installments = request.getInstallments();
        BigDecimal interestRate = request.getInterestRate();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found with requested customerId: " + customerId));
        BigDecimal totalLoanAmount = amount.multiply(interestRate.add(BigDecimal.ONE)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalLimitUsage = customer.getUsedCreditLimit().add(totalLoanAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal availableLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).setScale(2, RoundingMode.HALF_UP);
        if (totalLimitUsage.compareTo(customer.getCreditLimit()) > 0) {
            log.error("Customer exceeds credit limit. Current limit: {}, Available limit: {}, Total limit usage: {}", customer.getCreditLimit(), availableLimit, totalLimitUsage);
            throw new CustomerCreditLimitExceededException("Customer exceeds credit limit. Current limit: " + customer.getCreditLimit() + ", Available limit: " + availableLimit + ", Total limit usage: " + totalLimitUsage);
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(totalLoanAmount);
        loan.setNumberOfInstallments(installments);
        loanRepository.save(loan);

        BigDecimal installmentAmount = totalLoanAmount.divide(BigDecimal.valueOf(installments), RoundingMode.HALF_UP);
        for (int i = 1; i <= installments; i++) {
            loanInstallmentService.saveLoanInstallment(loan, installmentAmount, LocalDate.now().plusMonths(i).withDayOfMonth(1) );
        }

        customer.setUsedCreditLimit(totalLimitUsage);
        customerRepository.save(customer);

        return loan;
    }

    @Override
    public List<Loan> listLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> listLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        return loanRepository.findLoans(customerId, numberOfInstallments, isPaid);
    }

    public PaymentResult payLoan(PayLoanRequest request) {
        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        Long loanId = request.getLoanId();
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new NotFoundException("Loan not found with request loanId: " + loanId));
        if (Boolean.TRUE.equals(loan.getIsPaid())) {
            throw new LoanIsAlreadyFullyPaidException("Loan is already fully paid");
        }

        List<LoanInstallment> allInstallmentsUnpaid = loanInstallmentService.listInstallmentsByLoanIdAndIsPaid(loanId, false);
        // Installments have due date that still more than 3 calendar months cannot be paid.
        List<LoanInstallment> installmentsUnpaidNext3Months = allInstallmentsUnpaid.stream()
                .filter(installment -> installment.getDueDate().isBefore(LocalDate.now().plusMonths(3)))
                .sorted(Comparator.comparing(LoanInstallment::getDueDate))
                .toList();

        if (!allInstallmentsUnpaid.isEmpty() && installmentsUnpaidNext3Months.isEmpty()) {
            throw new LoanInstallmentsMoreThan3MonthsCannotBePaidException("Loan installments more than 3 calendar months cannot be paid");
        }

        int installmentsPaid = 0;
        BigDecimal totalAmountSpent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (LoanInstallment installment : installmentsUnpaidNext3Months) {
            if (amount.compareTo(installment.getAmount()) < 0) {
                log.warn("amount to be paid {} is below of loan installment amount : {}", amount, installment.getAmount());
                break;
            }
            log.debug("Amount to be paid {} for installment {}", amount, installment);

            BigDecimal timeBasedInstallmentAmount = loanInstallmentService.getTimeBasedInstallmentAmount(installment.getDueDate(), installment.getAmount());

            if (amount.compareTo(timeBasedInstallmentAmount) >= 0) {
                installment.setPaidAmount(timeBasedInstallmentAmount);
                installment.setIsPaid(true);
                installment.setPaymentDate(LocalDate.now());

                amount = amount.subtract(timeBasedInstallmentAmount);
                totalAmountSpent = totalAmountSpent.add(timeBasedInstallmentAmount);
                installmentsPaid++;
            }
        }

        // Check if the loan is fully paid
        boolean loanFullyPaid = allInstallmentsUnpaid.stream().allMatch(LoanInstallment::getIsPaid);
        if (loanFullyPaid) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        loanInstallmentService.saveAllLoanInstallments(installmentsUnpaidNext3Months);

        return PaymentResult.builder().installmentsPaid(installmentsPaid).totalAmountSpent(totalAmountSpent).loanFullyPaid(loanFullyPaid).build();
    }
}
