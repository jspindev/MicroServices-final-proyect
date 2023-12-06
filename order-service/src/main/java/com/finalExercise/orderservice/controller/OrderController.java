    package com.finalExercise.orderservice.controller;


    import com.finalExercise.orderservice.dto.OrderLineItemRequest;
    import com.finalExercise.orderservice.dto.OrderLineItemResponse;
    import com.finalExercise.orderservice.dto.OrderRequest;
    import com.finalExercise.orderservice.dto.OrderResponse;
    import com.finalExercise.orderservice.model.Order;
    import com.finalExercise.orderservice.proxy.InventoryProxy;
    import com.finalExercise.orderservice.service.OrderService;
    import org.apache.http.HttpException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

    import java.math.BigDecimal;
    import java.net.URI;
    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    @RestController
    public class OrderController {

        @Autowired
        private OrderService service;

        @Autowired
        private InventoryProxy proxy;

        @GetMapping("/orders")
        public List<OrderResponse> retrieveAllOrders() throws HttpException {
            return service.retrieveAllOrders();
        }

        @GetMapping("/orders/searchid/{id}")
        public OrderResponse retrieveAllItemsId(@PathVariable Long id) throws HttpException {
            return service.retrieveOrderById(id);

        }

        @GetMapping("/orders/searchorder/{orderNumber}")
        public OrderResponse retrieveAllItemsOrder(@PathVariable String orderNumber) throws HttpException {
            return service.retrieveOrder(orderNumber);
        }
        @GetMapping("/orders/searchsku/{skuCode}")
        public List<Order> retrieveAllOrderFromSku(@PathVariable String skuCode) throws HttpException {
            return service.retrieveAllOrderFromSku(skuCode);
        }

        @PostMapping("/orders")
        public ResponseEntity<OrderResponse> addNewOrder(@RequestBody List<OrderLineItemRequest> listItems)
                throws HttpException{
                Order order = new Order();
                order.setOrderNumber(service.getRandomId());
                order.setPrice(new BigDecimal(0));
                service.addNewOrderFromEntity(order);
                for(OrderLineItemRequest ir : listItems){
                    service.addOrUpdateItemToOrder(ir.getSkuCode(),ir.getQuantity(),Optional.of(order));
                }
                OrderResponse or = service.retrieveOrder(order.getOrderNumber());
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{orderNumber}").buildAndExpand(order.getOrderNumber()).toUri();
                return ResponseEntity.created(location).body(or);
        }
        @PostMapping("/orders/empty")
        public ResponseEntity<OrderResponse> addNewOrderSolo() throws HttpException {
                String orderNumber = service.getRandomId();
                Order order = new Order();
                order.setOrderNumber(service.getRandomId());
                order.setPrice(new BigDecimal(0));
                order.setOrderLineItemsList(Collections.emptyList());
                service.addNewOrderFromEntity(order);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{orderNumber}").buildAndExpand(orderNumber).toUri();
                return ResponseEntity.created(location).body(service.toDTO(order));
        }


        @PostMapping("/orders/{orderNumber}/items") //?skucode=skuxxxx&quantity=4
        public  ResponseEntity<Object> addNewItemToOrder(@RequestParam("skucode") String skuCode
                ,@RequestParam("quantity") int quantity
                ,@PathVariable String orderNumber) throws HttpException {
                Optional<Order> order = service.retrieveOrderEntity(orderNumber);
                service.addOrUpdateItemToOrder(skuCode,quantity,order);
                OrderLineItemResponse orlit = service.retrieveOrderLineItem(orderNumber,skuCode);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{skuCode}").buildAndExpand(orlit.getSkuCode()).toUri();
                return ResponseEntity.created(location).body(orlit);
        }

        @DeleteMapping("/orders/deleteorder/{orderNumber}")
        public ResponseEntity<OrderResponse> deleteByOrderNumber(@PathVariable String orderNumber) throws HttpException {
                OrderResponse or = service.retrieveOrder(orderNumber);
                service.deleteByOrderNumber(orderNumber);
                return ResponseEntity.ok(or);
        }
        @DeleteMapping("/orders/deletesku/{orderNumber}/items/{skuCode}")
        public ResponseEntity<OrderLineItemResponse> deleteBySkuCode(@PathVariable String orderNumber
                ,@PathVariable String skuCode) throws HttpException {
                OrderLineItemResponse oli = service.retrieveOrderLineItem(orderNumber,skuCode);
                service.deleteBySkuCode(orderNumber,skuCode);
                return ResponseEntity.ok(oli);
        }

        @DeleteMapping("/orders/deleteid/{id}")
        public ResponseEntity<OrderResponse> deleteById(@PathVariable Long id) throws HttpException {
            OrderResponse or = service.retrieveOrderById(id);
            service.deleteById(id);
            return ResponseEntity.ok(or);
        }


    }
