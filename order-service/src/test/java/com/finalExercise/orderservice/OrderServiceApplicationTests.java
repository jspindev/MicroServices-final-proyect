package com.finalExercise.orderservice;

import com.finalExercise.orderservice.controller.OrderController;
import com.finalExercise.orderservice.dto.OrderLineItemResponse;
import com.finalExercise.orderservice.dto.OrderRequest;
import com.finalExercise.orderservice.dto.OrderResponse;
import com.finalExercise.orderservice.model.Order;
import com.finalExercise.orderservice.model.OrderLineItem;
import com.finalExercise.orderservice.repository.OrderLineItemRepository;
import com.finalExercise.orderservice.repository.OrderRepository;
import feign.FeignException;
import org.apache.http.HttpException;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.finalExercise.orderservice.service.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class OrderServiceApplicationTests {


	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@InjectMocks
	private OrderController orderControllerMock;

	@Mock
	private OrderService orderServiceMock;

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderController orderController;
	@Autowired
	private OrderRepositoryTest repository;
	@Autowired
	private OrderLineItemRepositoryTest itemRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeEach
	public void cleanupBefore() {
		int rowsAffected = jdbcTemplate.update("DELETE FROM orders");
		jdbcTemplate.execute("ALTER TABLE orders AUTO_INCREMENT = 1");
		System.out.println("Deleted " + rowsAffected + " rows from orders table.");
	}
	@Test
	public void testAddOrUpdateItemToOrder() {
		String skuCode = "SKU123";
		int quantity = 2;

		try {
			OrderLineItem lineItem = new OrderLineItem();
			lineItem.setSkuCode(skuCode);
			lineItem.setPrice(BigDecimal.valueOf(50));
			lineItem.setQuantity(quantity);

			Order order = new Order();
			order.setOrderNumber("Order1");
			order.setPrice(BigDecimal.valueOf(100));
			if (lineItem == null || order == null) {
				throw new DataIntegrityViolationException("lineItem o order es null");
			}

			lineItem.setOrder(order);
			order.setOrderLineItemsList(Arrays.asList(lineItem));

			order = repository.save(order);

			orderService.addOrUpdateItemToOrder(skuCode, quantity, Optional.of(order));

			Order updatedOrder = repository.findById(order.getId()).orElse(null);
			assertNotNull(updatedOrder);

			int updatedQuantity = updatedOrder.getOrderLineItemsList().get(0).getQuantity();
			assertEquals(quantity, updatedQuantity);

		} catch (FeignException.ServiceUnavailable e) {
			fail("El servicio inventory-service no está disponible");

		} catch (DataIntegrityViolationException e) {
			fail("Violación de la integridad de los datos: " + e.getMessage());

		} catch (Exception e) {
			fail("Ocurrió una excepción desconocida: " + e.getMessage());
		}
	}


	@Test
	public void testDeleteByOrderNumber() throws HttpException {
		String orderNumber = "Order1";

		Order order = new Order();
		order.setOrderNumber(orderNumber);
		order.setPrice(BigDecimal.valueOf(100));

		order = repository.save(order);

		orderService.deleteByOrderNumber(orderNumber);

		Optional<Order> deletedOrder = repository.findByOrderNumber(orderNumber);
		assertFalse(deletedOrder.isPresent());
	}


	@Test
	public void testRetrieveOrderEntity() throws HttpException {
		String orderNumber = "Order1";

		Order order = new Order();
		order.setOrderNumber(orderNumber);
		order.setPrice(BigDecimal.valueOf(100));

		order = repository.save(order);

		Optional<Order> retrievedOrder = orderService.retrieveOrderEntity(orderNumber);

		assertTrue(retrievedOrder.isPresent());
		assertEquals(order.getId(), retrievedOrder.get().getId());
	}



	@Test
	void test_retrieveAllItemsId() throws HttpException {
		Long id = 1L;
		// given
		OrderResponse expected = new OrderResponse();
		when(orderServiceMock.retrieveOrderById(any())).thenReturn(expected);  // Corrected the method name

		// when
		OrderResponse actual = orderServiceMock.retrieveOrderById(id);  // Corrected the object name

		// then
		assertEquals(expected, actual);
	}

	@Test
	public void test_RetrieveAllOrders() throws HttpException {
		// given
		OrderLineItemResponse lineItemResponse1 = new OrderLineItemResponse(1L, "skuCode1", BigDecimal.valueOf(100), 10);
		OrderLineItemResponse lineItemResponse2 = new OrderLineItemResponse(2L, "skuCode2", BigDecimal.valueOf(50), 5);
		OrderResponse response1 = new OrderResponse(1L, "ord1", Arrays.asList(lineItemResponse1), BigDecimal.valueOf(1000));
		OrderResponse response2 = new OrderResponse(2L, "ord2", Arrays.asList(lineItemResponse2), BigDecimal.valueOf(500));
		List<OrderResponse> expected = Arrays.asList(response1, response2);
		when(orderServiceMock.retrieveAllOrders()).thenReturn(expected);

		// when
		List<OrderResponse> actual = orderControllerMock.retrieveAllOrders();

		// then
		assertEquals(expected, actual);
	}


	@Test
	public void test_RetrieveAllItemsOrder() throws HttpException {
		// given
		OrderLineItemResponse lineItemResponse1 = new OrderLineItemResponse(1L, "skuCode1", BigDecimal.valueOf(100), 10);
		OrderResponse expected = new OrderResponse(1L, "ord1", Arrays.asList(lineItemResponse1), BigDecimal.valueOf(1000));
		when(orderServiceMock.retrieveOrder("ord1")).thenReturn(expected);

		// when
		OrderResponse actual = orderControllerMock.retrieveAllItemsOrder("ord1");

		// then
		assertEquals(expected, actual);
	}

	@Test
	public void test_AddNewOrder() throws HttpException {
		// given
		when(orderServiceMock.getRandomId()).thenReturn("ord1");

		OrderRequest request = new OrderRequest("ord1", Collections.emptyList(), BigDecimal.valueOf(0));
		MockHttpServletRequest servletRequest = new MockHttpServletRequest();
		servletRequest.setRequestURI("/orders");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

		// when
		ResponseEntity<OrderResponse> responseEntity = orderControllerMock.addNewOrder(Collections.emptyList());

		// then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertEquals("http://localhost/orders/ord1", responseEntity.getHeaders().getLocation().toString());
	}

	@Test
	public void test_RetrieveOrderByIdNotFound() throws HttpException {
		// given
		String orderNumber = "ord1";
		when(orderServiceMock.retrieveOrder(orderNumber)).thenReturn(null);

		// when
		OrderResponse actual = orderControllerMock.retrieveAllItemsOrder(orderNumber);

		// then
		assertNull(actual);
	}

	@Test
	void test_retrieveAllOrderFromSku() throws HttpException {
		// given
		List<Order> expectedOrders = Arrays.asList(new Order());
		when(orderServiceMock.retrieveAllOrderFromSku(anyString())).thenReturn(expectedOrders);

		// when
		List<Order> actualOrders = orderControllerMock.retrieveAllOrderFromSku("someSku");

		// then
		assertNotNull(actualOrders);
		assertEquals(expectedOrders, actualOrders);
	}

	@Test
	void test_addNewOrderSolo() throws HttpException {
		// given
		String orderNumber = "someOrderNumber";
		Order order = new Order();
		order.setOrderNumber(orderNumber);
		OrderResponse orderResponse = new OrderResponse();
		when(orderServiceMock.getRandomId()).thenReturn(orderNumber);
		when(orderServiceMock.toDTO(any(Order.class))).thenReturn(orderResponse);

		// when
		ResponseEntity<OrderResponse> actualResponse = orderControllerMock.addNewOrderSolo();

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		assertEquals(orderResponse, actualResponse.getBody());
	}

	@Test
	void test_deleteByOrderNumber() throws HttpException {
		// given
		String orderNumber = "someOrderNumber";
		OrderResponse orderResponse = new OrderResponse();
		when(orderServiceMock.retrieveOrder(anyString())).thenReturn(orderResponse);

		// when
		ResponseEntity<OrderResponse> actualResponse = orderControllerMock.deleteByOrderNumber(orderNumber);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(orderResponse, actualResponse.getBody());
	}
	@Test
	void test_deleteById() throws HttpException {
		// given
		Long id = 1L;
		OrderResponse orderResponse = new OrderResponse();
		when(orderServiceMock.retrieveOrderById(anyLong())).thenReturn(orderResponse);

		// when
		ResponseEntity<OrderResponse> actualResponse = orderControllerMock.deleteById(id);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(orderResponse, actualResponse.getBody());
	}

	@Test
	void test_retrieveAllOrders_EmptyList() throws HttpException {
		// given
		when(orderServiceMock.retrieveAllOrders()).thenReturn(Collections.emptyList());

		// when
		List<OrderResponse> actual = orderControllerMock.retrieveAllOrders();

		// then
		assertTrue(actual.isEmpty());
	}

	@Test
	void test_retrieveAllItemsId_ThrowsException() throws HttpException {
		// given
		when(orderServiceMock.retrieveOrderById(anyLong())).thenThrow(new HttpException("Some error"));

		// when and then
		assertThrows(HttpException.class, () -> orderControllerMock.retrieveAllItemsId(1L));
	}

	@Test
	void test_AddNewItemToOrder() throws HttpException {
		// given
		String skuCode = "sku1";
		int quantity = 2;
		String orderNumber = "ord1";
		OrderLineItemResponse expectedResponse = new OrderLineItemResponse(1L, skuCode, BigDecimal.valueOf(100), quantity);
		when(orderServiceMock.retrieveOrderLineItem(eq(orderNumber), eq(skuCode))).thenReturn(expectedResponse);

		// when
		ResponseEntity<Object> actualResponse = orderControllerMock.addNewItemToOrder(skuCode, quantity, orderNumber);

		// then
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		assertEquals(expectedResponse, actualResponse.getBody());
	}

	@Test
	void test_DeleteBySkuCode() throws HttpException {
		// given
		String orderNumber = "ord1";
		String skuCode = "sku1";
		OrderLineItemResponse expectedResponse = new OrderLineItemResponse(1L, skuCode, BigDecimal.valueOf(100), 2);
		when(orderServiceMock.retrieveOrderLineItem(eq(orderNumber), eq(skuCode))).thenReturn(expectedResponse);

		// when
		ResponseEntity<OrderLineItemResponse> actualResponse = orderControllerMock.deleteBySkuCode(orderNumber, skuCode);

		// then
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(expectedResponse, actualResponse.getBody());
	}


}
