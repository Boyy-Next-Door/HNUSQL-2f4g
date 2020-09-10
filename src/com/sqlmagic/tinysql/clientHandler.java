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
import com.sqlmagic.tinysql.utils.MyTableUtil;
import usersystem2.UserManager2;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

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
                    //��ȡ�ͻ��˷���������
                    inputString = in.readLine().trim();

                    System.out.println(inputString);
                    //�մ������
                    if (inputString == (String) null || inputString.isEmpty()) continue;
                    //��������
                    String clientCookie;        //�û����cookie
                    int requestType;            //����ӿ�����
                    String rawSQL;              //ԭʼSQL���
                    JSONObject obj = JSON.parseObject(inputString);
                    clientCookie = obj.getString("cookie");
                    //����Я����cookie
                    if (clientCookie != null && !clientCookie.isEmpty()) {
                        //TODO У��cookie
                        String temp = CryptoUtil.decodeTarget(clientCookie);
                        if (Pattern.matches("].*}", temp) == true) {
                            //���У��ͨ��
                            //System.out.println("pass");
                            cookie = clientCookie;
                            String s = CryptoUtil.decodeTarget(cookie);
                            username = s.substring(1,s.length()-1);
                        }
                        //���У��û��ͨ�� ˵�������û���ݷǷ�
                        else out.println(JSON.toJSONString(BaseResponse.fail("Login status error.")));

                    } else {
                        System.out.println("cookie is null.");
                        //û��Я��cookie ���Ҫ���վ��幦�ܽӿ�������
                    }
                    requestType = obj.getInteger("requestType");
                    rawSQL = obj.getString("rawSQL");
                    inputString = rawSQL;
                    startAt = 0;

                    while (startAt < inputString.length() - 1) {
                        endAt = inputString.indexOf(";", startAt);
                        //�������ڴ������ԷֺŽ�β�Ķ������  ʵ�������ǲ������������� һ�η��͵�ָ��ָ�Ӱ���һ���������
                        if (endAt == -1)                                //û����;��β  ��Ϊ�ַ�����ĩβ����ָ��Ľ�β
                            endAt = inputString.length();
                        cmdString = inputString.substring(startAt, endAt);
                        startAt = endAt + 1;

                        if (requestType == Request.REGISTER) {     /*ע��*/
                            username = obj.getString("username");
                            password = obj.getString("password");
                            //У�����
                            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                                //��������ȷ
                                out.println(JSON.toJSONString(BaseResponse.fail("Parameter error.")));
                                continue;
                            }
                            //���û�������������ж�
                            boolean isSuccess = UserManager2.register(username, password);

                            if (isSuccess) {
                                //���ظ��ͻ���
                                out.println(JSON.toJSONString(BaseResponse.ok("ok", null)));
                            } else {
                                out.println(JSON.toJSONString(BaseResponse.fail("Register failed.")));
                            }
                        } else if (requestType == Request.LOGIN) {     /*��½*/
                            //��ȡ�ӿͻ��˷��������û���������
                            username = obj.getString("username");
                            password = obj.getString("password");
                            //У�����
                            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                                //��������ȷ
                                out.println(JSON.toJSONString(BaseResponse.fail("Parameter error.")));
                                continue;
                            }
                            //���û�������������ж�
                            boolean isSuccess = UserManager2.login(username, password);
                            //isSuccess = true;


                            //��½�ɹ� ����cookie
                            if (isSuccess) {
                                //����cookie
                                cookie = CryptoUtil.encodeSrc("]" + username + "}");
                                //System.out.println(cookie);
                                //���ظ��ͻ���
                                out.println(JSON.toJSONString(BaseResponse.ok("ok", cookie)));
                            } else {
                                out.println(JSON.toJSONString(BaseResponse.fail("Username or password error.")));
                            }

                        } else if (requestType == Request.USE_DATABASE) { /*ѡ�����ݿ�*/
                            String databaseName = cmdString.substring(4, cmdString.indexOf(";")==-1?cmdString.length():cmdString.indexOf(";"));
                            String url = DatabaseMapper.getURL(databaseName);
                            //���ݿⲻ����
                            if (url.equals("DB_NOT_EXIST")) {
                                out.println(JSON.toJSONString(BaseResponse.fail("Database doesn't exist.")));
                            } else {
                                fName = databaseName;
                                con = dbConnect(url);
                                stmt = con.createStatement();
                            }

                            //���ɹ� Ϊlogger����url �����ؽ��
                            if (!"DB_NOT_EXIST".equals(url) && con != null) {
                                logger.setDataDir(url);
                               //���ؽ�����ͻ���
                                out.println(JSON.toJSONString(BaseResponse.ok("ok", null)));
                            }
                        } else if (requestType == Request.SHOW_DATABASES || requestType == Request.SHOW_TABLES||requestType == Request.DESCRIBE_TABLE) { /*show�����*/
                            show.whichShow(con, out, requestType,cmdString);
                        } else if (requestType == Request.SELECT || requestType == Request.INSERT
                                || requestType == Request.UPDATE || requestType == Request.DELETE) { /*��ɾ�Ĳ�*/
                            // ���ȶ�rowSQL���дʷ����� ��Ҫ����cookie�����õ����û����  ���۸��û��Ƿ���Ȩ��ִ���������
                            PreParser preParser = new PreParser();
                            boolean isQualified = preParser.verifyPermission(cmdString, username, fName, con).getStatus() == 0 ? true : false;
//                            boolean isQualified = true;
                            if (isQualified) {
                                //�����Ȩִ�� ���ڲ����ؽ��
                                // System.out.println("qualified");
                                dqlDml.SelectInsertUpdateDelete(con, stmt, requestType, out, cmdString,logger);
                            } else {
                                //��Ȩִ��
                                // System.out.println("unqualified");
                                out.println(JSON.toJSONString(BaseResponse.fail("Operation denied.")));
                            }


                        } else if (requestType == Request.CREATE || requestType == Request.ALTER
                                || requestType == Request.DROP || requestType == Request.GRANT
                                || requestType == Request.REVOKE) { /*���ݶ������ԡ����ݿ�������*/

                            //���ȶ�rowSQL���дʷ����� ��Ҫ����cookie�����õ����û����  ���۸��û��Ƿ���Ȩ��ִ���������
                            PreParser preParser = new PreParser();

                            boolean isQualified = preParser.verifyPermission(cmdString, username, fName, con).getStatus() == 0 ? true : false;
//                            boolean isQualified = true;
                            if (isQualified) {
                                //�����Ȩִ�� ���ڲ����ؽ��
                                ddlDcl.ddlAndDcl(fName,con, stmt, username, requestType, out, cmdString,logger);
                            } else {
                                //��Ȩִ��
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
        fileList = conPath.listFiles();//����ĳ��Ŀ¼�������ļ���Ŀ¼�ľ���·��
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
