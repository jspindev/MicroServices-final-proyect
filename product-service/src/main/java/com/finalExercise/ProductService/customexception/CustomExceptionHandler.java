package com.finalExercise.ProductService.customexception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{


    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest request) {
        CustomErrorResponse customExceptionHandler =
                new CustomErrorResponse(ex.getMessage(),request.getRequestURI());
        System.out.println(customExceptionHandler);
        return new ResponseEntity<>(customExceptionHandler.toString(), HttpStatus.BAD_REQUEST);
    }
}