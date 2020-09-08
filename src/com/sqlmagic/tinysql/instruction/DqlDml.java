package com.sqlmagic.tinysql.instruction;

import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.Logger;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.protocol.Request;
import com.sqlmagic.tinysql.tinySQLGlobals;
import com.sqlmagic.tinysql.utils.MyTableUtil;



import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//数据查询语言DQL（Data Query Language） SELECT
//数据操纵语言DML（Data Manipulation Language）INSERT,UPDATE,DELETE
public class DqlDml {

    private static DqlDml instance;


    public static DqlDml getInstance(){
        if(instance==null){
            instance=new DqlDml();
        }
        return instance;
    }

    private DqlDml(){}

    public void SelectInsertUpdateDelete(Connection con, Statement statement,
                                         int requestType, PrintWriter out, String rawSQL,Logger logger)throws Exception{
        if(requestType== Request.SELECT){
            ResultSet display_rs;
            display_rs = statement.executeQuery(rawSQL);
            if (display_rs == (ResultSet) null) {
                BaseResponse baseResponse=BaseResponse.ok("Null ResultSet returned from query",null);
                String str= JSONObject.toJSONString(baseResponse);
                out.println(str);
                return;
            }
            MyTableUtil myTableUtil=new MyTableUtil();
            myTableUtil=buildResults(display_rs);
           // String s=JSONObject.toJSONString(myTableUtil);
           // System.out.println(s);
            String generate = myTableUtil.generate();
            System.out.println(generate);
            BaseResponse baseResponse =BaseResponse.ok(myTableUtil.generate());
            String str=JSONObject.toJSONString(baseResponse);
            out.println(str);
        }
        else if (requestType==Request.INSERT){
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
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.UPDATE){
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
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
        else if (requestType==Request.DELETE){
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
                BaseResponse baseResponse =BaseResponse.fail(null);
                String str=JSONObject.toJSONString(baseResponse);
                out.println(str);
            }
        }
    }

    public MyTableUtil buildResults(ResultSet rs) throws java.sql.SQLException {
        MyTableUtil myTableUtil=new MyTableUtil();
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        boolean first = true;
        StringBuffer head = new StringBuffer();
        while (rs.next()) {
            List<String> textList=new ArrayList<>();
            for (int ii = 0; ii < cols; ii++) {
                String value = rs.getString(ii + 1);
                if (first) {
                    myTableUtil.addColumn(meta.getColumnName(ii + 1));
                }
                textList.add(value);
            }
            first = false;
            String[] strs=new String[textList.size()];
            for(int i=0;i<textList.size();i++){
                strs[i]=textList.get(i);
            }
            myTableUtil.addRow(strs);
        }
        return myTableUtil;
    }

}
