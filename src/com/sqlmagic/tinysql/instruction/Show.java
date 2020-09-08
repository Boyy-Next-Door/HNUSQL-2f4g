package com.sqlmagic.tinysql.instruction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.DatabaseMapper;
import com.sqlmagic.tinysql.utils.*;
import com.sqlmagic.tinysql.entities.*;
import com.sqlmagic.tinysql.protocol.*;
import com.sqlmagic.tinysql.DatabaseMapper.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Show {
    private static Show instance;

    /*
    private Connection con;
    private PrintWriter out;
    private int requestType;

     */

    public static Show getInstance() {
        if (instance == null) {
            instance = new Show();
        }
        return instance;
    }

    /*
    public static Show getInstance(Connection con, PrintWriter out,int requestType){
        if(instance==null){
            instance=new Show(con,out,requestType);
        }
        return instance;
    }

     */

    private Show() {
    }

    /*
    private Show(Connection con, PrintWriter out,int requestType){
        this.con=con;
        this.out=out;
        this.requestType=requestType;
    }

     */


    public void whichShow(Connection con, PrintWriter out, int requestType) throws SQLException {
        if (requestType == Request.SHOW_TABLES) {
            List<String> respList = new ArrayList<>();
            ResultSet tables = con.getMetaData().getTables(null, null, null, null);
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                respList.add(tableName);
            }

            out.println(JSON.toJSONString(BaseResponse.ok("ok", respList)));
        } else if (requestType == Request.SHOW_DATABASES) {
            List<String> respList = new ArrayList<>();
            ArrayList<DatabaseMapper.MapperEntry> databases = getDatabaseList();
            for (DatabaseMapper.MapperEntry entry : databases) {
                respList.add(entry.getDatabaseName());
            }
            out.println(JSON.toJSONString(BaseResponse.ok("ok", respList)));
        }
    }

    private static ArrayList<DatabaseMapper.MapperEntry> getDatabaseList() {
        return DatabaseMapper.getDatabaseList();
    }
}


