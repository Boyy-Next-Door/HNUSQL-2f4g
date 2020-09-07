package com.sqlmagic.tinysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.utils.MD5Util;
import com.sqlmagic.tinysql.utils.MyTableUtil;
import usersystem.Admin;
import usersystem.UserTree;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

//            System.out.println("login success.");




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

                        }

                        else if (cmdString.toUpperCase().equals("SHOW TABLES")) {
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
                        }
                        else if (cmdString.toUpperCase().startsWith("SELECT")) {
                            display_rs = stmt.executeQuery(cmdString);
                            if (display_rs == (ResultSet) null) {
                                // System.out.println("Null ResultSet returned from query");
                                BaseResponse baseResponse=BaseResponse.ok(null,"Null ResultSet returned from query");
                                String str=JSONObject.toJSONString(baseResponse);
                                out.println(str);
                                continue;
                            }
                            MyTableUtil myTableUtil=new MyTableUtil();
                            myTableUtil=buildResults(display_rs);
                            System.out.println("hahaha");
                            System.out.println(myTableUtil.generate());
                            BaseResponse baseResponse =BaseResponse.ok(myTableUtil.generate());
                            String str=JSONObject.toJSONString(baseResponse);
                            System.out.println(str);

                        }


                    }

                } catch (Exception e) {
                    System.out.println("into Exception e");
                    System.out.println(e.getMessage());
                    cmdString = "EXIT";
                    break;
                }


            }














/*

            while (!cmdString.toUpperCase().equals("EXIT")) {
                try {
                    if (startReader != (BufferedReader) null) {
                        System.out.println("aaaaaa");
                        inputBuffer = new StringBuffer();
                        inputString = (String) null;
                        while ((readString = startReader.readLine()) != null) {
                            if (readString.startsWith("--") |
                                    readString.startsWith("#")) continue;
                            inputBuffer.append(readString + " ");

                            ft = new FieldTokenizer(inputBuffer.toString(), ';', true);
                            if (ft.countFields() > 1) {
                                inputString = inputBuffer.toString();
                                break;
                            }
                        }
                        if (inputString == (String) null) {
                            System.out.println("bbbbbb");
                            startReader = (BufferedReader) null;
                            continue;
                        }
                    }

                    else{
                        System.out.print("tinySQL>");
                        inputString = in.readLine();
                        System.out.println(inputString);
                       // JSONObject obj= JSON.parseObject(inputString);
                        //inputString=obj.getString("rawSQL");
                        //System.out.println(inputString);

                    }

                    if (inputString == (String) null) {
                        System.out.println("ccccc");
                        break;
                    }

                    if (inputString.toUpperCase().startsWith("EXIT") |
                            inputString.toUpperCase().startsWith("QUIT")) break;

                    startAt = 0;
                    while (startAt < inputString.length() - 1) {//输出的命令必须大于1。否则忽略不计
                        System.out.println("zzzzzzz");
                        endAt = inputString.indexOf(";", startAt);
                        if (endAt == -1)
                            endAt = inputString.length();
                        cmdString = inputString.substring(startAt, endAt);
                        if (echo) System.out.println(cmdString);//输出命令
                        startAt = endAt + 1;

                        if (cmdString.toUpperCase().startsWith("SELECT")) {
                            display_rs = stmt.executeQuery(cmdString);
                            if (display_rs == (ResultSet) null) {
                                System.out.println("Null ResultSet returned from query");
                                out.println("Null ResultSet returned from query");
                                continue;
                            }
                            meta = display_rs.getMetaData();

                            rsColCount = meta.getColumnCount();
                            lineOut = new StringBuffer(100);
                            int[] columnWidths = new int[rsColCount];
                            int[] columnScales = new int[rsColCount];
                            int[] columnPrecisions = new int[rsColCount];
                            int[] columnTypes = new int[rsColCount];
                            String[] columnNames = new String[rsColCount];
                            for (i = 0; i < rsColCount; i++) {
                                columnNames[i] = meta.getColumnName(i + 1);
                                columnWidths[i] = meta.getColumnDisplaySize(i + 1);
                                columnTypes[i] = meta.getColumnType(i + 1);
                                columnScales[i] = meta.getScale(i + 1);
                                columnPrecisions[i] = meta.getPrecision(i + 1);
                                if (columnNames[i].length() > columnWidths[i])
                                    columnWidths[i] = columnNames[i].length();
                                lineOut.append(padString(columnNames[i], columnWidths[i]) + " ");
                            }
                            if (tinySQLGlobals.DEBUG)
                                System.out.println(lineOut.toString());
                            displayResults(display_rs);
                            out.println("DONE");
                        }
                        else if (cmdString.toUpperCase().startsWith("CONNECT")) {
                            con = dbConnect(cmdString.substring(8, cmdString.length()));
                        }
                        else if (cmdString.toUpperCase().startsWith("HELP")) {
                 //           helpMsg(cmdString);
                        }
                        else if (cmdString.toUpperCase().startsWith("DESCRIBE")) {
                            dbMeta = con.getMetaData();
                            tableName = cmdString.toUpperCase().substring(9);
                            display_rs = dbMeta.getColumns(null, null, tableName, null);
                            System.out.println("\nColumns for table " + tableName + "\n"
                                    + "Name                            Type");
                            //out.println("\nColumns for table " + tableName + "\n"
                            //        + "Name                            Type");
                            while (display_rs.next()) {
                                lineOut = new StringBuffer(100);
                                lineOut.append(padString(display_rs.getString(4), 32));
                                colTypeName = display_rs.getString(6);
                                colType = display_rs.getInt(5);
                                colWidth = display_rs.getInt(7);
                                colScale = display_rs.getInt(9);
                                colPrecision = display_rs.getInt(10);
                                if (colTypeName.equals("CHAR")) {
                                    colTypeName = colTypeName + "("
                                            + Integer.toString(colWidth) + ")";
                                } else if (colTypeName.equals("FLOAT")) {
                                    colTypeName += "(" + Integer.toString(colPrecision)
                                            + "," + Integer.toString(colScale) + ")";
                                }
                              lineOut.append(padString(colTypeName, 20) + padString(colType, 12));
                                System.out.println(lineOut.toString());
                                out.println(lineOut.toString());
                            }
                            out.println("DONE");
                        }
                        else if (cmdString.toUpperCase().equals("SHOW TABLES")) {//show tables;
                            System.out.println("into show tables;");
                            for (i = 0; i < tableList.size(); i++) {
                                System.out.println((String) tableList.elementAt(i));
                                out.println((String) tableList.elementAt(i));
                            }
                            out.println("DONE");
                        }
                        else if (cmdString.toUpperCase().equals("SHOW TYPES")) {
                            typesRS = dbMeta.getTypeInfo();
                            typeCount = displayResults(typesRS);
                            out.println("DONE");
                        }
                        else if (cmdString.toUpperCase().startsWith("SET ")) {

                            ft = new FieldTokenizer(cmdString.toUpperCase(), ' ', false);
                            fields = ft.getFields();
                            if (fields[1].equals("ECHO")) {
                                if (fields[2].equals("ON")) echo = true;
                                else echo = false;
                            } else if (fields[1].equals("DEBUG")) {
                                if (fields[2].equals("ON")) tinySQLGlobals.DEBUG = true;
                                else tinySQLGlobals.DEBUG = false;
                            } else if (fields[1].equals("PARSER_DEBUG")) {
                                if (fields[2].equals("ON"))
                                    tinySQLGlobals.PARSER_DEBUG = true;
                                else tinySQLGlobals.PARSER_DEBUG = false;
                            } else if (fields[1].equals("WHERE_DEBUG")) {
                                if (fields[2].equals("ON"))
                                    tinySQLGlobals.WHERE_DEBUG = true;
                                else tinySQLGlobals.WHERE_DEBUG = false;
                            } else if (fields[1].equals("EX_DEBUG")) {
                                if (fields[2].equals("ON"))
                                    tinySQLGlobals.EX_DEBUG = true;
                                else tinySQLGlobals.EX_DEBUG = false;
                            }
                        } else if (cmdString.toUpperCase().startsWith("SPOOL ")) {

                            ft = new FieldTokenizer(cmdString, ' ', false);
                            fName = ft.getField(1);
                            if (fName.equals("OFF")) {
                                try {
                                    spoolFileWriter.close();
                                } catch (Exception spoolEx) {
                                    System.out.println("Unable to close spool file "
                                            + spoolEx.getMessage() + newLine);
                                    out.println("Unable to close spool file "
                                            + spoolEx.getMessage() + newLine);
                                }
                            } else {
                                try {
                                    spoolFileWriter = new FileWriter(fName);
                                    if (spoolFileWriter != (FileWriter) null) {
                                        System.out.println("Output spooled to " + fName);
                                        out.println("Output spooled to " + fName);
                                    }
                                } catch (Exception spoolEx) {
                                    System.out.println("Unable to spool to file "
                                            + spoolEx.getMessage() + newLine);
                                    out.println("Unable to spool to file "
                                            + spoolEx.getMessage() + newLine);
                                }
                            }
                        } else if (cmdString.toUpperCase().startsWith("START ")) {
                            ft = new FieldTokenizer(cmdString, ' ', false);
                            fName = ft.getField(1);
                            if (!fName.toUpperCase().endsWith(".SQL")) fName += ".SQL";
                            try {
                                startReader = new BufferedReader(new FileReader(fName));
                            } catch (Exception ex) {
                                startReader = (BufferedReader) null;
                                throw new tinySQLException("No such file: " + fName);
                            }
                        } else if (cmdString.toUpperCase().startsWith("LOAD")) {
                            ft = new FieldTokenizer(cmdString, ' ', false);
                            fName = ft.getField(1);
                            tableName = ft.getField(3);
                            display_rs = stmt.executeQuery("SELECT * FROM " + tableName);
                            meta = display_rs.getMetaData();
                            rsColCount = meta.getColumnCount();


                            prepareBuffer = new StringBuffer("INSERT INTO " + tableName);
                            valuesBuffer = new StringBuffer(" VALUES");
                            for (i = 0; i < rsColCount; i++) {
                                if (i == 0) {
                                    prepareBuffer.append(" (");
                                    valuesBuffer.append(" (");
                                } else {
                                    prepareBuffer.append(",");
                                    valuesBuffer.append(",");
                                }
                                prepareBuffer.append(meta.getColumnName(i + 1));
                                valuesBuffer.append("?");
                            }
                            prepareBuffer.append(")" + valuesBuffer.toString() + ")");
                            try {
                                pstmt = con.prepareStatement(prepareBuffer.toString());
                                loadFileReader = new BufferedReader(new FileReader(fName));
                                while ((loadString = loadFileReader.readLine()) != null) {
                                    if (loadString.toUpperCase().equals("ENDOFDATA"))
                                        break;
                                    columnIndex = 0;
                                    valueIndex = 0;
                                    ft = new FieldTokenizer(loadString, '|', true);
                                    while (ft.hasMoreFields()) {
                                        fieldString = ft.nextField();
                                        if (fieldString.equals("|")) {
                                            columnIndex++;
                                            if (columnIndex > valueIndex) {
                                                pstmt.setString(valueIndex + 1, (String) null);
                                                valueIndex++;
                                            }
                                        } else if (columnIndex < rsColCount) {
                                            pstmt.setString(valueIndex + 1, fieldString);
                                            valueIndex++;
                                        }
                                    }
                                    pstmt.executeUpdate();
                                }
                                pstmt.close();
                            } catch (Exception loadEx) {
                                System.out.println(loadEx.getMessage());
                                out.println(loadEx.getMessage());
                            }
                        } else if (cmdString.toUpperCase().startsWith("SETSTRING") |
                                cmdString.toUpperCase().startsWith("SETINT")) {
                            b1 = cmdString.indexOf(" ");
                            b2 = cmdString.lastIndexOf(" ");
                            if (b2 > b1 & b1 > 0) {
                                parameterIndex = Integer.parseInt(cmdString.substring(b1 + 1, b2));
                                parameterString = cmdString.substring(b2 + 1);
                                if (tinySQLGlobals.DEBUG) System.out.println("Set parameter["
                                        + parameterIndex + "]=" + parameterString);
                                if (cmdString.toUpperCase().startsWith("SETINT")) {
                                    parameterInt = Integer.parseInt(parameterString);
                                    pstmt.setInt(parameterIndex, parameterInt);
                                } else {
                                    pstmt.setString(parameterIndex, parameterString);
                                }
                                if (parameterIndex == 2)
                                    pstmt.executeUpdate();
                            }
                        } else {
                            System.out.println("hhhhhhh");
                            if (cmdString.indexOf("?") > -1) {
                                pstmt = con.prepareStatement(cmdString);
                            } else {
                                try {
                                    stmt.executeUpdate(cmdString);
                                    System.out.println("DONE\n");
                                    out.println("DONE");
                                } catch (Exception upex) {
                                    System.out.println(upex.getMessage());
                                    out.println(upex.getMessage());
                                    if (tinySQLGlobals.DEBUG) upex.printStackTrace();
                                }
                            }
                        }
                    }
                   // if (args.length > 1) cmdString = "EXIT";
                } catch (SQLException te) {
                    System.out.println("iiiiiii");
                    System.out.println(te.getMessage());
                    out.println(te.getMessage());
                    if (tinySQLGlobals.DEBUG) te.printStackTrace(System.out);
                    inputString = (String) null;
                } catch (Exception e) {
                    System.out.println("lllllllll");
                    System.out.println(e.getMessage());
                    out.println(e.getMessage());
                    cmdString = "EXIT";
                    break;
                }
            }

*/




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
