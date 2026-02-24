package com.ozshift.OzShift_App.dto;

import java.time.LocalDateTime;

public class ShiftCalendarEventDTO {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private double hourlyRate;
    private double pay;
    private String userName;

    public ShiftCalendarEventDTO(String title, LocalDateTime start, LocalDateTime end, double hourlyRate, double pay, String userName) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.hourlyRate = hourlyRate;
        this.pay = pay;
        this.userName = userName;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
