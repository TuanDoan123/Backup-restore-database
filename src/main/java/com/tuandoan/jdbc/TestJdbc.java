package com.tuandoan.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestJdbc {

	public static void main(String[] args) {

		String dbURL = "jdbc:sqlserver://localhost;databaseName=customer;user=sa;password=sa";

		
		try {
			System.out.println("Connecting to database: ");
			//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = DriverManager.getConnection(dbURL);
			
			System.out.println("Connection successful!!! TTTTTTTT");
			
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
	}

}



