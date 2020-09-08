package com.sqlmagic.tinysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.instruction.DdlDcl;
import com.sqlmagic.tinysql.instruction.DqlDml;
import com.sqlmagic.tinysql.instruction.PreParser;
import com.sqlmagic.tinysql.instruction.Show;
import com.sqlmagic.tinysql.protocol.Request;
import com.sqlmagic.tinysql.utils.CryptoUtil;
import com.sqlmagic.tinysql.utils.MD5Util;
import com.sqlmagic.tinysql.utils.MyTableUtil;
import usersystem2.UserManager2;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class clientHandler extends Thread {
    static Vector tableList;
    static String dbVersion;
    static FileWriter spoolFileWriter = (FileWriter) null;
    static String newLine = System.getProperty("line.separator");


    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static ObjectInputStream obin;

    public clientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            String cookie;
            String username = "";
            String password = "";
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            obin = new ObjectInputStream(clientSocket.getInputStream());

            DatabaseMetaData dbMeta;
            ResultSetMetaData meta;
            ResultSet display_rs, typesRS;
            BufferedReader loadFileReader;

            BufferedReader startReader = (BufferedReader) null;
            String[] fields;
            Connection con;
            Statement stmt;
            FieldTokenizer ft;
            PreparedStatement pstmt = (PreparedStatement) null;

            Logger logger = new Logger();
            int i, rsColCount, endAt, colWidth, colScale, colPrecision, typeCount,
                    colType, parameterIndex, b1, b2, parameterInt, startAt, columnIndex, valueIndex;
            String fName, tableName = null, inputString, cmdString, colTypeName, dbType,
                    parameterString, loadString, fieldString, readString;

            StringBuffer lineOut, prepareBuffer, valuesBuffer, inputBuffer;

            boolean echo = false;

            try {
                /*
                 *       Register the JDBC driver for dBase
                 */
                Class.forName("com.sqlmagic.tinysql.dbfFileDriver");
            } catch (ClassNotFoundException e) {
                System.err.println(
                        "JDBC Driver could not be registered!!\n");
                if (tinySQLGlobals.DEBUG) e.printStackTrace();
            }
            fName = ".";

            con = dbConnect(fName);

            dbMeta = con.getMetaData();

            dbType = dbMeta.getDatabaseProductName();
            dbVersion = dbMeta.getDatabaseProductVersion();


            cmdString = "NULL";
            stmt = con.createStatement();
            inputString = (String) null;

            Show show = Show.getInstance();
            DqlDml dqlDml = DqlDml.getInstance();
            DdlDcl ddlDcl = DdlDcl.getInstance();
            while (true) {
                try {
                    //获取客户端发来的请求
                    inputString = in.readLine().trim();
                    //空串则忽略
                    if (inputString == (String) null || inputString.isEmpty()) continue;
                    //解析请求
                    String clientCookie;        //用户身份cookie
                    int requestType;            //请求接口类型
                    String rawSQL;              //原始SQL语句
                    JSONObject obj = JSON.parseObject(inputString);
                    clientCookie = obj.getString("cookie");
                    //请求携带了cookie
                    if (clientCookie != null && !clientCookie.isEmpty()) {
                        //TODO 校验cookie
                        String temp=CryptoUtil.decodeTarget(clientCookie);
                        if(temp.charAt(0)=='{'&&temp.charAt(temp.length()-1)=='}') {
                            //如果校验通过
                            cookie = clientCookie;
                            username = CryptoUtil.decodeTarget(cookie);
                        }
                        //如果校验没有通过 说明请求用户身份非法
                        else out.println(JSON.toJSONString(BaseResponse.fail("Login status error.")));

                    } else {
                        //没有携带cookie 这个要按照具体功能接口做处理
                    }
                    requestType = obj.getInteger("requestType");
                    rawSQL = obj.getString("rawSQL");

                    inputString = rawSQL;

                    startAt = 0;


                    while (startAt < inputString.length() - 1) {
                        endAt = inputString.indexOf(";", startAt);
                        //这里是在处理多个以分号结尾的独立语句  实际上我们不允许这样操作 一次发送的指令指挥包含一条独立语句
                        if (endAt == -1)                                //没有以;结尾  认为字符串的末尾就是指令的结尾
                            endAt = inputString.length();
                        cmdString = inputString.substring(startAt, endAt);
                        startAt = endAt + 1;


                        if (requestType == Request.LOGIN) {     /*登陆*/
                            //读取从客户端发过来的用户名和密码
                            JSONObject jsonObject = JSON.parseObject(cmdString);
                            username = jsonObject.getString("username");
                            password = jsonObject.getString("password");

                            //校验参数
                            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                                //参数不正确
                                out.println(JSON.toJSONString(BaseResponse.fail("Parameter error.")));
                                continue;
                            }
                            //对用户名和密码进行判断
                            boolean isSuccess = UserManager2.login(username, password);

                            //登陆成功 创建cookie
                            if (isSuccess) {
                                //创建cookie
                                cookie = CryptoUtil.encodeSrc("{"+username+"}");
                                //返回给客户端
                                out.println(JSON.toJSONString(BaseResponse.ok("ok", cookie)));
                            } else {
                                out.println(JSON.toJSONString(BaseResponse.fail("Username or password error.")));
                            }

                        } else if (requestType == Request.USER_DATABASE) { /*选择数据库*/
                            String databaseName = cmdString.substring(4, cmdString.indexOf(";"));
                            String url = DatabaseMapper.getURL(databaseName);
                            //数据库不存在
                            if (url.equals("DB_NOT_EXIST")) {
                                out.println(JSON.toJSONString(BaseResponse.fail("Database doesn't exist.")));
                            } else {
                                fName = databaseName;
                                con = dbConnect(url);
                            }

                            //若成功 为logger设置url 并返回结果
                            if (!"DB_NOT_EXIST".equals(url) && con != null) {
                                logger.setDataDir(url);
                                //返回结果给客户端
                                out.println(JSON.toJSONString(BaseResponse.ok("ok", null)));
                            }
                        } else if (requestType == Request.SHOW_DATABASES || requestType == Request.SHOW_TABLES) { /*show类操作*/
                            show.whichShow(con, out, requestType);
                        } else if (requestType == Request.SELECT || requestType == Request.INSERT
                                || requestType == Request.UPDATE || requestType == Request.DELETE) { /*增删改查*/
                            // 首先对rowSQL进行词法分析 需要根据cookie解析得到的用户身份  讨论该用户是否有权利执行这项操作
                            PreParser preParser = new PreParser();
                            boolean isQualified = preParser.verifyPermission(cmdString, username, fName);

                            if (isQualified) {
                                //如果有权执行 在内部返回结果
                                dqlDml.SelectInsertUpdateDelete(con, stmt, requestType, out, cmdString);
                            } else {
                                //无权执行
                                out.println(JSON.toJSONString(BaseResponse.fail("Operation denied.")));
                            }
                        } else if (requestType == Request.CREATE || requestType == Request.ALTER
                                || requestType == Request.DROP || requestType == Request.GRANT
                                || requestType == Request.REVOKE) { /*数据定义语言、数据控制语言*/

                            //首先对rowSQL进行词法分析 需要根据cookie解析得到的用户身份  讨论该用户是否有权利执行这项操作
                            PreParser preParser = new PreParser();
                            boolean isQualified = preParser.verifyPermission(cmdString, username, fName);

                            if (isQualified) {
                                //如果有权执行 在内部返回结果
                                ddlDcl.ddlAndDcl(con, stmt, username, Request.DROP, out, cmdString);
                            } else {
                                //无权执行
                                out.println(JSON.toJSONString(BaseResponse.fail("Operation denied.")));
                            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public MyTableUtil buildResults(ResultSet rs) throws java.sql.SQLException {
        System.out.println("======================================");
        MyTableUtil myTableUtil = new MyTableUtil();
        int numCols = 0, nameLength;
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        int[] width = new int[cols];

        boolean first = true;
        StringBuffer head = new StringBuffer();

        while (rs.next()) {

            String text = new String();
            List<String> textList = new ArrayList<>();

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
                System.out.println("value=" + value);
                textList.add(value);
                text += " ";   // the gap between the columns
            }
            first = false;
            //      System.out.println("print text");
            //     System.out.println(text);
            String[] strs = new String[textList.size()];
            for (int i = 0; i < textList.size(); i++) {
                strs[i] = textList.get(i);
            }
            for (String s : strs) {
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


    private static String padString(int inputint, int padLength) {
        return padString(Integer.toString(inputint), padLength);
    }


    private static String padString(String inputString, int padLength) {
        StringBuffer outputBuffer;
        String blanks = "                                        ";
        if (inputString != (String) null)
            outputBuffer = new StringBuffer(inputString);
        else
            outputBuffer = new StringBuffer(blanks);
        while (outputBuffer.length() < padLength)
            outputBuffer.append(blanks);
        return outputBuffer.toString().substring(0, padLength);
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
