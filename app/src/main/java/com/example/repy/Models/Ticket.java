package com.example.repy.Models;

import java.text.DateFormat;
import java.io.Serializable;


public class Ticket {
    private String uid;
    private String ticketId;
    private DateFormat date;
    private Address address;
    private String carNum;
    private String cause;
    private double amount;
    private String currency;
    private TicketType ticketType;

    public Ticket(String uid, DateFormat date, String carNum, String ticketId, String cause, Address address, double amount, String currency, TicketType ticketType) {
        this.uid = uid;
        this.date = date;
        this.carNum = carNum;
        this.ticketId = ticketId;
        this.cause = cause;
        this.amount = amount;
        this.currency = currency;
        this.ticketType = ticketType;

        if (address == null || address.getCountry() == null) {
            throw new IllegalArgumentException("Country must be provided for all tickets.");
        }

        if (ticketType == TicketType.MUNICIPALITY && (address.getStreet() == null || address.getStreetNum() <= 0)) {
            throw new IllegalArgumentException("Municipality tickets must have a street and street number.");
        }

        this.address = address;
    }

    public Ticket() {}

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if (address == null || address.getCountry() == null) {
            throw new IllegalArgumentException("Country must be provided for all tickets.");
        }

        if (ticketType == TicketType.MUNICIPALITY && (address.getStreet() == null || address.getStreetNum() <= 0)) {
            throw new IllegalArgumentException("Municipality tickets must have a street and street number.");
        }

        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public DateFormat getDate() {
        return date;
    }

    public void setDate(DateFormat date) {
        this.date = date;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", ticketType=" + ticketType +
                ", date=" + date +
                ", address=" + address +
                ", carNum='" + carNum + '\'' +
                ", cause='" + cause + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
