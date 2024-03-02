package com.datn.client.models;

public class _BaseModel {
    private String _id;
    private String created_at;

    public _BaseModel() {
    }

    public _BaseModel(String _id) {
        this._id = _id;
    }

    public _BaseModel(String _id, String created_at) {
        this._id = _id;
        this.created_at = created_at;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
