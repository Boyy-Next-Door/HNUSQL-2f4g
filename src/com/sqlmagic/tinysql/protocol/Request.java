package com.sqlmagic.tinysql.protocol;

import com.alibaba.fastjson.JSONObject;

/**
 * 向服务器发起接口的请求
 */
public class Request {
    //具体指令编码
    public static final int SELECT = 101;
    public static final int INSERT = 102;
    public static final int UPDATE = 103;
    public static final int DELETE = 104;
    public static final int CREATE = 201;
    public static final int ALTER = 202;
    public static final int DROP = 203;
    public static final int GRANT = 204;
    public static final int REVOKE = 205;
    public static final int SHOW_DATABASES = 301;
    public static final int SHOW_TABLES = 302;


    //请求指令类型
    int requestType = 0;

    //原生sql语句
    String rawSQL = "";


    public Request(int requestType, String rawSQL) {
        this.requestType = requestType;
        this.rawSQL = rawSQL;
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getRawSQL() {
        return rawSQL;
    }

    public void setRawSQL(String rawSQL) {
        this.rawSQL = rawSQL;
    }


}
