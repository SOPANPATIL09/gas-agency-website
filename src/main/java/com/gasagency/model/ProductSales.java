package com.gasagency.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_sales")
public class ProductSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;
    private String productName;
    private int productQty;
    private double productPrice;
    private double productTotal;
    private double cashReceived;
    private double onlineReceived;
    private double totalAmount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getProductQty() { return productQty; }
    public void setProductQty(int productQty) { this.productQty = productQty; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public double getProductTotal() { return productTotal; }
    public void setProductTotal(double productTotal) { this.productTotal = productTotal; }

    public double getCashReceived() { return cashReceived; }
    public void setCashReceived(double cashReceived) { this.cashReceived = cashReceived; }

    public double getOnlineReceived() { return onlineReceived; }
    public void setOnlineReceived(double onlineReceived) { this.onlineReceived = onlineReceived; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}