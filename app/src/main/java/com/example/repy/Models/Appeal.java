package com.example.repy.Models;

import java.text.DateFormat;

public class Appeal {
    private String id;
    private Ticket ticket;
    private DateFormat date;
    private AppealStatus status;
    private String reason;

    public Appeal(String id, Ticket ticket, AppealStatus status, String reason, DateFormat date) {
        this.id = id;
        this.ticket = ticket;
        this.status = status;
        this.date = date;
        setReason(reason);
    }

    public Appeal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateFormat getDate() {
        return date;
    }

    public void setDate(DateFormat date) {
        this.date = date;
    }

    public Ticket getTicket() {  // Correct the method name
        return ticket;
    }

    public void setTicket(Ticket ticket) {  // Correct the method name
        this.ticket = ticket;
    }

    public AppealStatus getStatus() {
        return status;
    }

    public void setStatus(AppealStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (reason != null && reason.length() <= 256) {
            this.reason = reason;
        } else {
            throw new IllegalArgumentException("Reason must be 256 characters or less.");
        }
    }

    @Override
    public String toString() {
        return "Appeal{" +
                "id='" + id + '\'' +
                ", date= " + date +
                ", ticket=" + ticket +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}
