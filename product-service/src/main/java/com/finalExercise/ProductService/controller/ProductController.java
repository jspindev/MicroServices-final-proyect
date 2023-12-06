package com.finalExercise.ProductService.controller;

import com.finalExercise.ProductService.dto.ProductRequest;
import com.finalExercise.ProductService.dto.ProductResponse;
import com.finalExercise.ProductService.model.Product;
import com.finalExercise.ProductService.service.ProductService;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<ProductResponse> viewAllProducts() throws HttpException{return productService.viewAllProducts();}

    @GetMapping("/products/searchname/{name}")
    public List <ProductResponse> viewProductByName(@PathVariable String name) throws HttpException{
        String nameInDB = name.toLowerCase();
        return productService.viewProductsByName(nameInDB);
    }

    @GetMapping("/products/{id}/price")
    public BigDecimal viewProductByPrice(@PathVariable String id) throws HttpException{
        return productService.getPrice(id);
    }

    @GetMapping("/products/searchid/{skuCode}")
    public ProductResponse viewProductById(@PathVariable String skuCode) throws HttpException{
        return productService.viewProductById(skuCode);
    }


    @PostMapping("/products")
    public ResponseEntity<Object> addNewProduct(@RequestBody ProductRequest productRequest,
                                                @RequestParam(value="quantity",defaultValue = "10") int quantity) throws HttpException{
        String id = productService.getRandomId();
        if(quantity<0){
            return ResponseEntity.badRequest().body("Wrong quantity, must be >=0");
        }
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setId(id);
        productService.createProductFromEntity(product,quantity);
        //productService.createProduct(productRequest,id,quantity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(productService.toDTO(product));
    }

    @DeleteMapping("/products/deleteid/{id}")
    public ResponseEntity<ProductResponse> deleteById(@PathVariable String id) throws HttpException {
        ProductResponse or = productService.viewProductById(id);
        productService.deleteProductById(id);
        return ResponseEntity.ok(or);
    }
}
