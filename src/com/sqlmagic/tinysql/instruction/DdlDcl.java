package com.sqlmagic.tinysql.instruction;

//数据定义语言DDL（Data Ddefinition Language）CREATE,DROP,ALTER
//数据控制功能DCL（Data Control Language）GRANT,REVOKE

import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.DatabaseMapper;
import com.sqlmagic.tinysql.Logger;
import com.sqlmagic.tinysql.entities.*;
import com.sqlmagic.tinysql.protocol.*;
import com.sqlmagic.tinysql.tinySQLGlobals;
import usersystem2.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class DdlDcl {
    private static DdlDcl instance;

    private DdlDcl(){}

    public static DdlDcl getInstance(){
        if(instance==null){
            instance=new DdlDcl();
        }
        return instance;
    }

    public void ddlAndDcl(String databaseName,Connection con, Statement statement, String username,
                          int requestType, PrintWriter out, String rawSQL, Logger logger)throws Exception{
        if(requestType== Request.CREATE){
            try {

                statement.executeUpdate(rawSQL);
                //logger记录
                if(tinySQLGlobals.LOG) {
                    logger.logStatement(rawSQL);
                }

                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);

                if(rawSQL.toUpperCase().startsWith("CREATE TABLE")){
                    //todo  给见表的用户授权  给admin也授权
                    PreParser preParser = new PreParser();
                    preParser.setParser(rawSQL);
                    String tableName = preParser.tableName;
                    User user = UserManager2.getUserByName(username);
                    User admin = UserManager2.getUserByName("admin");
                    Permission permission = new Permission();
                    HashMap<Table, Permission> tablePermissionHashMap= new HashMap<>();
                    Database database = new Database(databaseName);
                    Table table = new Table(database,tableName,(byte)0xff);
                    permission.setTarget(1);
                    permission.setDatabase(database);
                    permission.setTable(table);
                    permission.setPermission((byte)0xff);
                    permission.setGrantedBy(admin);
                    permission.setGrantType(2);
                    tablePermissionHashMap.put(table,permission);
                    user.setPermissions(tablePermissionHashMap);
                    admin.setPermissions(tablePermissionHashMap);

                    UserManager2.writeUsersToFile();
                }
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(upex.getMessage());
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.DROP){
            try {
                statement.executeUpdate(rawSQL);
                //logger记录
                if(tinySQLGlobals.LOG) {
                    logger.logStatement(rawSQL);
                }
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(upex.getMessage());
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.ALTER){
            try {
                statement.executeUpdate(rawSQL);
                //logger记录
                if(tinySQLGlobals.LOG) {
                    logger.logStatement(rawSQL);
                }
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(upex.getMessage());
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.GRANT){
            try {
//                int i = rawSQL.toUpperCase().indexOf("TO");
//                String substring = rawSQL.substring(i + 2).trim().split("\\s+")[0];
                rawSQL=rawSQL+" EX "+username;
                statement.executeUpdate(rawSQL);
                //logger记录
                if(tinySQLGlobals.LOG) {
                    logger.logStatement(rawSQL);
                }
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(upex.getMessage());
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.REVOKE){
            try {
//                int i = rawSQL.toUpperCase().indexOf("FROM");
//                String substring = rawSQL.substring(i + 2).trim().split("\\s+")[0];
                rawSQL=rawSQL+" EX "+username;
                statement.executeUpdate(rawSQL);
                //logger记录
                if(tinySQLGlobals.LOG) {
                    logger.logStatement(rawSQL);
                }
                BaseResponse baseResponse =BaseResponse.ok(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            } catch (Exception upex) {
                //System.out.println(upex.getMessage());
                BaseResponse baseResponse =BaseResponse.fail(upex.getMessage());
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else{
            BaseResponse baseResponse = BaseResponse.fail("Can't find requestType");
            String str=JSONObject.toJSONString(baseResponse);
            out.println(str);
        }
    }

}



