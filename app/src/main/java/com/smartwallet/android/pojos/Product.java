package com.smartwallet.android.pojos;

/**
 * Created by ROCK LEE on 23.11.2016.
 */

public class Product {


    private int id;
    private String name;
    private double price;
    private int available;

    public String getTopicName() {
        return topicName;
    }

    public void setTopic_name(String topic_name) {
        this.topicName = topic_name;
    }

    private String topicName;

    public Product() {}
    public Product(int id, String name, double price, int available, String topicName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
        this.topicName = topicName;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "id: " + id + "name: "+name+" price: "+price+" available: "+available;
    }
}
