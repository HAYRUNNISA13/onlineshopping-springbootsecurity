package com.example.demo.Controller;

import com.example.demo.Model.Order;
import com.example.demo.Model.Product;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.DTOs.Orderview;
import com.example.demo.Model.Customer;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Date;

@Controller
public class OrderController {

    private OrderService orderService;
    private final ProductService productService;
    private final CustomerService customerService;
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;


    @Autowired
    public OrderController(OrderService orderService,ProductRepository productRepository, ProductService productService, CustomerService customerService, OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /*public OrderController(OrderService orderService, ProductService productService, CustomerService customerService,ProductRepository productRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
        this.productRepository = productRepository;*/

    @GetMapping("/oindex")
    public String getIndexPage() {
        return "index";
    }


    @GetMapping("/Product/porderlist/{id}")
    public String showPorderListPage(Model model, @PathVariable("id") Long id, RedirectAttributes ra) {

        Order o = new Order();
        Product product= productService.findById(id);
        List<Customer> customers= (List<Customer>) customerRepository.findAll();

        model.addAttribute("order", o);
        model.addAttribute("product", product);
        model.addAttribute("customer",customers);
        //model.addAttribute("product_name", o.getProduct().getName());
        //model.addAttribute("customerName", customerService.findCustomerNameById(id));
        // model.addAttribute("product_name", productService.findProductNameById(id));

        return "Product/porderlist";
    }


    @PostMapping("/Product/createp/{id}")
    public String createOrder(Order order, RedirectAttributes ra)throws ProductNotFoundException {

        order.setId(order.getId());
        order.setDate(order.getDate());
        order.setDeliveryStatus(order.getDeliveryStatus());
        order.setCity(order.getCity());
        order.setCustomer(order.getCustomer());
        order.setProduct(order.getProduct());
        try {
            orderService.saveOrder(order);
            ra.addFlashAttribute("message", "The order was successfully added");
        } catch (OrderAlreadyExistsException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CustomerNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/orders";
    }

    @GetMapping("/order/add")
    public String showAddOrderForm(Model model) {
        model.addAttribute("order", new Orderview());
        return "order/orderAdd";
    }
    @PostMapping("/order/add")
    public String addOrder(Orderview orderv, RedirectAttributes ra) {
        try {
            orderService.createOrder(orderv);
            ra.addFlashAttribute("message", "The order was successfully added");
        } catch (OrderAlreadyExistsException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }








    @GetMapping("/orders")
    public String showOrderList(Model model) {
        Iterable<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "order/orderList";
    }


    @GetMapping("/order/update/{id}")
    public String getOrderById(@PathVariable Long id, Model model) throws OrderNotFoundException {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order/orderEdit";
    }

    @PostMapping("/order/update/{id}")
    public String updateOrder(Order orderv, RedirectAttributes ra) throws OrderNotFoundException {
        try {
            orderService.updateOrder(orderv);
            ra.addFlashAttribute("message", "The order was successfully updated");
        }catch (CustomerNotFoundException |ProductNotFoundException e)
        {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }


    @GetMapping("/customers/corderlist/{id}")
    public String showCorderListPage(Model model, @PathVariable("id") Long id, RedirectAttributes ra) {
        Order o = new Order();
        Customer customer = customerService.findById(id);
        List<Product> products= (List<Product>) productRepository.findAll();

        model.addAttribute("order", o);
        model.addAttribute("customer", customer);
        model.addAttribute("products",products);
        //model.addAttribute("product_name", o.getProduct().getName());
        //model.addAttribute("customerName", customerService.findCustomerNameById(id));
       // model.addAttribute("product_name", productService.findProductNameById(id));
        return "customer/corderlist";
    }




    @PostMapping("/customers/createc/{id}")
    public String createOrderc(@PathVariable("id") Long customerId, @ModelAttribute Order order, RedirectAttributes ra) {
        try {
            Customer customer = customerService.findById(customerId);
            order.setCustomer(customer);
            orderService.saveOrder(order);
            ra.addFlashAttribute("message", "The order was successfully added");
        } catch (OrderAlreadyExistsException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }










    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id , RedirectAttributes ra) {
        try {
            orderService.deleteOrder(id);
            ra.addFlashAttribute("message", "Order deleted successfully");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("error", "Failed to delete order: " + e.getMessage());

        }
        return "redirect:/orders";
    }
}

