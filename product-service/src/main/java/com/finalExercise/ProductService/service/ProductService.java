package com.finalExercise.ProductService.service;

import com.finalExercise.ProductService.dto.ProductMapper;
import com.finalExercise.ProductService.dto.ProductRequest;
import com.finalExercise.ProductService.dto.ProductResponse;
import com.finalExercise.ProductService.model.Product;
import com.finalExercise.ProductService.proxy.InventoryProxy;
import com.finalExercise.ProductService.repository.ProductRepository;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private InventoryProxy inventoryproxy;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,InventoryProxy inventoryproxy) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse mapProductToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        return productResponse;
    }

    public List<ProductResponse> viewAllProducts() throws  HttpException{
        if(productRepository.findAll().isEmpty()){
            throw new HttpException("No products found");
        }
        List<Product> products = productRepository.findAll();
        List<ProductResponse> pr = products.stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());
        return pr;
    }

    public List<ProductResponse> viewProductsByName(String name) throws  HttpException{
        List <Product> product= productRepository.findByName(name);
        if(productRepository.findByName(name).isEmpty()){
            throw new HttpException("No product found with this name : " + name);
        }
        return productMapper.toDTOList(product);
    }

    public ProductResponse viewProductById(String skuCode) throws HttpException{
        if(productRepository.findById(skuCode).isEmpty()){
            throw new HttpException("No product found with this skuCode : " + skuCode);
        }
        Product product= productRepository.findById(skuCode).get();
        return productMapper.toDTO(product);
    }

    public ProductResponse toDTO(Product product) {
        return productMapper.toDTO(product);
    }
    @Transactional
    public void createProductFromEntity(Product product,int quantity) throws HttpException {
        try{
            product.setName(product.getName().toLowerCase());
            productRepository.save(product);
            inventoryproxy.newInventoryFromProduct(product.getId(),quantity);
        }catch (Exception e){
            throw new HttpException("Error saving product entity");
        }
    }

   /*
    @Transactional
    public void createProduct(ProductRequest dto, String id, int quantity) throws HttpException{
        try {
            Product product = productMapper.toEntity(dto);
            product.setId(id);
            product.setPrice(dto.getPrice());
            inventoryproxy.newInventoryFromProduct(product.getId(), quantity);
            productRepository.save(product);
        }catch (Exception e){
            throw new HttpException("Error saving product entity");
        }
    }
*/
    @Transactional
    public void deleteProductById(String id) throws HttpException{
        if(productRepository.findById(id).isEmpty()){
            throw new HttpException("No product found with this ID to DELETE: " + id);
        }else{
            productRepository.deleteById(id);
        }

    }

    public BigDecimal getPrice(String id) {
        return productRepository.findById(id).get().getPrice();
    }

    public String getRandomId(){
        String id;
        do {
            id = "sku" + new Random().nextInt(999999);  // o cualquier otro mecanismo para generar números
        } while (productRepository.existsById(id));  // verifica que el ID no exista en la base de datos
        return id;
    }


}
