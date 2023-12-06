package com.finalExercise.inventoryservice.customexception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest request) {
        CustomErrorResponse customErrorResponse =
                new CustomErrorResponse(ex.getMessage(),request.getRequestURI());
        System.out.println(customErrorResponse);
        return new ResponseEntity<>(customErrorResponse.toString(), HttpStatus.BAD_REQUEST);
    }
}
