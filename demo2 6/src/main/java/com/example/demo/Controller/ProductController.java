package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Services.ProductAlreadyExistsException;
import com.example.demo.Services.ProductNotFoundException;
import com.example.demo.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("/pindex")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/Product")
    public String showProductList(Model model) {
        List<Product> productList = productService.listAll();
        model.addAttribute("productList", productList);
        return "Product/ProductList";
    }

    @PostMapping("/Product/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes ra) throws ProductAlreadyExistsException {
       try {
           productService.save(product);
           ra.addFlashAttribute("message", "The product has been saved successfully :)");

       }catch (ProductAlreadyExistsException e)
       {
          ra.addFlashAttribute("message",e.getMessage());
       }
        return "redirect:/Product";
    }

    @GetMapping("/Product/create")
    public String showNewForm(Model model) {
        model.addAttribute("product", new Product());
        return "Product/ProductAdd";
    }

    @GetMapping("/Product/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", "Product not found :(");
            return "redirect:/Product";
        }
        return "Product/ProductEdit";
    }

    @PostMapping("/Product/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute Product product, RedirectAttributes ra) {
        try {
            productService.updateProduct(id, product);
            ra.addFlashAttribute("message", "The product has been updated successfully :)");
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", "Product not found :(");
        }
        return "redirect:/Product";
    }


    @GetMapping("/Product/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productService.delete(id);
            ra.addFlashAttribute("message", "The product with ID " + id + " has been deleted");
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/Product";
    }
}
