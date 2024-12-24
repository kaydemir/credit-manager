package com.ingbank.credit_manager.service;

import com.ingbank.credit_manager.entity.Customer;

public interface CustomerService {
    Customer findByUsername(String username);
}
