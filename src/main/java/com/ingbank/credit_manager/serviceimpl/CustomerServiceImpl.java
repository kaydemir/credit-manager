package com.ingbank.credit_manager.serviceimpl;

import com.ingbank.credit_manager.entity.Customer;
import com.ingbank.credit_manager.entity.User;
import com.ingbank.credit_manager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final UserService userService;

    @Autowired
    public CustomerServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Customer findByUsername(String username) {
        User user = userService.findUserByUsername(username);
        if (user.getCustomer() == null) {
            throw new AccessDeniedException("User does not have an associated customer.");
        }
        return user.getCustomer();
    }
}
