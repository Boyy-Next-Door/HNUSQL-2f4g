package com.sqlmagic.tinysql;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class Logger extends tinySQL {

    private String dataDir = null;

    Logger() {
    }

    Logger(String url) {
        dataDir = url;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
/*static {
        dataDir = tinySQLGlobals.connectDir;
    }*/

    /**
     * 记录DCL和DDL语句
     *
     * @param sqlStatement
     */

    public void logStatement(String sqlStatement) {

        //System.out.println("statement is " + sqlStatement);
        /*if (tinySQLGlobals.connectDir != null && !tinySQLGlobals.connectDir.equals(dataDir))
            dataDir = tinySQLGlobals.connectDir;*/
        //System.out.println(dataDir);

        ByteArrayInputStream byteStream = new ByteArrayInputStream(sqlStatement.getBytes());
        tinySQLParser tsp = null;

        try {
            tsp = new tinySQLParser((InputStream) byteStream, this);
        } catch (tinySQLException e) {
            e.printStackTrace();
        }
        //System.out.println(tsp.statementType);


        if (needToLog(tsp.statementType)) {
            //System.out.println(tsp.statementType + " what");
            logIt(sqlStatement);
        }
    }

    /**
     * 具体的记录操作
     *
     * @param sqlStatement
     */
    private void logIt(String sqlStatement) {
        File toOpen = loadFile();


        //若log文件夹不存在，创建
        if (!toOpen.getParentFile().exists()) {
            try {
                toOpen.getParentFile().mkdirs();
                toOpen.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!toOpen.exists()) {
            try {
                toOpen.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (OutputStream fos = new FileOutputStream(toOpen, true)) {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String time = df.format(date);
            String newLine = System.getProperty("line.separator");

            fos.write(time.getBytes());
            fos.write("====".getBytes());
            fos.write(sqlStatement.getBytes());
            fos.write(newLine.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



   /* public void logDCL(String sqlStatement) {

    }*/

    /**
     * 是否需要记录日志
     *
     * @param statementType
     * @return
     */
    private boolean needToLog(String statementType) {

        String[] tolog = {"UPDATE", "DELETE", "INSERT", "CREATE_TABLE",
                "ALTER_ADD", "ALTER_DROP", "ALTER_RENAME", "DROP_TABLE",
                "GRANT", "REVOKE"};

        for (int i = 0; i < tolog.length; i++) {
            if (tolog[i].equals(statementType))
                return true;
        }
        return false;
    }

    /**
     * 读取并打印日志方法，根据日志行数读取，-1读取最新
     *
     * @param linenum
     */
    public void readLog(int linenum) {
        if (linenum < -1 || linenum == 0) {
            System.out.println("linenum invalid (-1 OR >0)");
        }
        File toOpen = loadFile();
        String line = null;
        if (toOpen.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(toOpen),
                    "UTF-8"))) {
                if (linenum == -1) {
                    String templine = "";
                    while (true) {
                        if ((templine = br.readLine()) == null) break;
                        line = templine;
                    }
                }
                while (linenum > 0) {
                    line = br.readLine();
                    if (line == null) {
                        System.out.println("line number is out of bound");
                        return;
                    }
                    linenum--;
                }

                String[] part = line.split("====");
                System.out.println("LogInfo:");
                System.out.println("Time: " + part[0]);
                System.out.println("SqlStatement: " + part[1]);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("log file does not exists");
        }
    }

    public void readLog(String date) {
        System.out.println("没写");
    }

    public void recoverDatabase(int linenum, Connection con) {
        if (linenum < 0) {
            System.out.println("linenum invalid (>0)");
        }
        File toOpen = loadFile();
        String line = null;
        if (toOpen.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(toOpen),
                    "UTF-8"))) {

                while (linenum > 0) {
                    line = br.readLine();
                    if (line == null) {
                        System.out.println("line num is out of bound");
                        return;
                    }
                    linenum--;
                }

                int count = 0;
                Statement stmt = con.createStatement();
                while (true) {
                    String sql = line.substring(20, line.length());
                    System.out.println("SqlStatement: " + sql);
                    stmt.executeUpdate(sql);
                    count++;
                    line = br.readLine();
                    if (line == null) {
                        System.out.println("Recover Done, " + count + " statement executed");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("log file does not exists");
        }
    }

    /**
     * 全量备份 饿啊
     * @throws IOException
     */
    public void backupFull() throws IOException {
        File Dir;
        if (dataDir == null)
            Dir = new File("." + File.separator);
        else
            Dir = new File(dataDir + File.separator);

        File[] files = Dir.listFiles();
        for(File f : files) {
            if (f.getName().endsWith(".DBF")) {
                backupTable(f.getName());
            }
        }
    }

    public void recoverFull() throws IOException {
        File backupDir = loadBackupDir();
        if(!backupDir.exists()) {
            System.out.println("backup dose not exists");
            return;
        }
        File[] files = backupDir.listFiles();
        for(File f : files) {
            if(f.getName().endsWith(".DBF")) {
                recoverTable(f.getName(),f);
            }
        }

    }

    private void backupTable(String tableName) throws IOException {
        File tableFile;
        if (dataDir == null)
            tableFile = new File('.' + File.separator + tableName);
        else
            tableFile = new File(dataDir + File.separator + tableName);
        if(!tableFile.exists()) {
            System.out.println("this table dose not exists!!!");
            return;
        }

        File backupDir = loadBackupDir();
        if(!backupDir.exists())
            backupDir.mkdir();
        File backupFile = new File(backupDir.getPath() + File.separator + tableName);

        if(backupFile.exists())
            backupFile.delete();

        Files.copy(tableFile.toPath(),backupFile.toPath());

    }

    private void recoverTable(String tableName,File src) throws IOException {
        File Dir;
        if (dataDir == null)
            Dir = new File("." + File.separator);
        else
            Dir = new File(dataDir + File.separator);

        File tar = new File(Dir + File.separator + tableName);

        Files.copy(src.toPath(),tar.toPath());
    }



    //获取日志路径
    private File loadFile() {
        File toOpen;
        //若没有连接数据库，默认为项目目录下
        if (dataDir == null)
            toOpen = new File('.' + File.separator + "log" + File.separator + "log.txt");
        else
            toOpen = new File(dataDir + File.separator +
                    "log" + File.separator + "log.txt");
        return toOpen;
    }

    private File loadBackupDir() {
        File toOpen;
        //若没有连接数据库，默认为项目目录下
        if (dataDir == null)
            toOpen = new File('.' + File.separator + "backup" + File.separator);
        else
            toOpen = new File(dataDir + File.separator + "backup" + File.separator);
        return toOpen;
    }



    @Override
    void CreateTable(String tableName, Vector v) throws IOException, tinySQLException {

    }

    @Override
    void AlterTableAddCol(String tableName, Vector v) throws IOException, tinySQLException {

    }

    @Override
    void AlterTableDropCol(String tableName, Vector v) throws IOException, tinySQLException {

    }

    @Override
    void AlterTableRenameCol(String tableName, String oldColumnName, String newColumnName) throws tinySQLException {

    }

    @Override
    void DropTable(String tableName) throws tinySQLException {

    }

    @Override
    tinySQLTable getTable(String tableName) throws tinySQLException {
        return null;
    }

}
