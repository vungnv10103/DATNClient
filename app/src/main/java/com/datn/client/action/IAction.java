package com.datn.client.action;

import com.datn.client.models._BaseModel;

public interface IAction {
    void onClick(_BaseModel baseModel);

    void onLongClick(_BaseModel baseModel);

    void onItemClick(_BaseModel baseModel);
}
