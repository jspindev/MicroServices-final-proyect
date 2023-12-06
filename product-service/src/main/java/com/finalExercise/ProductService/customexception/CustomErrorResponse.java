package com.finalExercise.ProductService.customexception;

public class CustomErrorResponse {

    private String path;
    private String message;

    public CustomErrorResponse(String message,String path) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error Response{\n" +
                " message='" + message + '\'' +
                ",\n path='" + path + '\'' +
                "\n}";
    }

}
