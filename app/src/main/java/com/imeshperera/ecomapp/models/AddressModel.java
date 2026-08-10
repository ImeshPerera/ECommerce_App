package com.imeshperera.ecomapp.models;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String id;
    private String label;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String postal;
    private boolean isDefault;

    public AddressModel() {
    }

    public AddressModel(String id, String label, String name, String phone, String address, String city, String postal, boolean isDefault) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.postal = postal;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
