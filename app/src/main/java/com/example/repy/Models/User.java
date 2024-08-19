package com.example.repy.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Serializable {
    private String uid;
    private String id;
    private String name;
    private Address address;
    private String email;
    private String password;
    private String profileImage;


    public User() {
    }

    public User(String uid, String id, String name, Address address, String email, String password, String profileImage) {
        this.uid = uid;
        this.id = id;
        this.name = name;
        this.address = address;
        setEmail(email);
        setPassword(password);
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String validationMessage = validateEmail(email);
        if (validationMessage == null) {
            this.email = email;
        } else {
            throw new IllegalArgumentException(validationMessage);
        }
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String validationMessage = validatePassword(password);
        if (validationMessage == null) {
            this.password = password;
        } else {
            throw new IllegalArgumentException(validationMessage);
        }
    }

    public String validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return "Invalid email format";
        }
        return null;
    }

    public String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*\\W.*")) {
            return "Password must contain at least one special character";
        }
        return null;
    }


    @Override
    public String toString() {
        return "Appealer: " +
                "id='" + id + '\'' +
                ", full name='" + name + '\'' +
                ", address=" + address +
                ", email='" + email + '\'' +
                '}';
    }
}
