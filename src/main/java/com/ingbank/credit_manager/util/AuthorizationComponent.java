package com.ingbank.credit_manager.util;

import com.ingbank.credit_manager.entity.Customer;
import com.ingbank.credit_manager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationComponent {

    CustomerService customerService;

    @Autowired
    public AuthorizationComponent(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void checkAccess(Long customerId, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // Ensure the user is accessing their own data
            Customer customer = customerService.findByUsername(username);
            if (customer == null || !customer.getId().equals(customerId)) {
                throw new AccessDeniedException("You are not authorized to access this data.");
            }
        }
    }
}
