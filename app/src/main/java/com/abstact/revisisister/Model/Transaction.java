package com.abstact.revisisister.Model;

public class Transaction {
    private int idTransaction;
    private String nameTransaction, amountTransaction, payTransaction, totalPayTransaction, itemDateTransaction;
    private int priceTransaction, countTransaction;

    public Transaction(int idTransaction, String itemDateArrive, String nameTransaction, String amountTransaction, String payTransaction, String totalPayTransaction, int priceTransaction) {
        this.idTransaction = idTransaction;
        this.itemDateTransaction = itemDateArrive;
        this.nameTransaction = nameTransaction;
        this.amountTransaction = amountTransaction;
        this.payTransaction = payTransaction;
        this.totalPayTransaction = totalPayTransaction;
        this.priceTransaction = priceTransaction;
    }

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getItemDateTransaction() {
        return itemDateTransaction;
    }

    public void setItemDateTransaction(String itemDateTransaction) {
        this.itemDateTransaction = itemDateTransaction;
    }

    public String getNameTransaction() {
        return nameTransaction;
    }

    public void setNameTransaction(String nameTransaction) {
        this.nameTransaction = nameTransaction;
    }

    public String getAmountTransaction() {
        return amountTransaction;
    }

    public void setAmountTransaction(String amountTransaction) {
        this.amountTransaction = amountTransaction;
    }

    public String getPayTransaction() {
        return payTransaction;
    }

    public void setPayTransaction(String payTransaction) {
        this.payTransaction = payTransaction;
    }

    public String getTotalPayTransaction() {
        return totalPayTransaction;
    }

    public void setTotalPayTransaction(String totalPayTransaction) {
        this.totalPayTransaction = totalPayTransaction;
    }

    public int getPriceTransaction() {
        return priceTransaction;
    }

    public void setPriceTransaction(int priceTransaction) {
        this.priceTransaction = priceTransaction;
    }

    public int getCountTransaction() {
        return countTransaction;
    }

    public void setCountTransaction(int countTransaction) {
        this.countTransaction = countTransaction;
    }
}
