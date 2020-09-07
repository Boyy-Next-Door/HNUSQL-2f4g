package com.sqlmagic.tinysql;

import java.io.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Map;

//将数据库名映射到文件directory
public class DatabaseMapper {
    private static final String MAPPER_FILE_URL = "src/static_resourse/database_mapper";
    private static final String DBF_LOCATION = "src/static_resourse/dbf_file";

    public static boolean createDatabase(String databaseName) {
        //首先获取当前已有数据库 比较是否重名
        ArrayList<MapperEntry> databaseList = getDatabaseList();
        boolean isAvailable = true;
        for (MapperEntry entry : databaseList) {
            if (entry.databaseName.equals(databaseName)) {
                isAvailable = false;
                break;
            }
        }
        //可以创建
        if (isAvailable) {
            //在dbf_file中创建一个新的文件夹 并作为该数据库的url
            File file = new File(DBF_LOCATION + "\\" + databaseName);
            file.mkdir();

            //在mapper表中存储该数据库隐射
            MapperEntry entry = new MapperEntry(databaseName, file.getPath());
            addToMapper(entry);
            return true;
        }
        return false;
    }

    private static void addToMapper(MapperEntry entry) {
        BufferedWriter bfw = null;
        try {
            bfw = new BufferedWriter(new FileWriter(new File(MAPPER_FILE_URL),true));
            bfw.write(entry.databaseName + "=" + entry.getDatabaseDir());
            bfw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bfw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean dropDatabase(String databaseName) {
        return false;
    }


    public static ArrayList<MapperEntry> getDatabaseList() {
        BufferedReader bfr = null;
        ArrayList<MapperEntry> list = new ArrayList<>();
        try {
            bfr = new BufferedReader(new FileReader(new File(MAPPER_FILE_URL)));
            while (bfr.ready()) {
                String[] split = bfr.readLine().split("=");
                if(split.length!=2) continue;
                list.add(new MapperEntry(split[0], split[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public static String getURL(String databaseName) {
        //获取现有db列表 查找是否存在此表
        ArrayList<MapperEntry> databaseList = getDatabaseList();
        for(MapperEntry entry:databaseList){
            if(entry.getDatabaseName().equals(databaseName)){
                return entry.getDatabaseDir();
            }
        }
        return "DB_NOT_EXIST";
    }

    public static class MapperEntry {
        private String databaseName;
        private String databaseDir;

        public MapperEntry(String databaseName, String databaseDir) {
            this.databaseName = databaseName;
            this.databaseDir = databaseDir;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public void setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
        }

        public String getDatabaseDir() {
            return databaseDir;
        }

        public void setDatabaseDir(String databaseDir) {
            this.databaseDir = databaseDir;
        }

        @Override
        public String toString() {
            return "MapperEntry{" +
                    "databaseName='" + databaseName + '\'' +
                    ", databaseDir='" + databaseDir + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        createDatabase("StudentDB");
    }
}
