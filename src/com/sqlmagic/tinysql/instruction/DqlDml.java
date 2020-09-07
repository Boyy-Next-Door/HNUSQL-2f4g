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
    public void SelectInsertUpdateDelete(Connection con, Statement statement,
                                         int requestType, PrintWriter out, String rawSQL)throws Exception{
        if(requestType== Request.SELECT){
            ResultSet display_rs;
            display_rs = statement.executeQuery(rawSQL);
            if (display_rs == (ResultSet) null) {
                BaseResponse baseResponse=BaseResponse.ok(null,"Null ResultSet returned from query");
                String str= JSONObject.toJSONString(baseResponse);
                out.println(str);
                return;
            }
            MyTableUtil myTableUtil=new MyTableUtil();
            myTableUtil=buildResults(display_rs);
            System.out.println(myTableUtil.generate());
            BaseResponse baseResponse =BaseResponse.ok(myTableUtil);
            String str=JSONObject.toJSONString(baseResponse);
            out.println(str);
        }
        else if (requestType==Request.INSERT){
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
        else if (requestType==Request.UPDATE){

        }
        else if (requestType==Request.DELETE){

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
