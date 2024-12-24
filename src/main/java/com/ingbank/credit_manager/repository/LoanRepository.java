package com.ingbank.credit_manager.repository;

import java.util.List;

import com.ingbank.credit_manager.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);

    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId" +
            " AND (:numberOfInstallments IS NULL OR l.numberOfInstallments = :numberOfInstallments)" +
            " AND (:isPaid IS NULL OR l.isPaid = :isPaid)")
    List<Loan> findLoans(@Param("customerId") Long customerId,
                         @Param("numberOfInstallments") Integer numberOfInstallments,
                         @Param("isPaid") Boolean isPaid);
}
