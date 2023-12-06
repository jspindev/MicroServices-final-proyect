package com.finalExercise.inventoryservice;

import com.finalExercise.inventoryservice.controller.InventoryController;
import com.finalExercise.inventoryservice.dto.InventoryRequest;
import com.finalExercise.inventoryservice.dto.InventoryResponse;
import com.finalExercise.inventoryservice.model.Inventory;
import com.finalExercise.inventoryservice.service.InventoryService;
import feign.Response;
import org.junit.Assert;
import org.junit.Before;
import org.apache.http.HttpException;
import org.junit.After;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InventoryServiceApplicationTests {

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private InventoryService service;
	@Autowired
	private InventoryController controller;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeEach
	public void cleanupBefore() {
		int rowsAffected = jdbcTemplate.update("DELETE FROM inventories");
		jdbcTemplate.execute("ALTER TABLE inventories AUTO_INCREMENT = 1");
		System.out.println("Deleted " + rowsAffected + " rows from inventories table.");
	}


	@Test
	public void testRetrieveAllInventories() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		InventoryRequest request2 = new InventoryRequest("SKU2", 20);
		service.addItemToInventory(request);
		service.addItemToInventory(request2);

		InventoryResponse inventoryResponse1 = new InventoryResponse(1L, "SKU1", 10);
		InventoryResponse inventoryResponse2 = new InventoryResponse(2L, "SKU2", 20);


		List<InventoryResponse> expected = Arrays.asList(inventoryResponse1, inventoryResponse2);

		List<InventoryResponse> actual = controller.retrieveAllInventories();

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testRetrieveInventoryBySku() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		service.addItemToInventory(request);

		InventoryResponse expected = new InventoryResponse(1L, "SKU1", 10);

		InventoryResponse actual = controller.retrieveInventoryItem("SKU1");

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testRetrieveInventoryById() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		service.addItemToInventory(request);

		InventoryResponse expected = new InventoryResponse(1L, "SKU1", 10);

		InventoryResponse actual = controller.retrieveInventoryItemId(1L);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testAddInventory() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 20);
		InventoryResponse expected = new InventoryResponse(1L,"SKU1", 20);

		ResponseEntity<Object> responseEntity = controller.addInventory(request);
		System.out.println("Response Body: " + responseEntity.getBody());
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(expected);

	}

	@Test
	public void testAddInventoryFromProduct() throws HttpException {
		InventoryResponse expected = new InventoryResponse(1L,"SKU1", 10);

		ResponseEntity<Object> responseEntity = controller.newInventoryFromProduct("SKU1", 10);

		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testDeleteBySkuCode() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		InventoryResponse expected = service.addItemToInventory(request);

		ResponseEntity<Object> responseEntity = controller.deleteBySkuCode("SKU1");

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testDeleteBySkuId() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		InventoryResponse expected = service.addItemToInventory(request);

		ResponseEntity<InventoryResponse> responseEntity = controller.deleteById(1L);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testModifyQuantity() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		InventoryResponse expected = service.addItemToInventory(request);
		expected.setQuantity(expected.getQuantity() + 10);

		ResponseEntity<Object> responseEntity = controller.modifiedQuantity("SKU1", 10);

		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
		assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testInventoryEmpty() throws HttpException {
		try {
			controller.retrieveAllInventories();
			Assert.fail("Expected HttpException was not thrown");
		} catch (HttpException e) {
			Assert.assertEquals("No inventories found", e.getMessage());
		}
	}
	@Test
	public void testInventoryNotFoundBySku() throws HttpException {
		try {
			controller.retrieveInventoryItem("SKU1");
			Assert.fail("Expected HttpException was not thrown");
		} catch (HttpException e) {
			Assert.assertEquals("No inventory found for SkuCode: SKU1", e.getMessage());
		}
	}

	@Test
	public void testInventoryNotFoundById() throws HttpException {
		try {
			controller.retrieveInventoryItemId(1L);
			Assert.fail("Expected HttpException was not thrown");
		} catch (HttpException e) {
			Assert.assertEquals("No inventory found for ID: 1L", e.getMessage());
		}
	}
	@Test
	public void testAddInventorySkuDuplicated() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 20);
		InventoryResponse expected = new InventoryResponse(1L,"SKU1", 20);

		ResponseEntity<Object> responseEntity = controller.addInventory(request);
		try {
			controller.addInventory(request);
			Assert.fail("Expected HttpException was not thrown");
		} catch (HttpException e) {
			Assert.assertEquals("Error: SkuCode repeated", e.getMessage());
		}
	}

	@Test
	public void testNotEnoughQuantity() throws HttpException {
		InventoryRequest request = new InventoryRequest("SKU1", 10);
		InventoryResponse expected = service.addItemToInventory(request);

		try {
			controller.modifiedQuantity("SKU1", -20);
			Assert.fail("Expected HttpException was not thrown");
		} catch (HttpException e) {
			Assert.assertEquals("Error: Not that much quantity available", e.getMessage());
		}
	}

	@Test
	public void testModifyQuantityInventoryNotFound() throws HttpException {
		try {
			controller.modifiedQuantity("SKU1", -20);
			Assert.fail("Expected HttpException was not thrown");
		} catch (HttpException e) {
			Assert.assertEquals("Inventory not found with skuCode to DELETE: SKU1", e.getMessage());
		}
	}


}
