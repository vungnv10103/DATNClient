package com.datn.client.task;

import android.os.AsyncTask;

public class CheckLoginTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Sau khi kiểm tra đăng nhập xong, khởi tạo dịch vụ
//        initService();
    }
}
