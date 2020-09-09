package com.sqlmagic.tinysql.instruction;

import com.alibaba.fastjson.JSON;
import com.sqlmagic.tinysql.DatabaseMapper;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.protocol.Request;
import com.sqlmagic.tinysql.protocol.TableColumn;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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


    public void whichShow(Connection con, PrintWriter out, int requestType, String cmdString) throws SQLException {
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
        } else if (requestType == Request.DESCRIBE_TABLE) {
            ArrayList<TableColumn> respList = new ArrayList<>();
            DatabaseMetaData metaData = con.getMetaData();
            String tableName = cmdString.toUpperCase().substring(9);
            ResultSet display_rs = metaData.getColumns(null, null, tableName, null);

            while (display_rs.next()) {
                //字段名 字段类型名  字段长度 字段范围 字段精确度  display_rs.getInt(5)---字段类型
                respList.add(new TableColumn(
                        display_rs.getString(4), display_rs.getString(6),
                        String.valueOf(display_rs.getInt(7)), String.valueOf(display_rs.getInt(9)),
                        String.valueOf(display_rs.getInt(10))));
            }
            out.println(JSON.toJSONString(BaseResponse.ok("ok", respList)));
        }
    }

    private static ArrayList<DatabaseMapper.MapperEntry> getDatabaseList() {
        return DatabaseMapper.getDatabaseList();
    }



}


