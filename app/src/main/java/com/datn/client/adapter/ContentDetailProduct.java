package com.datn.client.adapter;

import android.content.Context;

import com.datn.client.models.Product;

import java.util.List;

public class ContentDetailProduct {

    private final List<Product> items;
    private final Context context;

    private boolean isPlaying = false;

    public ContentDetailProduct(Context context, List<Product> items) {
        this.items = items;
        this.context = context;
    }
}
