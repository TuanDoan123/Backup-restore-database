package com.tuandoan.jdbc;

import java.sql.*;

public class Connect {
    private Connection con;
    private final String url = "jdbc:sqlserver://";
    private final String serverName = "localhost";
    private final String portNumber = "1433";
    private final String databaseName = "master";
    private String userName;
    private String password;

    // Constructor
    public Connect() {
    }

    private String getConnectionUrl() {
        return url + serverName + ":" + portNumber + ";databaseName=" + databaseName;
    }

    public Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(getConnectionUrl(), userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    public void displayDbProperties() {
        DatabaseMetaData dm = null;
        ResultSet rs = null;
        try {
            con = this.getConnection();
            if (con != null) {
                dm = con.getMetaData();
                System.out.println("Driver Information");
                System.out.println("\tDriver Name: " + dm.getDriverName());
                System.out.println("\tDriver Version: " + dm.getDriverVersion());
                System.out.println("\nDatabase Information ");
                System.out.println("\tDatabase Name: " + dm.getDatabaseProductName());
                System.out.println("\tDatabase Version: " + dm.getDatabaseProductVersion());
                System.out.println("Avalilable Catalogs ");
                rs = dm.getCatalogs();
                while (rs.next()) {
                    System.out.println("\tcatalog: " + rs.getString(1));
                }
                rs.close();
                rs = null;
                closeConnection();
            } else {
                System.out.println("Error: No active Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dm = null;
    }

    public void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
            con = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) throws Exception {
        Connect myDbTest = new Connect();
        myDbTest.setUserName("sa");
        myDbTest.setPassword("sa1");
        myDbTest.displayDbProperties();
    }

}
