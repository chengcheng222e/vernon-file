package com.vernon.file.client;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 7/14/14
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public enum Product {
    User("user");

    private String value;

    Product(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
