package com.example.demo.Services;

import com.example.demo.Controller.OrderAlreadyExistsException;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Order;
import com.example.demo.Model.Product;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.DTOs.Orderview;
import java.util.List;
import java.util.ArrayList;

import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository crepo;
    @Autowired
    private ProductRepository productRepository;
    @Override
    public List<Order> getAllOrders() {
        Iterable<Order> orderIterable = orderRepository.findAll();
        List<Order> orderList = new ArrayList<>();
        orderIterable.forEach(orderList::add);
        return orderList;
    }


    @Override
    public void saveOrder(Order order) throws OrderAlreadyExistsException {
        Long count = orderRepository.countById(order.getId());



        if (count != null && count > 0) {
            throw new OrderAlreadyExistsException("An order with ID " + order.getId() + " already exists.");
        }
        orderRepository.save(order);
    }



    @Override
    public Order createOrder(Orderview orderview) throws OrderAlreadyExistsException, CustomerNotFoundException, ProductNotFoundException {
        Long count = orderRepository.countById(orderview.getId());
        if (count != null && count > 0) {
            throw new OrderAlreadyExistsException("An order with ID " + orderview.getId() + " already exists.");
        }

        Customer customer = crepo.findByName(orderview.getCustomerName());
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with name: " + orderview.getCustomerName());
        }

        Product product = productRepository.findByName(orderview.getProductName());
        if (product == null) {
            throw new ProductNotFoundException("Product not found with name: " + orderview.getProductName());
        }

        Order order = new Order();
        order.setId(orderview.getId());
        order.setCity(orderview.getCity());
        order.setDate(orderview.getDate());
        order.setDeliveryStatus(orderview.getDeliveryStatus());
        order.setProduct(product);
        order.setCustomer(customer);

        if (customer != null && product != null) {
            return orderRepository.save(order);
        } else {

            return null;
        }
    }

    public Order getOrderById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            Orderview orderview = new Orderview();
            orderview.setId(order.getId());
            orderview.setCity(order.getCity());
            orderview.setDate(order.getDate());
            orderview.setDeliveryStatus(order.getDeliveryStatus());
            orderview.setProductId(order.getProduct().getId());
            orderview.setCustomerId(order.getCustomer().getId());
            return orderRepository.save(order);
        } else {

            return null;
        }
    }

    public void updateOrder(Order order) throws OrderNotFoundException, CustomerNotFoundException,ProductNotFoundException{

        Optional<Order> optionalOrder = orderRepository.findById(order.getId());


        if (optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();
            existingOrder.setDeliveryStatus(order.getDeliveryStatus());
            existingOrder.setCity(order.getCity());
            existingOrder.setDate(order.getDate());


            Product product = null;
            product = productRepository.findByName(order.getProduct().getName());

            if (product == null) {
                throw new ProductNotFoundException("Product not found with name: " + order.getProduct().getName());
            }


            Customer customer = null;
            customer = crepo.findByName(order.getCustomer().getName());
            if (customer == null) {
                throw new CustomerNotFoundException("Customer not found with name: " + order.getCustomer().getName());
            }


            existingOrder.setProduct(product);
            existingOrder.setCustomer(customer);


            orderRepository.save(existingOrder);
        } else {

            throw new OrderNotFoundException("Order not found with id: " + order.getId());
        }
    }








    @Override
    public void deleteOrder(Long id) throws OrderNotFoundException {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
    }
}
