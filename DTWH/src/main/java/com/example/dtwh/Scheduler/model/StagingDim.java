package com.example.dtwh.Scheduler.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "staging_dim")
public class StagingDim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private Date date;
    private String prize_type;
    private String winning_number;
    private int number_start;
    private String number_end;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPrize_type() {
        return prize_type;
    }

    public void setPrize_type(String prize_type) {
        this.prize_type = prize_type;
    }

    public String getWinning_number() {
        return winning_number;
    }

    public void setWinning_number(String winning_number) {
        this.winning_number = winning_number;
    }

    public int getNumber_start() {
        return number_start;
    }

    public void setNumber_start(int number_start) {
        this.number_start = number_start;
    }

    public String getNumber_end() {
        return number_end;
    }

    public void setNumber_end(String number_end) {
        this.number_end = number_end;
    }
}