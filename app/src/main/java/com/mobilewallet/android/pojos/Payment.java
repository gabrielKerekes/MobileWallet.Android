package com.mobilewallet.android.pojos;

/**
 * Created by ROCK LEE on 29.11.2016.
 */

public class Payment {

    private double bankId;
    private double amount;
    private String dateCreated;

    public double getBankId() {
        return bankId;
    }

    public void setBankId(double bankId) {
        this.bankId = bankId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public Payment(double bankId, double amount, String dateCreated) {
        this.amount = amount;
        this.dateCreated = dateCreated;
        this.bankId = bankId;
    }

    @Override
    public String toString() {
        return "amount: "+amount+" dateCreated: "+dateCreated + " bankId:" + getBankId();
    }

}
