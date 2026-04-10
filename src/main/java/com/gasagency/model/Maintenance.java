package com.gasagency.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;
    private double fuel;
    private double otherExpense;
    private String employeeName;
    private double employeePayment;
    private double bankDeposit;

    // getters & setters
    public int getId() { return id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getFuel() { return fuel; }
    public void setFuel(double fuel) { this.fuel = fuel; }

    public double getOtherExpense() { return otherExpense; }
    public void setOtherExpense(double otherExpense) { this.otherExpense = otherExpense; }

    public double getEmployeePayment() { return employeePayment; }
    public void setEmployeePayment(double employeePayment) { this.employeePayment = employeePayment; }

    public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getBankDeposit() { return bankDeposit; }
    public void setBankDeposit(double bankDeposit) { this.bankDeposit = bankDeposit; }
}