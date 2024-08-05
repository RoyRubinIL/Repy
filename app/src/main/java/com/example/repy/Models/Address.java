package com.example.repy.Models;

import java.io.Serializable;

public class Address implements Serializable {
    private Country country;
    private City city;
    private String street;
    private int streetNum;

    public Address() {
    }

    public Address(Country country, City city, String street, int streetNum) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.streetNum = streetNum;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(int streetNum) {
        this.streetNum = streetNum;
    }

    @Override
    public String toString() {
        return street + " " + streetNum + ", " + city.getName() + ", " + country.getName();
    }
}
