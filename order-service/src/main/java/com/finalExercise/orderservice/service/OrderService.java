package com.finalExercise.orderservice.service;

import com.finalExercise.orderservice.dto.*;
import com.finalExercise.orderservice.model.Order;
import com.finalExercise.orderservice.model.OrderLineItem;
import com.finalExercise.orderservice.proxy.InventoryProxy;
import com.finalExercise.orderservice.proxy.ProductProxy;
import com.finalExercise.orderservice.repository.OrderLineItemRepository;
import com.finalExercise.orderservice.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderLineItemRepository itemRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLineItemMapper orderLineItemMapper;

    @Autowired
    private InventoryProxy inventoryProxy;

    @Autowired
    private ProductProxy productProxy;

    private Logger logger = LoggerFactory.getLogger(OrderService.class);


    public OrderService(OrderRepository repository, OrderLineItemRepository itemRepository,
                        OrderMapper orderMapper, OrderLineItemMapper orderLineItemMapper,
                        InventoryProxy inventoryProxy, ProductProxy productProxy){
        this.repository=repository;
        this.itemRepository=itemRepository;
        this.orderMapper = orderMapper;
        this.orderLineItemMapper = orderLineItemMapper;
        this.inventoryProxy = inventoryProxy;
        this.productProxy = productProxy;
    }
    public String getRandomId(){
        String ordN;
        do {
            ordN = "ord" + new Random().nextInt(999999);  // o cualquier otro mecanismo para generar números
        } while (repository.findByOrderNumber(ordN).isPresent());  // verifica que el ID no exista en la base de datos
        return ordN;
    }

    public OrderResponse retrieveOrderById(Long id) throws HttpException {
        if(repository.findById(id).isEmpty()){
            throw new HttpException("No order found with this ID: " + id);
        }
        Order o = repository.findById(id).get();
        return orderMapper.toDTO(o);
    }
    private static BigDecimal getPriceF(Order order, OrderLineItem item, int quantity) {
        return order.getPrice().add(item.getPrice().multiply(new BigDecimal(quantity)));
    }
    public List<OrderResponse> retrieveAllOrders() throws HttpException {
        if(repository.findAll().isEmpty()){
            throw new HttpException("No orders found");
        }
        List<Order> orders = repository.findAll();
        return orderMapper.toDTOList(orders);
    }
    public OrderResponse retrieveOrder(String orderNumber) throws HttpException {
        if(repository.findByOrderNumber(orderNumber).isEmpty()){
            throw new HttpException("No orders found with this ORD: " + orderNumber);
        }
        Order order = repository.findByOrderNumber(orderNumber).get();
        return orderMapper.toDTO(order);
    }
    public Optional<Order> retrieveOrderEntity(String orderNumber) throws HttpException {
        if(repository.findByOrderNumber(orderNumber).isEmpty()){
            throw new HttpException("No orders found with this ORD: " + orderNumber);
        }
        return repository.findByOrderNumber(orderNumber);
    }



    @Transactional
    public void deleteByOrderNumber(String orderNumber) {
            repository.deleteByOrderNumber(orderNumber);
    }

    public List<Order> retrieveAllOrderFromSku(String skuCode) throws HttpException {
        if(repository.findByOrderLineItemsList_SkuCode(skuCode).isEmpty()){
            throw new HttpException("No items found with this SKU: " + skuCode);
        }
        return repository.findByOrderLineItemsList_SkuCode(skuCode);
    }

    @Transactional
    public void deleteBySkuCode(String orderNumber, String skuCode) throws HttpException {

            OrderResponse orderResponse = retrieveOrder(orderNumber);
            Order order = orderMapper.toEntity(orderResponse);
            if(repository.findOrderLineItemByOrderNumberAndSkuCode(orderNumber, skuCode).isEmpty()){
                throw new HttpException("No items found with this SKU to DELETE: " + skuCode);
            }
            OrderLineItem item = repository.findOrderLineItemByOrderNumberAndSkuCode(orderNumber, skuCode).get();
            BigDecimal priceF = order.getPrice().subtract(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            order.setPrice(priceF);
            item.setOrder(null);
            order.getOrderLineItemsList().remove(item);
            repository.save(order);
            itemRepository.delete(item);

    }


    @Transactional
    public void addOrUpdateItemToOrder(String skuCode, int quantity, Optional<Order> OptionalOrder) throws HttpException {
            Order order = OptionalOrder.get();
            OrderLineItemRequest itemR = inventoryProxy.retrieveInventoryItem(skuCode);
            OrderLineItem item = orderLineItemMapper.toEntity(itemR);
            if (quantity <= item.getQuantity()) {
                item.setQuantity(quantity);
                BigDecimal precioP = productProxy.viewProductByPrice(skuCode);
                item.setPrice(precioP);
                inventoryProxy.modifiedQuantity(skuCode, (-quantity));
                if (!order.getOrderLineItemsList().stream().anyMatch(orderLineItem -> orderLineItem.getSkuCode().equals(skuCode))) {
                    BigDecimal priceF = getPriceF(order, item, quantity);
                    order.setPrice(priceF);
                    item.setOrder(order);
                    order.getOrderLineItemsList().add(item);
                } else {
                    OrderLineItem itemExistente = order.getOrderLineItemsList().stream()
                            .filter(orderLineItem -> orderLineItem.getSkuCode().equals(skuCode)).findFirst().get();
                    itemExistente.setQuantity(item.getQuantity() + itemExistente.getQuantity());
                    BigDecimal priceF = getPriceF(order, item, quantity);
                    order.setPrice(priceF);
                    itemRepository.save(itemExistente);
                }
                repository.save(order);
            } else {
                throw new HttpException("Insufficient item quantity. No STOCK");
            }
    }


    public OrderLineItemResponse retrieveOrderLineItem(String orderNumber, String skuCode) throws HttpException {
        if(repository.findOrderLineItemByOrderNumberAndSkuCode(orderNumber,skuCode).isEmpty()){
            throw new HttpException("No items found with this SKU to DELETE: " + skuCode);
        }
        OrderLineItem oli = repository.findOrderLineItemByOrderNumberAndSkuCode(orderNumber,skuCode).get()  ;
        return orderLineItemMapper.toDTO(oli);
    }

    @Transactional
    public void deleteById(Long id) throws HttpException {
        if(repository.findById(id).isEmpty()){
            throw new HttpException("No order found with this ID to DELETE: " + id);
        }else{
            repository.deleteById(id);
        }

    }

    @Transactional
    public void addNewOrderFromEntity(Order order) throws HttpException {
        try {
            repository.save(order);
        }catch (Exception e){
            throw new HttpException("Error saving order entity");
        }
    }

    public OrderResponse toDTO(Order order) {
        return orderMapper.toDTO(order);
    }

        /*
    @Transactional
    public OrderResponse addNewOrder(OrderRequest orderRequest) throws HttpException {
        try{
            Order order = orderMapper.toEntity(orderRequest);
            BigDecimal price = new BigDecimal(0);
            for(OrderLineItem item : order.getOrderLineItemsList()) {
                item.setOrder(order);
                price = price.add(item.getPrice());
            }
            order.setPrice(price);
            repository.save(order);
            return orderMapper.toDTO(order);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to add new order due to data integrity violation: {}", e.getMessage());
            throw new HttpException("Data integrity violation while adding new order.");
        } catch (EntityNotFoundException e) {
            logger.error("Failed to add new order because the associated entity was not found: {}", e.getMessage());
            throw new HttpException("Associated entity not found while adding new order.");
        }
    }
*/

}
