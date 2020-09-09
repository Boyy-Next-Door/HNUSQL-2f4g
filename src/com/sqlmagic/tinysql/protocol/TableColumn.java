package com.sqlmagic.tinysql.protocol;

public class TableColumn {
        private String columnName;
        private String typeName;
        private String width;
        private String scale;
        private String precision;

        public TableColumn() {
        }

        public TableColumn(String columnName, String typeName, String width, String scale, String precision) {
            this.columnName = columnName;
            this.typeName = typeName;
            this.width = width;
            this.scale = scale;
            this.precision = precision;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getScale() {
            return scale;
        }

        public void setScale(String scale) {
            this.scale = scale;
        }

        public String getPrecision() {
            return precision;
        }

        public void setPrecision(String precision) {
            this.precision = precision;
        }
    }