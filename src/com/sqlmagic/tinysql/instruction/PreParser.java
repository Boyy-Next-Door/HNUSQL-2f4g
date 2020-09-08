package com.sqlmagic.tinysql.instruction;

import com.sqlmagic.tinysql.FieldTokenizer;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.tinySQL;
import com.sqlmagic.tinysql.tinySQLWhere;
import usersystem2.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class PreParser {
    String operation;
    String tableName;
    byte perm = 0;

    public BaseResponse verifyPermission(String inputString, String nowUsername, String databaseName,
                                         Connection con) throws SQLException {
        setParser(inputString);
        boolean isQualified = false;
        User user = UserManager2.getUserByName(nowUsername);
        if (user == null) {
            //用户不存在
            return BaseResponse.fail("user not exist");
        }
        ResultSet tables = con.getMetaData().getTables(null, null, null, null);
        while (tables.next()) {
            if(tableName.toUpperCase().equals(tables.getString("TABLE_NAME").toUpperCase())){
                isQualified = true;
            }
        }
        if(isQualified == false) return BaseResponse.fail("table not exist");
        for (Map.Entry<Table, Permission> entry : user.getPermissions().entrySet()) {
            Table key = entry.getKey();
            if (key.getDb().getDatabaseName().equals(databaseName) && key.getTableName().equals(tableName)) {
                Permission permission = user.getPermissions().get(key);
                byte perm1 = permission.getPermission();
                if (( perm & perm1) != 0) {
                    return BaseResponse.ok("verify permission ok");
                }
            }
        }
        return BaseResponse.fail("verify permission failed");
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
                if(!ft.hasMoreFields()){
                    System.err.println("can't find keyword TABLE");
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
                if(!ft.hasMoreFields()){
                    System.err.println("can't find keyword FROM");
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
                if(!ft.hasMoreFields()){
                    System.err.println("can't find keyword FROM");
                }
            }
            perm = (byte) 0b00010000;
        }
    }

}