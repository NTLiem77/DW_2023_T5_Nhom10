package com.example.dtwh.Scheduler.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fact_kqxs")
public class FactKQXS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private int id_date;
    private String prize_type;
    private String winning_number;
    private int number_start;
    private String number_end;
    private boolean status_data;

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

    public int getId_date() {
        return id_date;
    }

    public void setId_date(int id_date) {
        this.id_date = id_date;
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

    public boolean isStatus_data() {
        return status_data;
    }

    public void setStatus_data(boolean status_data) {
        this.status_data = status_data;
    }
}