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

            Show show=Show.getInstance();
            DqlDml dqlDml=DqlDml.getInstance();

            while(true){
                try {
                    inputString = in.readLine().trim();
                    if(inputString==(String)null)break;
                    String clientCookie;
                    int requestType;
                    String rawSQL;
                    JSONObject obj= JSON.parseObject(inputString);
                    clientCookie=obj.getString("cookie");
                    //requestType=obj.getString("requestType");
                    requestType=obj.getInteger("requestType");
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

                        if(requestType==Request.SHOW_DATABASES){
                            show.whichShow(con,out,Request.SHOW_DATABASES);
                        }
                        else if(requestType==Request.SHOW_TABLES){
                            show.whichShow(con,out,Request.SHOW_TABLES);
                        }
                        else if(requestType==Request.SELECT){
                            dqlDml.SelectInsertUpdateDelete(con,stmt,Request.SELECT,out,cmdString);
                        }
                        else if(requestType==Request.INSERT){
                            dqlDml.SelectInsertUpdateDelete(con,stmt,Request.INSERT,out,cmdString);
                        }
                        else if(requestType==Request.UPDATE){
                            dqlDml.SelectInsertUpdateDelete(con,stmt,Request.UPDATE,out,cmdString);
                        }
                        else if(requestType==Request.DELETE){
                            dqlDml.SelectInsertUpdateDelete(con,stmt,Request.DELETE,out,cmdString);
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
