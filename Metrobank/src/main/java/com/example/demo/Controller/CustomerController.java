package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Customer;
import com.example.demo.Repository.CustomerRepository;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository custRepo;

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customerList = new ArrayList<>();
            custRepo.findAll().forEach(customerList::add);

            if (customerList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(customerList, HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/v1/account/{customerNumber}")
    public ResponseEntity<Map<String, Object>> getCustomerByNumber(@PathVariable long customerNumber) {
        Optional<Customer> customerDetails = custRepo.findById(customerNumber);
        Map<String, Object> response = new LinkedHashMap<>(); 
        try {
            if (customerDetails.isPresent()) {
                Customer customer = customerDetails.get();

                response.put("customerNumber", customer.getCustomerNumber());
                response.put("customerName", customer.getCustomerName());
                response.put("customerMobile", customer.getCustomerMobile());
                response.put("customerEmail", customer.getCustomerEmail());
                response.put("address1", customer.getAddress1());
                response.put("address2", customer.getAddress2());

                if (customer.getAccountType() == 'S') {
                    Map<String, Object> savingsMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order
                    savingsMap.put("accountNumber", "10001");
                    savingsMap.put("accountType", "Savings");
                    savingsMap.put("availableBalance", "500");
                    response.put("savings", savingsMap);
                } else if (customer.getAccountType() == 'C') {
                    Map<String, Object> savingsMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order
                    savingsMap.put("accountNumber", "10002");
                    savingsMap.put("accountType", "Checking");
                    savingsMap.put("availableBalance", "600");
                    response.put("savings", savingsMap);
                } else {
                    Map<String, Object> savingsMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order
                    savingsMap.put("accountNumber", "N/A");
                    savingsMap.put("accountType", "N/A");
                    savingsMap.put("availableBalance", "N/A");
                    response.put("savings", savingsMap);
                }
                response.put("transactionStatusCode", HttpStatus.FOUND.value());
                response.put("transactionStatusDescription", "Customer Account Found");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("transactionStatusCode", HttpStatus.NOT_FOUND.value());
                response.put("transactionStatusDescription", "Customer not Found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/api/v1/account")
    public ResponseEntity<Map<String, Object>> addCustomer(@RequestBody Customer customer) {
    	Map<String, Object> map = new LinkedHashMap<>(); 
        try {

            if (customer.getCustomerName() == "") {
                map.put("transactionStatusCode", HttpStatus.BAD_REQUEST.value());
                map.put("transactionStatusDescription", "Name is required field");

                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            } else if (customer.getCustomerMobile() == "") {
                map.put("transactionStatusCode", HttpStatus.BAD_REQUEST.value());
                map.put("transactionStatusDescription", "Mobile is required field");

                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            } else if (customer.getAddress1() == "") {
                map.put("transactionStatusCode", HttpStatus.BAD_REQUEST.value());
                map.put("transactionStatusDescription", "Address1 is required field");

                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }else if (customer.getAccountType() == ' ' || customer.getAccountType() == ' ') {
                map.put("transactionStatusCode", HttpStatus.BAD_REQUEST.value());
                map.put("transactionStatusDescription", "Account type is required field");

                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }else {
                Customer customerObj = custRepo.save(customer);
                map.put("customerNumber", customerObj.getCustomerNumber());
                map.put("transactionStatusCode", HttpStatus.CREATED.value());
                map.put("transactionStatusDescription", "Customer account created");

                return new ResponseEntity<>(map, HttpStatus.CREATED);
            }
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
