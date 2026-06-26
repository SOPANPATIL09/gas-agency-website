package com.gasagency.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_due")
public class CustomerDue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customerName;
    private String date;
    private String cylinderType;
    private double totalAmount;
    private double paidAmount;
    private String remarks;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCylinderType() { return cylinderType; }
    public void setCylinderType(String cylinderType) { this.cylinderType = cylinderType; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public double getRemainingAmount() { return Math.max(0, totalAmount - paidAmount); }
}