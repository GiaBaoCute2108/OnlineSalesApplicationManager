package com.manager.appbanhangonline.model;

import java.util.List;

public class ThongKeDoanhThuModel {
    boolean success;
    String message;
    List<ThongKeDoanhThu> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ThongKeDoanhThu> getResult() {
        return result;
    }

    public void setResult(List<ThongKeDoanhThu> result) {
        this.result = result;
    }
}
