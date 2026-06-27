package com.gasagency.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class NewConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customerName;
    private String phoneNumber;
    private double connectionPrice;

    // NEW FIELDS
    private String connectionType;   // NC / DBC / Re-connection
    private int cylinderQty;
    private String paymentMode;      // cash / online / due
    private double cashAmount;
    private double onlineAmount;
    private double dueAmount;
    private String date;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public double getConnectionPrice() { return connectionPrice; }
    public void setConnectionPrice(double connectionPrice) { this.connectionPrice = connectionPrice; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public int getCylinderQty() { return cylinderQty; }
    public void setCylinderQty(int cylinderQty) { this.cylinderQty = cylinderQty; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public double getCashAmount() { return cashAmount; }
    public void setCashAmount(double cashAmount) { this.cashAmount = cashAmount; }

    public double getOnlineAmount() { return onlineAmount; }
    public void setOnlineAmount(double onlineAmount) { this.onlineAmount = onlineAmount; }

    public double getDueAmount() { return dueAmount; }
    public void setDueAmount(double dueAmount) { this.dueAmount = dueAmount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}