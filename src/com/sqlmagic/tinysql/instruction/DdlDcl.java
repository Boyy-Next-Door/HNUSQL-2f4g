package com.sqlmagic.tinysql.instruction;

//数据定义语言DDL（Data Ddefinition Language）CREATE,DROP,ALTER
//数据控制功能DCL（Data Control Language）GRANT,REVOKE

import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.DatabaseMapper;
import com.sqlmagic.tinysql.entities.*;
import com.sqlmagic.tinysql.protocol.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;


public class DdlDcl {
    private static DdlDcl instance;
    private Connection con;
    private PrintWriter out;
    private int requestType;
    private String cmdString;
    private String username;
    private DdlDcl(){}

    private DdlDcl(Connection con, PrintWriter out,int requestType,String username){
        this.con=con;
        this.out=out;
        this.requestType=requestType;
        this.username=username;
    }

    public static DdlDcl getInstance(){
        if(instance==null){
            instance=new DdlDcl();
        }
        return instance;
    }

    public static DdlDcl getInstance(Connection con, PrintWriter out,int requestType,String username){
        if(instance==null){
            instance=new DdlDcl(con,out, requestType,username);
        }
        return instance;
    }





    public void ddlAndDcl(Connection con, Statement statement,String username,
                          int requestType, PrintWriter out, String rawSQL)throws Exception{
        if(requestType== Request.CREATE){
            try {

                statement.executeUpdate(rawSQL);
                //logger记录
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.DROP){
            try {
                statement.executeUpdate(rawSQL);
                //logger记录
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.ALTER){
            try {
                statement.executeUpdate(rawSQL);
                //logger记录
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.GRANT){
            try {
                rawSQL=rawSQL+" EX "+username;
                statement.executeUpdate(rawSQL);
                //logger记录
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.REVOKE){
            try {
                rawSQL=rawSQL+" EX "+username;
                statement.executeUpdate(rawSQL);
                //logger记录
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
    }

    private static ArrayList<DatabaseMapper.MapperEntry> getDatabaseList() {
        return DatabaseMapper.getDatabaseList();
    }
}



