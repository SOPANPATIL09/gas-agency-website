package com.gasagency.model;

import jakarta.persistence.*;

@Entity
@Table(name = "csc_sales")
public class CscSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;
    private String cscCenterName;
    private String cylinderType;
    private int cylinderQty;
    private double cylinderPrice;   // entered manually by user
    private double cylinderTotal;   // auto = qty * price
    private double cashReceived;
    private double onlineReceived;
    private double totalAmount;     // same as cylinderTotal

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCscCenterName() { return cscCenterName; }
    public void setCscCenterName(String cscCenterName) { this.cscCenterName = cscCenterName; }

    public String getCylinderType() { return cylinderType; }
    public void setCylinderType(String cylinderType) { this.cylinderType = cylinderType; }

    public int getCylinderQty() { return cylinderQty; }
    public void setCylinderQty(int cylinderQty) { this.cylinderQty = cylinderQty; }

    public double getCylinderPrice() { return cylinderPrice; }
    public void setCylinderPrice(double cylinderPrice) { this.cylinderPrice = cylinderPrice; }

    public double getCylinderTotal() { return cylinderTotal; }
    public void setCylinderTotal(double cylinderTotal) { this.cylinderTotal = cylinderTotal; }

    public double getCashReceived() { return cashReceived; }
    public void setCashReceived(double cashReceived) { this.cashReceived = cashReceived; }

    public double getOnlineReceived() { return onlineReceived; }
    public void setOnlineReceived(double onlineReceived) { this.onlineReceived = onlineReceived; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}