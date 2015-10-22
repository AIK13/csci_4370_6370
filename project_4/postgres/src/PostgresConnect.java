import java.sql.*;

public class PostgresConnect
{
	public static void main(String[] args)
	{
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = "jdbc:postgresql:myDatabase";
		String username = "mbottone";
		String password = "";
		
		
		try {
			Connection db = DriverManager.getConnection(url, username, password);
			System.out.println("Connected!");
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
