package com.pos.leaders.leaderspossystem.Models;

import java.sql.Timestamp;

/**
 * Created by KARAM on 31/10/2017.
 */

public class CreditCardPayment {
    private long creditCardPaymentId;
    private long orderId;
    private double amount;
    private String creditCardCompanyName;
    private int transactionType;
    private String last4Digits;
    private String transactionId;
    private String answer;
    private int paymentsNumber;
    private double firstPaymentAmount;
    private double otherPaymentAmount;
    private String cardholder;
    private Timestamp createdAt;


    public CreditCardPayment() {
    }

    public CreditCardPayment(long orderId, double amount, String creditCardCompanyName, int transactionType, String last4Digits, String transactionId, String answer, int paymentsNumber, double firstPaymentAmount, double otherPaymentAmount, String cardholder, Timestamp createdAt) {
        this.orderId = orderId;
        this.amount = amount;
        this.creditCardCompanyName = creditCardCompanyName;
        this.transactionType = transactionType;
        this.last4Digits = last4Digits;
        this.transactionId = transactionId;
        this.answer = answer;
        this.paymentsNumber = paymentsNumber;
        this.firstPaymentAmount = firstPaymentAmount;
        this.otherPaymentAmount = otherPaymentAmount;
        this.cardholder = cardholder;
        this.createdAt = createdAt;
    }

    public CreditCardPayment(long creditCardPaymentId, long orderId, double amount, String creditCardCompanyName, int transactionType, String last4Digits, String transactionId, String answer, int paymentsNumber, double firstPaymentAmount, double otherPaymentAmount, String cardholder, Timestamp createdAt) {
        this.creditCardPaymentId = creditCardPaymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.creditCardCompanyName = creditCardCompanyName;
        this.transactionType = transactionType;
        this.last4Digits = last4Digits;
        this.transactionId = transactionId;
        this.answer = answer;
        this.paymentsNumber = paymentsNumber;
        this.firstPaymentAmount = firstPaymentAmount;
        this.otherPaymentAmount = otherPaymentAmount;
        this.cardholder = cardholder;
        this.createdAt = createdAt;
    }

    //region Getters

    public long getCreditCardPaymentId() {
        return creditCardPaymentId;
    }

    public long getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCreditCardCompanyName() {
        return creditCardCompanyName;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAnswer() {
        return answer;
    }

    public int getPaymentsNumber() {
        return paymentsNumber;
    }

    public double getFirstPaymentAmount() {
        return firstPaymentAmount;
    }

    public double getOtherPaymentAmount() {
        return otherPaymentAmount;
    }

    public String getCardholder() {
        return cardholder;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }


    //endregion Getters

    //region Setters

    public void setCreditCardPaymentId(long creditCardPaymentId) {
        this.creditCardPaymentId = creditCardPaymentId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCreditCardCompanyName(String creditCardCompanyName) {
        this.creditCardCompanyName = creditCardCompanyName;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setPaymentsNumber(int paymentsNumber) {
        this.paymentsNumber = paymentsNumber;
    }

    public void setFirstPaymentAmount(double firstPaymentAmount) {
        this.firstPaymentAmount = firstPaymentAmount;
    }

    public void setOtherPaymentAmount(double otherPaymentAmount) {
        this.otherPaymentAmount = otherPaymentAmount;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    //endregion Setters
    @Override
    public String toString() {
        return "{" + "\"@type\":" + "\"CreditCardPayment\"" +
                ",\"creditCardPaymentId\":" + creditCardPaymentId +
                ",\"orderId\":" + orderId +
                ", \"amount\":" + amount +
                ", \"creditCardCompanyName\":" + "\"" + creditCardCompanyName + "\"" +
                ", \"transactionType\":" + transactionType +
                ", \"last4Digits\":" + "\"" + last4Digits + "\"" +
                ", \"transactionId\":" + "\"" + transactionId + "\"" +
                ", \"answer\":" + "\"" + answer + "\"" +
                ", \"paymentsNumber\":" + paymentsNumber +
                ", \"firstPaymentAmount\":" + firstPaymentAmount +
                ", \"otherPaymentAmount\":" + otherPaymentAmount +
                ", \"cardholder\":" + "\"" + cardholder + "\"" +
                ", \"createdAt\":" + "\"" + createdAt + "\"" +
                "}";
    }

}
