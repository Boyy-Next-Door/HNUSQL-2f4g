package com.sqlmagic.tinysql.instruction;

import com.sqlmagic.tinysql.FieldTokenizer;
import com.sqlmagic.tinysql.tinySQL;
import com.sqlmagic.tinysql.tinySQLWhere;

import java.sql.Types;
import java.util.Hashtable;
import java.util.Vector;

public class PreParser {
    String operation;
    String tableName;

    public void setParser(){
        Vector columnList,tableList,actionList,valueList,contextList,
                columnAliasList,columns,userList;
        String nowUserName;//当前用户名（待获取）
        Hashtable tables,users;
        tinySQL dbEngine;
        Byte Granting=0b00000000;
        tinySQLWhere whereClause;
        String tableName,tableAlias,dataDir,dbName;
        String userName,password;
        String statementType=(String)null;
        String lastKeyWord=(String)null,orderType=(String)null,
                inputString=(String)null,inputKeyWord=(String)null;
        String oldColumnName=(String)null,newColumnName=(String)null;
        String[] colTypeNames = {"INT","FLOAT","CHAR","DATE"};
        int[] colTypes = {Types.INTEGER,Types.FLOAT,Types.CHAR,Types.DATE};
        boolean distinct=false,defaultOrderBy=true;
            String getKeyWord1=null,getKeyWord2=null;
            String[] getKeyWord;
            String nextField,upperField,colTypeStr,colTypeSpec,
                    fieldString,syntaxErr,tempString,columnName,columnAlias;
            StringBuffer colTypeBuffer,concatBuffer;
            FieldTokenizer ft1,ft2,ft3;
            int i,j,k,lenc,colType,countFields;
            /*
             *    Handle compound keywords.
             */
            if ( inputString == (String)null )
            {
                lastKeyWord = inputKeyWord;
                return;
            } else if ( inputString.trim().length() == 0 ) {
                lastKeyWord = inputKeyWord;
                return;
            }
            ft1 = new FieldTokenizer(inputString,',',false);
            while ( ft1.hasMoreFields() )
            {
                nextField = ft1.nextField().trim();
                upperField = nextField.toUpperCase();
                if ( inputKeyWord.equals("SELECT") )
                {
                    /*
                     *          Check for the keyword DISTINCT
                     */
                    if (nextField.toUpperCase().startsWith("DISTINCT") )
                    {
                        distinct = true;
                        nextField = nextField.substring(9).trim();
                    }
                    /*
                     *          Check for and set column alias.
                     */
                    ft2 = new FieldTokenizer(nextField,' ',false);
                    columnName = ft2.getField(0);
                    columnAlias = (String)null;
                    /*
                     *          A column alias can be preceded by the keyword AS which will
                     *          be ignored by tinySQL.
                     */
                    if ( ft2.countFields() == 2 ) columnAlias = ft2.getField(1);
                    else if ( ft2.countFields() == 3 ) columnAlias = ft2.getField(2);
                    /*
                     *          Check for column concatenation using the | symbol
                     */
                    ft2 = new FieldTokenizer(columnName,'|',false);
                    if ( ft2.countFields() > 1 )
                    {
                        concatBuffer = new StringBuffer("CONCAT(");
                        while ( ft2.hasMoreFields() )
                        {
                            if ( concatBuffer.length() > 7 )
                                concatBuffer.append(",");
                            concatBuffer.append(ft2.nextField());
                        }
                        columnName = concatBuffer.toString() + ")";
                    }
                } else if ( inputKeyWord.equals("TABLE") ) {

                    if ( !statementType.equals("INSERT") )
                        statementType = statementType + "_TABLE";
                    if ( statementType.equals("CREATE_TABLE") )
                    {

                    }
                    else if ( statementType.equals("DROP_TABLE") ) {
                    }
                } else if ( inputKeyWord.equals("BY") ) {

                } else if ( inputKeyWord.equals("DROP") ) {
                    /*
                     *          Parse list of columns to be dropped.
                     */
                    statementType = "ALTER_DROP";
                    ft2 = new FieldTokenizer(upperField,' ',false);
                } else if ( inputKeyWord.equals("RENAME") ) {
                    /*
                     *          Parse old and new column name.
                     */
                    statementType = "ALTER_RENAME";
                    ft2 = new FieldTokenizer(upperField,' ',false);
                    oldColumnName = ft2.getField(0);
                    newColumnName = ft2.getField(1);
                    if ( newColumnName.equals("TO") & ft2.countFields() == 3 )
                        newColumnName = ft2.getField(2);

                } else if ( inputKeyWord.equals("ADD") ) {
                    /*
                     *          Parse definition of columns to be added.
                     */
                    statementType = "ALTER_ADD";
                } else if ( inputKeyWord.equals("FROM") ) {
                    /*
                     *          Check for valid table
                     */
                    tableName = upperField;
                } else if ( inputKeyWord.equals("INTO") ) {
                    ft2 = new FieldTokenizer(nextField,'(',false);
                    tableName = ft2.getField(0).toUpperCase();
                    fieldString = ft2.getField(1).toUpperCase();
                    ft2 = new FieldTokenizer(fieldString,',',false);

                } else if ( inputKeyWord.equals("VALUES") ) {

                } else if ( inputKeyWord.equals("UPDATE") ) {
                    tableName = nextField.toUpperCase();
                } else if ( inputKeyWord.equals("SET") ) {

                } else if ( inputKeyWord.equals("WHERE") ) {

                } else if( inputKeyWord.equals("USER") ){
                    statementType = statementType + "_USER";
                    /*DROP_USER or CREATE USER*/
                    if ( statementType.equals("DROP_USER") ){
                        /*
                         * DROP USER username
                         */
                        userName = nextField;
                    }else if ( statementType.equals("CREATE_USER") ){
                        /*
                         * CREATE USER username IDENTIFIED BY password;
                         */
                        userName = nextField;
                    }
                } else if( inputKeyWord.equals("IDENTIFIED") ){
                    ft2 = new FieldTokenizer(nextField,' ',false);
                    password = ft2.getField(1);
                } else if( inputKeyWord.equals("GRANT")) {
                    statementType = "GRANT";
                        //对该权限对用户进行操作
                }else if( inputKeyWord.equals("REVOKE")) {
                    statementType = "REVOKE";
                    ft2 = new FieldTokenizer(nextField,',',false);
                        //对该权限对用户进行操作
                } else if(inputKeyWord.equals("ON")){
                    /*获取到需要更改权限的表或数据库*/
                    ft2 = new FieldTokenizer(nextField,'.',false);
                    if ( ft2.countFields() == 2 ){
                        dbName = ft2.getField(0);
                        tableName = ft2.getField(1);
                        //System.out.println(dbName + "+" + tableName);
                    }else if( ft2.countFields() == 1 ){
                        tableName = ft2.getField(0);
                    }
                }else if(inputKeyWord.equals("TO")){
                    userName = nextField;
                }else if(inputKeyWord.equals("FROM")) {
                    userName = nextField;
                } else if(inputKeyWord.equals("WITH")){
                    statementType = statementType + "_WITH";
                    ft2 = new FieldTokenizer(nextField,' ',false); //获取后面两个字

                    Granting = (byte)(Granting >>> 4 | Granting);
                    if(ft2.countFields()==2){
                        getKeyWord1 = ft2.getField(0);
                        getKeyWord2 = ft2.getField(1);
                    }


                    if(getKeyWord1.toUpperCase().equals("LINK") && getKeyWord2.toUpperCase().equals("OPTION")){
                        statementType = statementType  + "_LINK";
                    }else if(getKeyWord1.toUpperCase().equals("ADMIN") && getKeyWord2.toUpperCase().equals("OPTION")){
                        statementType = statementType  + "_ADMIN";
                    }

                }

            }
        }
}