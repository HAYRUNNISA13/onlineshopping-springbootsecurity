package com.example.demo.Services;

import com.example.demo.Controller.OrderAlreadyExistsException;
import com.example.demo.DTOs.Orderview;
import  com.example.demo.Model.Product;
import  com.example.demo.Model.Order;
import com.example.demo.Model.Customer;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> getAllOrders();
    void saveOrder(Order order)throws OrderAlreadyExistsException, CustomerNotFoundException, ProductNotFoundException;

    Order createOrder(Orderview orderview)throws OrderAlreadyExistsException, CustomerNotFoundException, ProductNotFoundException;
    Order getOrderById(Long id) throws OrderNotFoundException;
   // Customer findCustomerByName(String customerName) throws CustomerNotFoundException;
   // Product findProductByName(String productName)throws ProductNotFoundException;




    void updateOrder(Order order) throws OrderNotFoundException, CustomerNotFoundException,ProductNotFoundException;

    void deleteOrder(Long id) throws OrderNotFoundException;
}

