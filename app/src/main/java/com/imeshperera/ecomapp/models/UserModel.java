package com.imeshperera.ecomapp.models;

public class UserModel {
    private String name;
    private String phone;
    private String email;
    private String profileImageUrl;

    public UserModel() {}

    public UserModel(String name, String phone, String email, String profileImageUrl) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
