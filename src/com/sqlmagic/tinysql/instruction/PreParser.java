package com.sqlmagic.tinysql.instruction;

import com.sqlmagic.tinysql.FieldTokenizer;
import com.sqlmagic.tinysql.tinySQL;
import com.sqlmagic.tinysql.tinySQLWhere;
import usersystem2.*;

import java.sql.Types;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class PreParser {
    String operation;
    String tableName;
    byte perm = 0;

    public boolean verifyPermission(String inputString, String nowUsername, String databaseName) {
        setParser(inputString);
        boolean isQualified = false;
        User user = UserManager2.getUserByName(nowUsername);
        if (user == null) {
            //用户不存在
            return false;
        }
        for (Map.Entry<Table, Permission> entry : user.getPermissions().entrySet()) {
            Table key = entry.getKey();
            if (key.getDb().getDatabaseName().equals(databaseName) && key.getTableName().equals(tableName)) {
                Permission permission = user.getPermissions().get(key);

                byte perm1 = permission.getPermission();
                if ((((byte) perm) & perm1) != 0) {
                    isQualified = true;
                    break;
                }
            }
        }
        return isQualified;
    }

    public void setParser(String inputString) {
        String nextField;
        FieldTokenizer ft = new FieldTokenizer(inputString, ' ', false);
        nextField = ft.nextField();
        if (nextField.toUpperCase().equals("CREATE")) {
            operation = "CREATE";
            while (ft.hasMoreFields()) {
                nextField = ft.nextField();
                if (nextField.toUpperCase().equals("TABLE")) {
                    tableName = ft.nextField();
                    break;
                }
            }
            perm = (byte) 0b10000000;
        } else if (nextField.toUpperCase().equals("DELETE")) {
            operation = "DELETE";
            while (ft.hasMoreFields()) {
                nextField = ft.nextField();
                if (nextField.toUpperCase().equals("FROM")) {
                    tableName = ft.nextField();
                    break;
                }
            }
            perm = (byte) 0b01000000;
        } else if (nextField.toUpperCase().equals("UPDATE")) {
            operation = "UPDATE";
            tableName = ft.nextField();
            perm = (byte) 0b00100000;
        } else if (nextField.toUpperCase().equals("SELECT")) {
            operation = "SELECT";
            while (ft.hasMoreFields()) {
                nextField = ft.nextField();
                if (nextField.toUpperCase().equals("FROM")) {
                    tableName = ft.nextField();
                    break;
                }
            }
            perm = (byte) 0b00010000;
        }
    }

}