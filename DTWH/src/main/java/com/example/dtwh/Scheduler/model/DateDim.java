package com.example.dtwh.Scheduler.model;
import jakarta.persistence.*;

import java.sql.Date;
@Entity
@Table(name = "date_dim")
public class DateDim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
