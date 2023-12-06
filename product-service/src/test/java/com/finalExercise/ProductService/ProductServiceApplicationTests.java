package com.finalExercise.ProductService;

import com.finalExercise.ProductService.controller.ProductController;
import com.finalExercise.ProductService.dto.ProductMapper;
import com.finalExercise.ProductService.dto.ProductRequest;
import com.finalExercise.ProductService.dto.ProductResponse;
import com.finalExercise.ProductService.repository.ProductRepository;
import com.finalExercise.ProductService.service.ProductService;
import org.apache.http.HttpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class ProductServiceApplicationTests {

	@InjectMocks
	private ProductController productController;

	@Mock
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@Test
	public void testviewAllProducts() throws HttpException {
		// given
		ProductResponse response1 = new ProductResponse("id1", "name1", "description1", BigDecimal.valueOf(100));
		ProductResponse response2 = new ProductResponse("id2", "name2", "description2", BigDecimal.valueOf(200));
		List<ProductResponse> expected = Arrays.asList(response1, response2);
		when(productService.viewAllProducts()).thenReturn(expected);

		// when
		List<ProductResponse> actual = productController.viewAllProducts();

		// then
		assertEquals(expected, actual);
	}

	@Test
	public void testviewProductByName()throws HttpException{
		// given

		ProductResponse response2 = new ProductResponse("id2", "Name2", "description2", BigDecimal.valueOf(200));
		List<ProductResponse> expected = Arrays.asList(response2);

		when(productService.viewProductsByName("Name2")).thenReturn(expected);


		// when
		List<ProductResponse> actual = productController.viewProductByName("Name2");

		// then
		assertEquals(expected, actual);
	}

	@Test
	void testdeleteById() throws HttpException {
		// given
		String id = "id1";
		ProductResponse productResponse = new ProductResponse();
		when(productService.viewProductById(id)).thenReturn(productResponse);

		// when
		ResponseEntity<ProductResponse> actualResponse = productController.deleteById(id);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(productResponse, actualResponse.getBody());
	}


@Test
	public void testviewProductById() throws HttpException {
		// given
		ProductResponse expected = new ProductResponse("id1", "name1", "description1", BigDecimal.valueOf(100));
		when(productService.viewProductById("id1")).thenReturn(expected);

		// when
		ProductResponse actual = productController.viewProductById("id1");

		// then
		assertEquals(expected, actual);
	}
	@Test
	public void testAddNewProduct() throws HttpException {
		// given
		ProductRequest request = new ProductRequest("name1", "description1", BigDecimal.valueOf(100));
		when(productService.getRandomId()).thenReturn("id1");

		MockHttpServletRequest servletRequest = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

		// when
		ResponseEntity<Object> responseEntity = productController.addNewProduct(request,0);

		// then
		assertEquals(201, responseEntity.getStatusCodeValue());
		assertEquals("http://localhost/id1", responseEntity.getHeaders().getLocation().toString());
	}




}
