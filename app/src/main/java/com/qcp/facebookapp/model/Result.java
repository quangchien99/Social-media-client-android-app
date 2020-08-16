package com.qcp.facebookapp.model;

public class Result {
    private int result_code;
    private String message;
    private String id;

    public Result() {
    }

    public Result(int result_code, String message, String id) {
        this.result_code = result_code;
        this.message = message;
        this.id = id;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
