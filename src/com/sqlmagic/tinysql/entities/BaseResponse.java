package com.sqlmagic.tinysql.entities;

import com.alibaba.fastjson.JSONObject;

public class BaseResponse {
    public int status;
    public String msg;
    public Object data;

    public static BaseResponse ok(Object data,String msg){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(0);
        baseResponse.setData(data);
        baseResponse.setMsg(msg);
        return baseResponse;
    }

    public static BaseResponse ok(Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(0);
        baseResponse.setData(data);
        baseResponse.setMsg("success");
        return baseResponse;
    }

    public static BaseResponse fail(String msg){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(1);
        baseResponse.setMsg(msg);
        baseResponse.setData(null);
        return baseResponse;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
