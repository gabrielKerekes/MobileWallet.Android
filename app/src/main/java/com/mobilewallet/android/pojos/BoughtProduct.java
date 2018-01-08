package com.mobilewallet.android.pojos;

import static android.R.attr.id;

/**
 * Created by ROCK LEE on 16.1.2017.
 */

public class BoughtProduct {
    private String name;
    private double price;
    private int amount;
    private String date;
    private String topic_name;

    public BoughtProduct(String name, double price, int amoount, String date,  String topicName) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.date = date;
        this.topic_name = topicName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "id: " + id + "name: "+name+" price: "+price+" amount: "+amount + " date: " + date;
    }
}
