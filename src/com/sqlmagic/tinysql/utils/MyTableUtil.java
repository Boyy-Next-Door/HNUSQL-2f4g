package com.sqlmagic.tinysql.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * �������������б��Ĺ�����
 */
public class MyTableUtil {
    private  ArrayList<Column> columns = new ArrayList<>();
    private  ArrayList<Row> rows = new ArrayList<>();

    public String generate() {
        //����ÿһ�е������
        for (int i = 0; i < columns.size(); i++) {
            int maxLength = 0;
            for (int j = 0; j < rows.size(); j++) {
                int length = getLength(rows.get(j).values.get(i));
                if (length > maxLength) {
                    maxLength = length;
                }
            }

            //ÿһ�еĿ����max(���ݵ������,columnName�������)
            columns.get(i).maxLength = Math.max(maxLength, getLength(columns.get(i).name));
        }

        //���Ʊ�� �����߽���ĵط��� '+'  ���������'--' ������ '|'
        StringBuilder sb = new StringBuilder();
        //��ͷռ����
        //��һ��
        sb.append('+');
        for (int i = 0; i < columns.size(); i++) {
            sb.append('-');
            for (int j = 0; j < columns.get(i).maxLength; j++) {
                sb.append('-');
            }
            sb.append("-+");
        }
        sb.append("\n");

        //�ڶ���
        sb.append("|");
        for (int i = 0; i < columns.size(); i++) {
            sb.append(' ');
            sb.append(columns.get(i).name);
            for (int j = 0; j + 1 + columns.get(i).name.length() <= columns.get(i).maxLength; j++) {
                sb.append(' ');
            }
            sb.append(" |");
        }
        sb.append("\n");

        //������ �͵�һ������һ��
        sb.append('+');
        for (int i = 0; i < columns.size(); i++) {
            sb.append('-');
            for (int j = 0; j < columns.get(i).maxLength; j++) {
                sb.append('-');
            }
            sb.append("-+");
        }
        sb.append("\n");

        //�������
        for (int i = 0; i < rows.size(); i++) {
            sb.append('|');
            for (int j = 0; j < columns.size(); j++) {
                sb.append(" ");
                sb.append(rows.get(i).values.get(j));
                for (int k = 0; k + 1 + getLength(rows.get(i).values.get(j)) <= columns.get(j).maxLength; k++) {
                    sb.append(' ');
                }
                sb.append(" |");
            }
            sb.append("\n");
        }

        //���һ�� �͵�һ��һ��
        sb.append('+');
        for (int i = 0; i < columns.size(); i++) {
            sb.append('-');
            for (int j = 0; j < columns.get(i).maxLength; j++) {
                sb.append('-');
            }
            sb.append("-+");
        }

        return sb.toString();
    }

    private static int getLength(String s) {
        int length = 0;
        for (char c : s.toCharArray()) {
            if (isChineseChar(c)) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }


    //�����ַ�ռ������λ����  �����Ǻܾ�׼
    public static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public MyTableUtil reset() {
        columns = new ArrayList<>();
        rows = new ArrayList<>();
        return this;
    }


    public MyTableUtil addColumn(String name) {
        columns.add(new Column(name));
        return this;
    }

    public MyTableUtil addRow(String... values) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(values));
        rows.add(new Row(list));
        return this;
    }

    public static void main(String[] args) {
        String generate = new MyTableUtil().
                addColumn("id").addColumn("name").addColumn("phone").addColumn("address").
                addRow("1", "George Yang", "18890072933", "Yuanzhou  District Yichun Jiangxi ").
                addRow("2", "Tony Wong", "188900", "Yuanzhou  District Yichun Jiangxi Province 0").
                addRow("3", "IronEgg Li", "190072933", "Yuanzhou  District Yichun Jiangxi Province 111").
                generate();
        System.out.println(generate);
    }

    static class Column {
        public Column(String name) {
            this.name = name;
        }

        private String name;
        private int maxLength;  //���е����ݵ���󳤶�

    }

    static class Row {
        public Row(ArrayList<String> values) {
            this.values = values;
        }

        private ArrayList<String> values = new ArrayList<>();
        private int columnNum = 0;
    }
}
