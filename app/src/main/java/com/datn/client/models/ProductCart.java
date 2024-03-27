package com.datn.client.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class ProductCart extends _BaseModel implements Parcelable {
    private String product_id;
    private String name;
    private String image;
    private String price;
    private String quantity_product;
    private String quantity_cart;
    private String note;
    private String status_product;
    private int status_cart;

    protected ProductCart(Parcel in) {
        product_id = in.readString();
        name = in.readString();
        image = in.readString();
        price = in.readString();
        quantity_product = in.readString();
        quantity_cart = in.readString();
        note = in.readString();
        status_product = in.readString();
        status_cart = in.readInt();
    }


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getQuantity_product() {
        return quantity_product;
    }

    public void setQuantity_product(String quantity_product) {
        this.quantity_product = quantity_product;
    }

    public String getQuantity_cart() {
        return quantity_cart;
    }

    public void setQuantity_cart(String quantity_cart) {
        this.quantity_cart = quantity_cart;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus_product() {
        return status_product;
    }

    public void setStatus_product(String status_product) {
        this.status_product = status_product;
    }

    public int getStatus_cart() {
        return status_cart;
    }

    public void setStatus_cart(int status_cart) {
        this.status_cart = status_cart;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductCart{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", price='" + price + '\'' +
                ", quantity_product='" + quantity_product + '\'' +
                ", quantity_cart='" + quantity_cart + '\'' +
                ", note='" + note + '\'' +
                ", status_product='" + status_product + '\'' +
                ", status_cart=" + status_cart +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(price);
        dest.writeString(quantity_product);
        dest.writeString(quantity_cart);
        dest.writeString(note);
        dest.writeString(status_product);
        dest.writeInt(status_cart);
    }

    public static final Creator<ProductCart> CREATOR = new Creator<ProductCart>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public ProductCart createFromParcel(Parcel in) {
            return new ProductCart(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public ProductCart[] newArray(int size) {
            return new ProductCart[size];
        }
    };
}
