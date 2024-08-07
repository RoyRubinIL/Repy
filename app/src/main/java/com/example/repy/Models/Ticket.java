package com.example.repy.Models;

import java.text.DateFormat;

public class Ticket {
    private String ticketId;
    private DateFormat date;
    private Address address;
    private String carNum;
    private String cause;
    private double amount;
    private String currency;
    private TicketType ticketType;

    public Ticket(DateFormat date, String carNum, String reportId, String cause, Address address, double amount, String currency, TicketType ticketType) {
        this.address = address;
        this.date = date;
        this.carNum = carNum;
        this.ticketId = reportId;
        this.cause = cause;
        this.amount = amount;
        this.currency = currency;
        this.ticketType = ticketType;
    }

    public Ticket() {}

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DateFormat getDate() {
        return date;
    }

    public void setDate(DateFormat date) {
        this.date = date;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getReportId() {
        return ticketId;
    }

    public void setReportId(String reportId) {
        this.ticketId = reportId;
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

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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
