/*
 * Java program to execute SQL commands using the tinySQL JDBC driver.
 *
 * $Author: davis $
 * $Date: 2004/12/18 21:26:02 $
 * $Revision: 1.1 $
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 * Revision History:
 *
 * Written by Davis Swan in November, 2003.
 */

package com.sqlmagic.tinysql;

import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;
public class tinySQLCmd
{
   static Vector tableList;
   /*
   动态数组，Vector 主要用在事先不知道数组的大小，或者只是需要一个可以改变大小的数组的情况。
    */
   static String dbVersion;
   static FileWriter spoolFileWriter = (FileWriter)null;
   /*
   FileWriter 类从 OutputStreamWriter 类继承而来。该类按字符向流中写入数据。
    */
   static String newLine = System.getProperty("line.separator");
   /*
   windows环境下的文本文件换行符:\r\n
   linux/unix环境下的文本文件换行符:\r
   Mac环境下的文本文件换行符:\n
   如果要实现程序跨平台运行，则必须使用 System.lineSeparator()进行换行。
    */
   public static void main(String[] args) throws IOException,SQLException
   {
      DatabaseMetaData dbMeta;
      /*
      DatabaseMetaData类是java.sql包中的类，利用它可以获取我们连接到的数据库的结构、存储等很多信息。
      https://blog.csdn.net/anxinliu2011/article/details/7560511
       */
      ResultSetMetaData meta;
      /*
      https://www.cnblogs.com/xtdxs/p/7114927.html
       */
      ResultSet display_rs,typesRS;
      /*
      ResultSet，数据库结果集的数据表，通常通过执行查询数据库的语句生成。
      https://baike.baidu.com/item/ResultSet/10061608
       */
      BufferedReader stdin,loadFileReader;
      /*
      BufferedReader类从字符输入流中读取文本并缓冲字符，以便有效地读取字符，数组和行
      可以通过构造函数指定缓冲区大小也可以使用默认大小。对于大多数用途，默认值足够大
      https://blog.csdn.net/ai_bao_zi/article/details/81134801
       */
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

      //这里包装了标准输入流，在命令行输入的内容就从这个stdin里读出来
      stdin = new BufferedReader(new InputStreamReader(System.in));
      try 
      {
/*
 *       Register the JDBC driver for dBase 加载驱动
 */
         Class.forName("com.sqlmagic.tinysql.dbfFileDriver");
      } catch (ClassNotFoundException e) {
         System.err.println(
              "JDBC Driver could not be registered!!\n");
         if ( tinySQLGlobals.DEBUG ) e.printStackTrace();
      }
      //默认db文件夹路径为 ‘.’ 即当前文件夹
      fName = ".";
      //.jar文件可以带参运行 第一个参数就是指定的db文件夹路径
      if ( args.length > 0 ) fName = args[0];
/* 
 *    Establish a connection to dBase 创建到db的连接
 */
      con = dbConnect(fName);
      //如果指定的路径不是一个有效的文件夹 那么会以当前文件夹为目标重新连接
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
      cmdString = "NULL";
      stmt = con.createStatement();
      inputString = (String)null;
      //带参运行的第二个参数是第一条指令语句
      if ( args.length > 1 ) inputString = args[1].trim();
      while ( !cmdString.toUpperCase().equals("EXIT") )
      {
         try
         {
            if ( startReader != (BufferedReader)null )
            {
/*
 *             Command START files can contain comments and can have
 *             commands broken over several lines.  However, they 
 *             cannot have partial commands on a line.
 */
               inputBuffer = new StringBuffer();
               inputString = (String)null;
               while ( ( readString = startReader.readLine() ) != null )
               {
                  if ( readString.startsWith("--") |
                       readString.startsWith("#") ) continue;
                  inputBuffer.append(readString + " ");
/*
 *                A field tokenizer must be used to avoid problems with
 *                semi-colons inside quoted strings.
 */
                  ft = new FieldTokenizer(inputBuffer.toString(),';',true);
                  if ( ft.countFields() > 1 )
                  {
                     inputString = inputBuffer.toString();
                     break;
                  }
               }
               if ( inputString == (String)null ) 
               {
                  startReader = (BufferedReader)null;
                  continue;
               }

             //如果没有带参执行 命令行会输出tinySQL> 然后等待输入流输入一行指令
            } else if ( args.length == 0 ) {
               System.out.print("tinySQL>");
               inputString = stdin.readLine().trim();
            }
            //读到了终止符 字符串是null 那么和exit quit效果一样 都是结束程序
            if ( inputString == (String)null ) break;
            if (inputString.toUpperCase().startsWith("EXIT") |
                inputString.toUpperCase().startsWith("QUIT") ) break;
            startAt = 0;
            while ( startAt < inputString.length() - 1 )
            {
               //寻找本条指令的结束符‘;’
               endAt = inputString.indexOf(";",startAt);
               //本行不是完整的指令
               if ( endAt == -1 )   //那么结束位置就是最后一个字符
                  endAt = inputString.length();
               //①本行带有; 以此左边作为本条指令 ②本行不含; 以整行作为本条指令  （这里应该是不支持像mysql一样的指令换行）
               cmdString = inputString.substring(startAt,endAt);
               if ( echo ) System.out.println(cmdString);
               startAt = endAt + 1;
               if ( cmdString.toUpperCase().startsWith("SELECT") ) 
               {
                  display_rs = stmt.executeQuery(cmdString);
                  if ( display_rs == (ResultSet)null )
                  {
                     System.out.println("Null ResultSet returned from query");
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
                  for ( i = 0; i < rsColCount; i++ )
                  {
                     columnNames[i] = meta.getColumnName(i + 1);
                     columnWidths[i] = meta.getColumnDisplaySize(i + 1);
                     columnTypes[i] = meta.getColumnType(i + 1);
                     columnScales[i] = meta.getScale(i + 1);
                     columnPrecisions[i] = meta.getPrecision(i + 1);
                     if ( columnNames[i].length() > columnWidths[i] )
                        columnWidths[i] = columnNames[i].length(); 
                     lineOut.append(padString(columnNames[i],columnWidths[i]) + " ");
                  }
                  if ( tinySQLGlobals.DEBUG )
                     System.out.println(lineOut.toString());
                  displayResults(display_rs);
               }
               // CONNECT指令  连接到指定数据库（绑定文件夹）
               else if ( cmdString.toUpperCase().startsWith("CONNECT") ) {
                  con = dbConnect(cmdString.substring(8,cmdString.length()));
               }
               //HELP指令  打印help信息
               else if ( cmdString.toUpperCase().startsWith("HELP") ) {
                  helpMsg(cmdString);
               }
               //DESCRIBE指令 描述当前db中的某一张表
               else if ( cmdString.toUpperCase().startsWith("DESCRIBE") ) {
                  dbMeta = con.getMetaData();
                  tableName = cmdString.toUpperCase().substring(9);
                  display_rs = dbMeta.getColumns(null,null,tableName,null);
                  System.out.println("\nColumns for table " + tableName + "\n"
                  + "Name                            Type");               
                  while (display_rs.next())
                  {
                     lineOut = new StringBuffer(100);
                     lineOut.append(padString(display_rs.getString(4),32));
                     colTypeName = display_rs.getString(6);
                     colType = display_rs.getInt(5);
                     colWidth = display_rs.getInt(7);
                     colScale = display_rs.getInt(9);
                     colPrecision = display_rs.getInt(10);
                     if ( colTypeName.equals("CHAR") )
                     {
                        colTypeName = colTypeName + "(" 
                        + Integer.toString(colWidth) + ")";
                     } else if ( colTypeName.equals("FLOAT") ) {
                        colTypeName += "("+ Integer.toString(colPrecision)
                        + "," + Integer.toString(colScale) + ")";
                     }  
                     lineOut.append(padString(colTypeName,20) + padString(colType,12));
                     System.out.println(lineOut.toString());
                  }
               }
               //SHOW TABLES指令
               else if ( cmdString.toUpperCase().equals("SHOW TABLES") ) {
                  for ( i = 0; i < tableList.size(); i++ )
                     System.out.println((String)tableList.elementAt(i));
               }
               //SHOW TYPES指令
               else if ( cmdString.toUpperCase().equals("SHOW TYPES") ) {
                  typesRS = dbMeta.getTypeInfo();
                  typeCount = displayResults(typesRS);
               }
               //SET指令 这应该是tinySQL自带的一些辅助功能的开关设置
               else if ( cmdString.toUpperCase().startsWith("SET ") ) {
/*
 *                Support for SET DEBUG ON/OFF and SET ECHO ON/OFF
 */
                  ft = new FieldTokenizer(cmdString.toUpperCase(),' ',false);
                  fields = ft.getFields();
                  if ( fields[1].equals("ECHO") )
                  { 
                     if ( fields[2].equals("ON") ) echo = true;
                     else echo = false;
                  } else if ( fields[1].equals("DEBUG") ) {
                     if ( fields[2].equals("ON") ) tinySQLGlobals.DEBUG = true;
                     else tinySQLGlobals.DEBUG = false;
                  } else if ( fields[1].equals("PARSER_DEBUG") ) {
                     if ( fields[2].equals("ON") ) 
                        tinySQLGlobals.PARSER_DEBUG = true;
                     else tinySQLGlobals.PARSER_DEBUG = false;
                  } else if ( fields[1].equals("WHERE_DEBUG") ) {
                     if ( fields[2].equals("ON") ) 
                        tinySQLGlobals.WHERE_DEBUG = true;
                     else tinySQLGlobals.WHERE_DEBUG = false;
                  } else if ( fields[1].equals("EX_DEBUG") ) {
                     if ( fields[2].equals("ON") ) 
                        tinySQLGlobals.EX_DEBUG = true;
                     else tinySQLGlobals.EX_DEBUG = false;
                  }
               }
               //SPOOL指令 输出重定向到文件
               else if ( cmdString.toUpperCase().startsWith("SPOOL ") ) {
/*
 *                Spool output to a file.
 */
                  ft = new FieldTokenizer(cmdString,' ',false);
                  fName = ft.getField(1);
                  if ( fName.equals("OFF") )
                  {
                     try
                     {
                        spoolFileWriter.close();
                     }  catch (Exception spoolEx ) {
                        System.out.println("Unable to close spool file " 
                        + spoolEx.getMessage() + newLine);
                     }
                  } else { 
                     try
                     {
                        spoolFileWriter = new FileWriter(fName);
                        if ( spoolFileWriter != (FileWriter)null) 
                           System.out.println("Output spooled to " + fName);
                     }  catch (Exception spoolEx ) {
                        System.out.println("Unable to spool to file " 
                        + spoolEx.getMessage() + newLine);
                     } 
                  }
               }
               //START指令 读取sql文件
               else if ( cmdString.toUpperCase().startsWith("START ") ) {
                  ft = new FieldTokenizer(cmdString,' ',false);
                  fName = ft.getField(1);
                  if ( !fName.toUpperCase().endsWith(".SQL") ) fName += ".SQL";
                  try
                  {
                     startReader = new BufferedReader(new FileReader(fName));
                  } catch ( Exception ex ) {
                     startReader = (BufferedReader)null;
                     throw new tinySQLException("No such file: " + fName);
                  }
               }
               //LOAD指令
               else if ( cmdString.toUpperCase().startsWith("LOAD") ) {
                  ft = new FieldTokenizer(cmdString,' ',false);
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
                  for ( i = 0; i < rsColCount; i++ )
                  {
                     if ( i == 0 )
                     {
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
                  try
                  {
                     pstmt = con.prepareStatement(prepareBuffer.toString());
                     loadFileReader = new BufferedReader(new FileReader(fName));
                     while ( (loadString=loadFileReader.readLine()) != null ) 
                     {
                        if ( loadString.toUpperCase().equals("ENDOFDATA") )
                           break;
                        columnIndex = 0;
                        valueIndex = 0;
                        ft = new FieldTokenizer(loadString,'|',true);
                        while ( ft.hasMoreFields() )
                        {
                           fieldString = ft.nextField();
                           if ( fieldString.equals("|") )
                           {
                              columnIndex++;
                              if ( columnIndex > valueIndex )
                              {
                                 pstmt.setString(valueIndex+1,(String)null); 
                                 valueIndex++;
                              }
                           } else if ( columnIndex < rsColCount ) { 
                              pstmt.setString(valueIndex+1,fieldString);
                              valueIndex++;
                           }
                        }
                        pstmt.executeUpdate();
                     }
                     pstmt.close();
                  } catch (Exception loadEx) {
                     System.out.println(loadEx.getMessage());
                  }
               } else if ( cmdString.toUpperCase().startsWith("SETSTRING") |
                           cmdString.toUpperCase().startsWith("SETINT") ) {
                  b1 = cmdString.indexOf(" ");
                  b2 = cmdString.lastIndexOf(" ");
                  if ( b2 > b1 & b1 > 0 )
                  {
                     parameterIndex = Integer.parseInt(cmdString.substring(b1+1,b2));
                     parameterString = cmdString.substring(b2+1);
                     if ( tinySQLGlobals.DEBUG ) System.out.println("Set parameter["
                      + parameterIndex + "]=" + parameterString);
                     if ( cmdString.toUpperCase().startsWith("SETINT") )
                     {
                        parameterInt = Integer.parseInt(parameterString);
                        pstmt.setInt(parameterIndex,parameterInt);
                     } else {
                        pstmt.setString(parameterIndex,parameterString);
                     }
                     if ( parameterIndex == 2 ) 
                        pstmt.executeUpdate();
                  }
               } else {
                  if ( cmdString.indexOf("?") > -1 )
                  {
                     pstmt = con.prepareStatement(cmdString);
                  } else {
                     try
                     {
                        stmt.executeUpdate(cmdString);
                        System.out.println("DONE\n");
                     } catch( Exception upex ) {
                        System.out.println(upex.getMessage());
                        if ( tinySQLGlobals.DEBUG ) upex.printStackTrace();
                     }
                  }
               }
            }
            if ( args.length > 1 )  cmdString = "EXIT";
         } catch ( SQLException te ) {
              System.out.println(te.getMessage());
              if ( tinySQLGlobals.DEBUG ) te.printStackTrace(System.out);
              inputString = (String)null;
         } catch( Exception e ) {
            System.out.println(e.getMessage());
            cmdString = "EXIT";
            break;
         }
      }
      try
      {
         if ( spoolFileWriter != (FileWriter)null ) spoolFileWriter.close();
      }  catch (Exception spoolEx ) {
         System.out.println("Unable to close spool file " 
         + spoolEx.getMessage() + newLine);
      }
   }
   private static void helpMsg(String inputCmd)
   {
      String upperCmd;
      upperCmd = inputCmd.toUpperCase().trim();
      if ( upperCmd.equals("HELP") )
      {
         System.out.println("The following help topics are available:\n"
         + "=============================================================\n"
         + "HELP NEW - list of new features in tinySQL " + dbVersion + "\n"
         + "HELP COMMANDS - help for the non-SQL commands\n"
         + "HELP LIMITATIONS - limitations of tinySQL " + dbVersion + "\n"
         + "HELP ABOUT - short description of tinySQL.\n");
      } else if ( upperCmd.equals("HELP COMMANDS") ) {
         System.out.println("The following non-SQL commands are supported:\n"
         + "=============================================================\n"
         + "SHOW TABLES - lists the tinySQL tables (DBF files) in the current "
         + "directory\n"
         + "SHOW TYPES - lists column types supported by tinySQL.\n"
         + "DESCRIBE table_name - describes the columns in table table_name.\n"
         + "CONNECT directory - connects to a different directory;\n"
         + "   Examples:  CONNECT C:\\TEMP in Windows\n"
         + "              CONNECT /home/mydir/temp in Linux/Unix\n"
         + "SET DEBUG ON/OFF - turns general debugging on or off\n"
         + "SET PARSER_DEBUG ON/OFF - turns parser debugging on or off\n"
         + "SET WHERE_DEBUG ON/OFF - turns where clause debugging on or off\n"
         + "SET EX_DEBUG ON/OFF - turns exception stack printing on or off\n"
         + "SET ECHO ON/OFF - will echo input commands\n"
         + "START <filename> - executes commands in the text file\n"
         + "SPOOL <filename> - spools output of commands to the file\n"
         + "EXIT - leave the tinySQL command line interface.\n");
      } else if ( upperCmd.equals("HELP LIMITATIONS") ) {
         System.out.println("tinySQL " + dbVersion 
         + " does NOT support the following:\n"
         + "=============================================================\n"
         + "Subqueries: eg SELECT COL1 from TABLE1 where COL2 in (SELECT ...\n"
         + "IN specification within a WHERE clause.\n"
         + "GROUP BY clause in SELECT statments.\n"
         + "AS in CREATE statements; eg CREATE TABLE TAB2 AS SELECT ...\n"
         + "UPDATE statements including JOINS.\n\n"
         + "If you run into others let us know by visiting\n"
         + "http://sourceforge.net/projects/tinysql\n");
      } else if ( upperCmd.equals("HELP NEW") ) {
         System.out.println("New features in tinySQL releases.\n"
         + "=============================================================\n"
         + "Version 2.26h released March 15, 2007\n"
         + "Corrected problems with date comparisions, added support for \n"
         + "the TO_DATE function, corrected problems with DELETE.\n"
         + "Added support for IS NULL and IS NOT NULL, added ability\n"
         + "to spool output to a file.\n"
         + "---------------------------------------------------------------\n"
         + "Version 2.10 released October 22, 2006\n"
         + "Added support for long column names and fixed bugs in \n"
         + "ALTER TABLE commands.\n"
         + "---------------------------------------------------------------\n"
         + "Version 2.02 released July 20, 2005\n"
         + "Fixed more bugs with the COUNT(*) and the like comparison.\n"
         + "---------------------------------------------------------------\n"
         + "Version 2.01 released April 20, 2005\n"
         + "Fixed several bugs with the COUNT(*) and other summary functions\n"
         + "Fixed ORDER BY using columns that were not SELECTed.\n"
         + "Added support for DISTINCT keyword and TRIM function.\n"
         + "Added default sorting by selected column.\n"
         + "Significant reorganization of code to allow the use of functions\n"
         + "in WHERE clauses (now stores tsColumn objects in tinySQLWhere).\n"
         + "---------------------------------------------------------------\n"
         + "Version 2.0 released Dec. 20, 2004\n" 
         + "The package name was changed to com.sqlmagic.tinysql.\n"
         + "Support for table aliases in JOINS: see example below\n"
         + "  SELECT A.COL1,B.COL2 FROM TABLE1 A,TABLE2 B WHERE A.COL3=B.COL3\n"
         + "COUNT,MAX,MIN,SUM aggregate functions.\n"
         + "CONCAT,UPPER,SUBSTR in-line functions for strings.\n"
         + "SYSDATE - current date.\n"
         + "START script_file.sql - executes SQL commands in file.\n"
         + "Support for selection of constants: see example below:\n"
         + "  SELECT 'Full Name: ',first_name,last_name from person\n"
         + "All comparisions work properly: < > = != LIKE \n");
      } else if ( upperCmd.equals("HELP ABOUT") ) {
         System.out.println(
           "=============================================================\n"
         + "tinySQL was originally written by Brian Jepson\n"
         + "as part of the research he did while writing the book \n"
         + "Java Database Programming (John Wiley, 1996).  The database was\n"
         + "enhanced by Andreas Kraft, Thomas Morgner, Edson Alves Pereira,\n"
         + "and Marcel Ruff between 1997 and 2002.\n"
         + "The current version " + dbVersion
         + " was developed by Davis Swan starting in 2004.\n\n"
         + "tinySQL is free software; you can redistribute it and/or\n"
         + "modify it under the terms of the GNU Lesser General Public\n"
         + "License as published by the Free Software Foundation; either\n"
         + "version 2.1 of the License, or (at your option) any later version.\n"
         + "This library is distributed in the hope that it will be useful,\n"
         + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
         + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n"
         + "Lesser General Public License for more details at\n"
         + "http://www.gnu.org/licenses/lgpl.html");
      } else {
         System.out.println("Unknown help command.\n");
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

   /**
    * 创建到指定数据库的连接
    * @param tinySQLDir 指定数据库的dir路径
    * @return  返回java.sql标准下的Connection
    * @throws SQLException
    */
   private static Connection dbConnect(String tinySQLDir) throws SQLException
   {
      Connection con=null;
      DatabaseMetaData dbMeta;
      File conPath;
      File[] fileList;
      String tableName;
      ResultSet tables_rs;
      //指定的文件夹路径
      conPath = new File(tinySQLDir);
      //列出当前文件夹下的文件名
      fileList = conPath.listFiles();
      //文件夹为空
      if ( fileList == null )
      {
         System.out.println(tinySQLDir + " is not a valid directory.");
         return (Connection)null;
      } else {
         //绑定到当前文件夹 即认为连接到了指定db 返回该文件夹在系统下的绝对路径（默认的数据库fname为 ‘.’ 即在当前文件夹）
         System.out.println("Connecting to " + conPath.getAbsolutePath());
         con = DriverManager.getConnection("jdbc:dbfFile:" + conPath, "", "");
      }
      dbMeta = con.getMetaData();
      tables_rs = dbMeta.getTables(null,null,null,null);
      tableList = new Vector();
      while ( tables_rs.next() )
      {
         tableName = tables_rs.getString("TABLE_NAME");
         tableList.addElement(tableName);
      }
      if ( tableList.size() == 0 )
         System.out.println("There are no tinySQL tables in this directory.");
      else
         System.out.println("There are " + tableList.size() + " tinySQL tables"
         + " in this directory.");
      return con;
   }
  /**
  Formatted output to stdout
  @return number of tuples
  */
   static int displayResults(ResultSet rs) throws java.sql.SQLException
   {
      if (rs == null)
      {
         System.err.println("ERROR in displayResult(): No data in ResultSet");
         return 0;
      }
      java.sql.Date testDate;
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
                  System.out.println(line.toString());
               }
               first = false;
            }
            if ( spoolFileWriter != (FileWriter)null ) 
               spoolFileWriter.write(text + newLine);
            else
               System.out.println(text);
            numCols++;
         } catch ( Exception writeEx ) {
            System.out.println("Exception writing to spool file " 
            + writeEx.getMessage());
         }
      }
      return numCols;
   }
}
