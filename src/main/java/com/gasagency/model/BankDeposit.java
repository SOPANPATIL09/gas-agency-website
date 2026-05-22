package com.gasagency.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_deposit")
public class BankDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;
    private double onlineDeposit;
    private double cashDeposit;
    private double totalDeposit;
    private String remarks;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getOnlineDeposit() { return onlineDeposit; }
    public void setOnlineDeposit(double onlineDeposit) { this.onlineDeposit = onlineDeposit; }

    public double getCashDeposit() { return cashDeposit; }
    public void setCashDeposit(double cashDeposit) { this.cashDeposit = cashDeposit; }

    public double getTotalDeposit() { return totalDeposit; }
    public void setTotalDeposit(double totalDeposit) { this.totalDeposit = totalDeposit; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}