package com.sqlmagic.tinysql;

import client.Info;
import usersystem.Admin;
import usersystem.UserTree;

import java.io.*;
import java.net.Socket;
import java.sql.*;
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


            cmdString=in.readLine().trim();
            System.out.println(cmdString);
            out.println("success");

            cmdString = "NULL";
            stmt = con.createStatement();
            inputString = (String) null;

            while (!cmdString.toUpperCase().equals("EXIT")) {
              //  System.out.println("hahaha");
                try {
                    if (startReader != (BufferedReader) null) {
                        /*
                         *             Command START files can contain comments and can have
                         *             commands broken over several lines.  However, they
                         *             cannot have partial commands on a line.
                         */
                        inputBuffer = new StringBuffer();
                        inputString = (String) null;
                        while ((readString = startReader.readLine()) != null) {
                            if (readString.startsWith("--") |
                                    readString.startsWith("#")) continue;
                            inputBuffer.append(readString + " ");
                            /*
                             *                A field tokenizer must be used to avoid problems with
                             *                semi-colons inside quoted strings.
                             */
                            ft = new FieldTokenizer(inputBuffer.toString(), ';', true);
                            if (ft.countFields() > 1) {
                                inputString = inputBuffer.toString();
                                break;
                            }
                        }
                        if (inputString == (String) null) {
                            startReader = (BufferedReader) null;
                            continue;
                        }
                    }
              /*
                    else if (args.length == 0) {
                        System.out.print("tinySQL>");
                        inputString = stdin.readLine().trim();
                    }
                */
                    else{
                        System.out.print("tinySQL>");
                        //inputString = in.readLine().trim();
                        //System.out.println((Info) obin.readObject());
                        Info info=(Info) obin.readObject();
                        inputString=info.getCommand().trim();

                    }

                    if (inputString == (String) null) break;

                    if (inputString.toUpperCase().startsWith("EXIT") |
                            inputString.toUpperCase().startsWith("QUIT")) break;

                    startAt = 0;
                    while (startAt < inputString.length() - 1) {//输出的命令必须大于1。否则忽略不计
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
                            /*
                             *                The actual number of columns retrieved has to be checked
                             */
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
                            /*
                             *                Support for SET DEBUG ON/OFF and SET ECHO ON/OFF
                             */
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
                            /*
                             *                Spool output to a file.
                             */
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
                            /*
                             *                Set up the PreparedStatement for the inserts
                             */
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
                    System.out.println(te.getMessage());
                    out.println(te.getMessage());
                    if (tinySQLGlobals.DEBUG) te.printStackTrace(System.out);
                    inputString = (String) null;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    out.println(e.getMessage());
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

    static int displayResults(ResultSet rs) throws SQLException
    {
        if (rs == null)
        {
            System.err.println("ERROR in displayResult(): No data in ResultSet");
            out.println("ERROR in displayResult(): No data in ResultSet");
            return 0;
        }
        Date testDate;
        int numCols = 0,nameLength;
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        int[] width = new int[cols];
        String dashes = "=============================================";
        /*
         *    Display column headers
         */
        boolean first=true;
        StringBuffer head = new StringBuffer();
        StringBuffer line = new StringBuffer();
        /*
         *    Fetch each row
         */
        while (rs.next())
        {
            /*
             *       Get the column, and see if it matches our expectations
             */
            String text = new String();
            for (int ii=0; ii<cols; ii++)
            {
                String value = rs.getString(ii+1);

                if (first)
                {
                    width[ii] = meta.getColumnDisplaySize(ii+1);

                    if ( tinySQLGlobals.DEBUG &
                            meta.getColumnType(ii+1) == Types.DATE )
                    {
                        testDate = rs.getDate(ii+1);
                        System.out.println("Value " + value + ", Date "
                                + testDate.toString());
                        out.println("Value " + value + ", Date "
                                + testDate.toString());
                    }

                    nameLength = meta.getColumnName(ii+1).length();
                    if ( nameLength > width[ii] ) width[ii] = nameLength;
                    head.append(padString(meta.getColumnName(ii+1), width[ii]));
                    head.append(" ");
                    line.append(padString(dashes+dashes,width[ii]));
                    line.append(" ");
                }
                text += padString(value, width[ii]);
                text += " ";   // the gap between the columns
            }
            try
            {
                if (first)
                {
                    if ( spoolFileWriter != (FileWriter)null )
                    {
                        spoolFileWriter.write(head.toString() + newLine);
                        spoolFileWriter.write(head.toString() + newLine);
                    } else {
                        System.out.println(head.toString());
                        out.println(head.toString());
                        System.out.println(line.toString());
                        out.println(line.toString());
                    }
                    first = false;
                }
                if ( spoolFileWriter != (FileWriter)null )
                    spoolFileWriter.write(text + newLine);
                else {
                    System.out.println(text);
                    out.println(text);
                }
                numCols++;
            } catch ( Exception writeEx ) {
                System.out.println("Exception writing to spool file "
                        + writeEx.getMessage());
                out.println("Exception writing to spool file "
                        + writeEx.getMessage());
            }
        }
        return numCols;
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
