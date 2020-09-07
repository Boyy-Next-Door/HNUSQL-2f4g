package com.sqlmagic.tinysql;

import client.Info;
import com.alibaba.fastjson.JSON;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.protocol.Request;
import com.sun.xml.internal.rngom.parse.host.Base;
import usersystem.Admin;
import usersystem.UserTree;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.utils.*;
import com.sqlmagic.tinysql.DatabaseMapper.*;
import com.sqlmagic.tinysql.instruction.*;

public class clientHandler extends Thread{
    static Vector tableList;
    static String dbVersion;
    static FileWriter spoolFileWriter = (FileWriter) null;
    static String newLine = System.getProperty("line.separator");


    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static ObjectInputStream obin;

    public clientHandler(Socket clientSocket){
        this.clientSocket=clientSocket;
    }

    public void run(){
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            obin=new ObjectInputStream(clientSocket.getInputStream());

            DatabaseMetaData dbMeta;
            ResultSetMetaData meta;
            ResultSet display_rs,typesRS;
            BufferedReader loadFileReader;

            BufferedReader startReader=(BufferedReader)null;
            String[] fields;
            Connection con;
            Statement stmt;
            FieldTokenizer ft;
            PreparedStatement pstmt=(PreparedStatement)null;

            Logger logger = new Logger();
            int i,rsColCount,endAt,colWidth,colScale,colPrecision,typeCount,
                    colType,parameterIndex,b1,b2,parameterInt,startAt,columnIndex,valueIndex;
            String fName,tableName=null,inputString,cmdString,colTypeName,dbType,
                    parameterString,loadString,fieldString,readString;

            StringBuffer lineOut,prepareBuffer,valuesBuffer,inputBuffer;

            boolean echo=false;

            try
            {
                /*
                 *       Register the JDBC driver for dBase
                 */
                Class.forName("com.sqlmagic.tinysql.dbfFileDriver");
            } catch (ClassNotFoundException e) {
                System.err.println(
                        "JDBC Driver could not be registered!!\n");
                if ( tinySQLGlobals.DEBUG ) e.printStackTrace();
            }
            fName = ".";

            con = dbConnect(fName);
            if ( con == (Connection)null )
            {
                fName = ".";
                con = dbConnect(fName);
            }
            dbMeta = con.getMetaData();

            dbType = dbMeta.getDatabaseProductName();
            dbVersion = dbMeta.getDatabaseProductVersion();
            System.out.println("===================================================");
            System.out.println(dbType + " Command line interface version "
                    + dbVersion + " released March 15, 2007");
            System.out.println("Type HELP to get information on available commands.");


            Admin admin = Admin.getAdmin();
            UserTree tree = new UserTree(admin);

            //读取从客户端发过来的用户名和密码
            cmdString=in.readLine();
            JSONObject jsonObject= JSON.parseObject(cmdString);
            String username=jsonObject.getString("username");
            String password=jsonObject.getString("password");

            /*
            对用户名和密码进行判断
            暂时没有完成
             */

            //根据用户名生成cookie
            String cookie=MD5Util.MD5Encode(username,"");
            //返回给客户端
            out.println(cookie);

            cmdString = "NULL";
            stmt = con.createStatement();
            inputString = (String) null;


            while(true){
                try {
                    inputString = in.readLine().trim();
                    if(inputString==(String)null)break;
                    String clientCookie;
                    String requestType;
                    String rawSQL;
                    JSONObject obj= JSON.parseObject(inputString);
                    clientCookie=obj.getString("cookie");
                    requestType=obj.getString("requestType");
                    rawSQL=obj.getString("rawSQL");

                    inputString=rawSQL;

                    if (inputString.toUpperCase().startsWith("EXIT") |
                            inputString.toUpperCase().startsWith("QUIT")) break;
                    startAt = 0;

                    while (startAt < inputString.length() - 1) {
                        endAt = inputString.indexOf(";", startAt);
                        if (endAt == -1)
                            endAt = inputString.length();
                        cmdString = inputString.substring(startAt, endAt);
                        startAt = endAt + 1;

                        if (cmdString.toUpperCase().equals("SHOW DATABASES")) {
                            /*
                            List<String> respList=new ArrayList<>();            //这里用list作为BaseResponse的data

                            ArrayList<DatabaseMapper.MapperEntry> databases = getDatabaseList();
                            MyTableUtil database_name = new MyTableUtil().addColumn("database_name");

                            for (DatabaseMapper.MapperEntry entry : databases) {
                                database_name.addRow(entry.getDatabaseName());
                                respList.add(entry.getDatabaseName());
                            }
                            //System.out.println(database_name.generate());
                            //System.out.println("----------------------------------");

                            BaseResponse baseResponse =BaseResponse.ok(respList);
                            String str=JSONObject.toJSONString(baseResponse);
                            out.println(str);
                             */
                            //Show show=new Show();
                           // show.whichShow(con,out, Request.SHOW_DATABASES);
                        }
                        else if (cmdString.toUpperCase().equals("SHOW TABLES")) {/*
//                        for (i = 0; i < tableList.size(); i++)
//                            System.out.println((String) tableList.elementAt(i));
                            //从数据库连接重新读取元数据并返回表清单
                            List<String> respList=new ArrayList<>();
                            ResultSet tables = con.getMetaData().getTables(null, null, null, null);
                            tableList = new Vector();
                            MyTableUtil table = new MyTableUtil().addColumn("table_name");
                            while (tables.next()) {
                                tableName = tables.getString("TABLE_NAME");
                                tableList.addElement(tableName);
                                table.addRow(tableName);
                                respList.add(tableName);
                            }
                            //System.out.println(table.generate());
                            BaseResponse baseResponse =BaseResponse.ok(respList);
                            String str=JSONObject.toJSONString(baseResponse);
                            out.println(str);
                            */
                      //      Show show=new Show();
                      //      show.whichShow(con,out, Request.SHOW_TABLES);
                        }

                        else if (cmdString.toUpperCase().startsWith("SELECT")) {
                            /*
                            display_rs = stmt.executeQuery(cmdString);
                            if (display_rs == (ResultSet) null) {
                                BaseResponse baseResponse=BaseResponse.ok(null,"Null ResultSet returned from query");
                                String str=JSONObject.toJSONString(baseResponse);
                                out.println(str);
                                continue;
                            }
                            MyTableUtil myTableUtil=new MyTableUtil();
                            myTableUtil=buildResults(display_rs);
                            System.out.println(myTableUtil.generate());
                            BaseResponse baseResponse =BaseResponse.ok(myTableUtil);
                            String str=JSONObject.toJSONString(baseResponse);
                            out.println(str);

                             */
                            DqlDml dqlDml=new DqlDml();
                            dqlDml.SelectInsertUpdateDelete(con,stmt,Request.SELECT,out,cmdString);

                        }
                        else if(cmdString.toUpperCase().startsWith("INSERT")){
                            /*
                            if (cmdString.indexOf("?") > -1) {
                                pstmt = con.prepareStatement(cmdString);
                            } else {
                                try {
                                    stmt.executeUpdate(cmdString);
                                    //logger记录
                                    if(tinySQLGlobals.LOG) {
                                        logger.logStatement(cmdString);
                                    }
                                    //System.out.println("DONE\n");
                                    BaseResponse baseResponse =BaseResponse.ok(null);
                                    String str=JSONObject.toJSONString(baseResponse);
                                    out.println(str);
                                } catch (Exception upex) {
                                    System.out.println(upex.getMessage());
                                    BaseResponse baseResponse =BaseResponse.fail(null);
                                    String str=JSONObject.toJSONString(baseResponse);
                                    out.println(str);
                                    if (tinySQLGlobals.DEBUG) upex.printStackTrace();
                                }
                            }
                            */
                            DqlDml dqlDml=new DqlDml();
                            dqlDml.SelectInsertUpdateDelete(con,stmt,Request.INSERT,out,cmdString);

                        }



                    }

                } catch (Exception e) {
                    System.out.println("into Exception e");
                    System.out.println(e.getMessage());
                    cmdString = "EXIT";
                    break;
                }


            }




            out.close();
            in.close();
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MyTableUtil buildResults(ResultSet rs) throws java.sql.SQLException {
        System.out.println("======================================");
        MyTableUtil myTableUtil=new MyTableUtil();
        int numCols = 0, nameLength;
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        int[] width = new int[cols];

        boolean first = true;
        StringBuffer head = new StringBuffer();

        while (rs.next()) {

            String text = new String();
            List<String> textList=new ArrayList<>();

            for (int ii = 0; ii < cols; ii++) {
                String value = rs.getString(ii + 1);
                if (first) {

                    width[ii] = meta.getColumnDisplaySize(ii + 1);

                    nameLength = meta.getColumnName(ii + 1).length();
                    if (nameLength > width[ii]) width[ii] = nameLength;
                    System.out.println(meta.getColumnName(ii + 1));
                    myTableUtil.addColumn(meta.getColumnName(ii + 1));

                }
                text += padString(value, width[ii]);
                System.out.println("value="+value);
                textList.add(value);
                text += " ";   // the gap between the columns
            }
            first = false;
            //      System.out.println("print text");
            //     System.out.println(text);
            String[] strs=new String[textList.size()];
            for(int i=0;i<textList.size();i++){
                strs[i]=textList.get(i);
            }
            for(String s:strs){
                System.out.println(s);
            }
            myTableUtil.addRow(strs);

            numCols++;

        }
        //  myTableUtil.addColumn("id");myTableUtil.addColumn("name");
        //  myTableUtil.addRow("1","1");
        System.out.println(myTableUtil.generate());
        System.out.println("======================================");
        return myTableUtil;
    }


    private static ArrayList<DatabaseMapper.MapperEntry> getDatabaseList() {
        return DatabaseMapper.getDatabaseList();
    }



    private static String padString(int inputint, int padLength)
    {
        return padString(Integer.toString(inputint),padLength);
    }


    private static String padString(String inputString, int padLength)
    {
        StringBuffer outputBuffer;
        String blanks = "                                        ";
        if ( inputString != (String)null )
            outputBuffer = new StringBuffer(inputString);
        else
            outputBuffer = new StringBuffer(blanks);
        while ( outputBuffer.length() < padLength )
            outputBuffer.append(blanks);
        return outputBuffer.toString().substring(0,padLength);
    }



    private static Connection dbConnect(String tinySQLDir) throws SQLException {
        Connection con = null;
        DatabaseMetaData dbMeta;
        File conPath;
        File[] fileList;
        String tableName;
        ResultSet tables_rs;
        conPath = new File(tinySQLDir);
        fileList = conPath.listFiles();//返回某个目录下所有文件和目录的绝对路径
        if (fileList == null) {
//            System.out.println(tinySQLDir + " is not a valid directory.");
            System.out.println("database does not exist.");
            return (Connection) null;
        } else {
            if (conPath.getAbsolutePath().endsWith(".")) {
                System.out.println("Connecting to default database.");
            } else {
                //System.out.println("Connecting to " + conPath.getAbsolutePath());
                System.out.println("database changed.");
            }

            con = DriverManager.getConnection("jdbc:dbfFile:" + conPath, "", "");
        }
        dbMeta = con.getMetaData();
        tables_rs = dbMeta.getTables(null, null, null, null);
        tableList = new Vector();
        while (tables_rs.next()) {
            tableName = tables_rs.getString("TABLE_NAME");
            tableList.addElement(tableName);
        }

        System.out.println("There are " + tableList.size() + (tableList.size() > 0 ? " tables" : " table") + " in this database.");
        return con;
    }



}
