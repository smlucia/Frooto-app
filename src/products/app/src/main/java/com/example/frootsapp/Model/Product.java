package com.example.frootsapp.Model;

public class Product {
    private String name;
    private String price;
    private String description;
    private String image;


    public Product() {
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getPrice() {

        return price;
    }

    public void setPrice(String price) {

        this.price = price;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public String getImage() {

        return image;
    }


}

