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
    public static final int DESCRIBE_TABLE = 303;


    //共功能性接口
    public static final int LOGIN = 401;                //登陆数据库
    public static final int USE_DATABASE = 402;         //选择数据库
    public static final int REGISTER = 403;             //注册


    //由服务器生成的cookie
    String cookie="";

    //请求指令类型
    int requestType = 0;

    //原生sql语句
    String rawSQL = "";

    String username="";
    String password="";

    public Request(int requestType, String rawSQL) {
        this.requestType = requestType;
        this.rawSQL = rawSQL;
    }

    public Request(String cookie,int requestType, String rawSQL) {
        this.cookie=cookie;
        this.requestType = requestType;
        this.rawSQL = rawSQL;
    }

    public Request(String cookie,int requestType, String rawSQL,String username,String password) {
        this.cookie=cookie;
        this.requestType = requestType;
        this.rawSQL = rawSQL;
        this.username=username;
        this.password=password;
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

    public String getCookie() { return cookie; }

    public void setCookie(String cookie) { this.cookie = cookie; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
