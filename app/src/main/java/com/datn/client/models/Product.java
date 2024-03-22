package com.datn.client.models;

import androidx.annotation.NonNull;

public class Product extends _BaseModel {
    private String category_id;
    private String name;
    private String ram;
    private String rom;
    private String color;
    private String quantity;
    private String description;
    private String sold;
    private String status;
    private String color_code;
    private String img_cover;
    private String video;
    private String price;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getImg_cover() {
        return img_cover;
    }

    public void setImg_cover(String img_cover) {
        this.img_cover = img_cover;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "category_id='" + category_id + '\'' +
                ", name='" + name + '\'' +
                ", ram='" + ram + '\'' +
                ", rom='" + rom + '\'' +
                ", color='" + color + '\'' +
                ", quantity='" + quantity + '\'' +
                ", description='" + description + '\'' +
                ", sold='" + sold + '\'' +
                ", status='" + status + '\'' +
                ", color_code='" + color_code + '\'' +
                ", img_cover='" + img_cover + '\'' +
                ", video='" + video + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
