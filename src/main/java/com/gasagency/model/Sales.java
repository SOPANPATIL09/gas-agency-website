package com.gasagency.model;

import jakarta.persistence.*;

@Entity
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;

    // OLD CYLINDER FIELDS
    private int domestic;
    private int commercial;
    private int small;

    // NEW CYLINDER SYSTEM
    private String cylinderType;
    private int cylinderQty;
    private double cylinderPrice;
    private double cylinderTotal;

    // PRODUCT
    private String productName;
    private int productQty;
    private double productPrice;
    private double productTotal;

    // PAYMENT
    private double cashReceived;
    private double onlineReceived;
    private double totalAmount;

    // ================= GETTERS / SETTERS =================

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // OLD
    public int getDomestic() {
        return domestic;
    }

    public void setDomestic(int domestic) {
        this.domestic = domestic;
    }

    public int getCommercial() {
        return commercial;
    }

    public void setCommercial(int commercial) {
        this.commercial = commercial;
    }

    public int getSmall() {
        return small;
    }

    public void setSmall(int small) {
        this.small = small;
    }

    // NEW CYLINDER
    public String getCylinderType() {
        return cylinderType;
    }

    public void setCylinderType(String cylinderType) {
        this.cylinderType = cylinderType;
    }

    public int getCylinderQty() {
        return cylinderQty;
    }

    public void setCylinderQty(int cylinderQty) {
        this.cylinderQty = cylinderQty;
    }

    public double getCylinderPrice() {
        return cylinderPrice;
    }

    public void setCylinderPrice(double cylinderPrice) {
        this.cylinderPrice = cylinderPrice;
    }

    public double getCylinderTotal() {
        return cylinderTotal;
    }

    public void setCylinderTotal(double cylinderTotal) {
        this.cylinderTotal = cylinderTotal;
    }

    // PRODUCT
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(double productTotal) {
        this.productTotal = productTotal;
    }

    // PAYMENT
    public double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public double getOnlineReceived() {
        return onlineReceived;
    }

    public void setOnlineReceived(double onlineReceived) {
        this.onlineReceived = onlineReceived;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}