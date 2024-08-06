package com.example.repy.Models;


import java.text.DateFormat;

public class Report {
    private Country country;
    private DateFormat date;
    private DateFormat appealDate;
    private City city;
    private String id;
    private String cause;
    private double amount;

    public Report(Country country, DateFormat date, DateFormat appealDate, City city, String id, String cause, double amount) {
        this.country = country;
        this.date = date;
        this.appealDate = appealDate;
        this.city = city;
        this.id = id;
        this.cause = cause;
        this.amount = amount;
    }

    public Report(){
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public DateFormat getDate() {
        return date;
    }

    public void setDate(DateFormat date) {
        this.date = date;
    }

    public DateFormat getAppealDate() {
        return appealDate;
    }

    public void setAppealDate(DateFormat appealDate) {
        this.appealDate = appealDate;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Report{" +
                "country=" + country +
                ", date=" + date +
                ", appealDate=" + appealDate +
                ", city=" + city +
                ", id='" + id + '\'' +
                ", cause='" + cause + '\'' +
                ", amount=" + amount +
                '}';
    }
}
