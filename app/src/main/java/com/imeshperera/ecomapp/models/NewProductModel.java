package com.imeshperera.ecomapp.models;

import java.io.Serializable;

public class NewProductModel implements Serializable {

    String img_url, name, brand, rate, detail, price, type;
    int stock;

    public NewProductModel() {
    }

    public NewProductModel(String img_url, String name, String brand, String detail, String price, String rate, String type, int stock) {
        this.img_url = img_url;
        this.name = name;
        this.rate = rate;
        this.brand = brand;
        this.detail = detail;
        this.price = price;
        this.type = type;
        this.stock = stock;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRate(String rate) {this.rate = rate; }

    public void setType(String type) { this.type = type; }

    public void setStock(int stock) { this.stock = stock; }

    public String getImg_url() {
        return img_url;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getDetail() {
        return detail;
    }

    public String getPrice() {
        return price;
    }

    public String getRate() { return rate; }

    public String getType() { return type; }

    public int getStock() { return stock; }
}
