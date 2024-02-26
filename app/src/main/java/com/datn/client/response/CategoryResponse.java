package com.datn.client.response;

import com.datn.client.models.Category;

import java.util.List;

public class CategoryResponse extends _BaseResponse {
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
