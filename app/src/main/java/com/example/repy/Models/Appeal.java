package com.example.repy.Models;

import java.util.Date;

public class Appeal {
    private String uid;
    private String id;
    private String ticketId;
    private Date date = new Date();
    private AppealStatus status;
    private String reason;
    private String pdfUrl;

    public Appeal(String uid, String id, String ticketId, String reason) {
        this.uid = uid;
        this.id = id;
        this.ticketId = ticketId;
        this.status = AppealStatus.ACTIVE;
        setReason(reason);
    }

    public Appeal() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTicketId() {  // Corrected method name
        return ticketId;
    }

    public void setTicketId(String ticketId) {  // Corrected method name
        this.ticketId = ticketId;
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

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    @Override
    public String toString() {
        return "Appeal{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", ticket=" + ticketId +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}
