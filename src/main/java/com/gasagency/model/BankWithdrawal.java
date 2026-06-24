package com.gasagency.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_withdrawal")
public class BankWithdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;
    private double onlineWithdrawal;
    private double cashWithdrawal;
    private double totalWithdrawal;
    private String remarks;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getOnlineWithdrawal() { return onlineWithdrawal; }
    public void setOnlineWithdrawal(double onlineWithdrawal) { this.onlineWithdrawal = onlineWithdrawal; }

    public double getCashWithdrawal() { return cashWithdrawal; }
    public void setCashWithdrawal(double cashWithdrawal) { this.cashWithdrawal = cashWithdrawal; }

    public double getTotalWithdrawal() { return totalWithdrawal; }
    public void setTotalWithdrawal(double totalWithdrawal) { this.totalWithdrawal = totalWithdrawal; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}