package com.gasagency.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;
    private int domestic;
    private int commercial;
    private int small;

    // getters
    public int getId() { return id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getDomestic() { return domestic; }
    public void setDomestic(int domestic) { this.domestic = domestic; }

    public int getCommercial() { return commercial; }
    public void setCommercial(int commercial) { this.commercial = commercial; }

    public int getSmall() { return small; }
    public void setSmall(int small) { this.small = small; }
}