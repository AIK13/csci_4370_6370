import java.io.*;
import java.sql.*;
import java.util.*;

public class SQLConnect
{
	/**
	 * Static constants to refer to the tables
	 */
	private static final int STUDENT_TABLE_ID = 0;
	private static final int PROFESSOR_TABLE_ID = 1;
	private static final int COURSE_TABLE_ID = 2;
	private static final int TEACHING_TABLE_ID = 3;
	private static final int TRANSCRIPT_TABLE_ID = 4;
	
	/**
	 * Storage variables
	 */
	private static Connection con;
	private static Comparable[][][] data;
	private static PreparedStatement stmt;
	
	/**
	 * Creates the PostgreSQL database connection.
	 */
	private static void createConnection()
	{
		Properties properties = new Properties();
		
		try {
			File config = new File("config.properties");
			FileInputStream fileInput = new FileInputStream(config);
			properties.load(fileInput);
			fileInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String url = properties.getProperty("jdbc.url");
		String driver = properties.getProperty("jdbc.driver");
		String username = properties.getProperty("jdbc.username");
		String password = properties.getProperty("jdbc.password");
		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			con = DriverManager.getConnection(url, username, password);
			System.out.println("Connected!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates the data values to be inserted into the database.
	 */
	private static void generateTuples()
	{
		TupleGenerator test = new TupleGeneratorImpl ();

        test.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id",
                           null);
        
        test.addRelSchema ("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id",
                           null);
        
        test.addRelSchema ("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode",
                           null);
        
        test.addRelSchema ("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crcCode semester",
                           new String [][] {{ "profId", "Professor", "id" },
                                            { "crsCode", "Course", "crsCode" }});
        
        test.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester",
                           new String [][] {{ "studId", "Student", "id"},
                                            { "crsCode", "Course", "crsCode" },
                                            { "crsCode semester", "Teaching", "crsCode semester" }});
        
        int tups [] = new int [] { 10000, 1000, 2000, 5000, 5000 };
        
        data = test.generate(tups);
        System.out.println("Tuples created!");
	}
	
	/**
	 * Creates the Student table in the database.
	 * @throws SQLException
	 */
	private static void createStudentTable() throws SQLException
	{
		System.out.println("Creating student table...");
		con.prepareStatement("CREATE TABLE IF NOT EXISTS student ("
				+ "id integer PRIMARY KEY,"
				+ "name varchar(64),"
				+ "address varchar(128),"
				+ "status varchar(16)"
				+ ");").execute();
	}
	
	/**
	 * Adds the data from the tuples to the Student database.
	 * @throws SQLException
	 */
	private static void populateStudentTable() throws SQLException
	{
		System.out.println("Populating student table...");
		Comparable[][] studentTable = data[STUDENT_TABLE_ID];
		
		for (int r = 0;r < studentTable.length;r ++)
		{
			Comparable[] tuple = studentTable[r];
			stmt = con.prepareStatement("INSERT INTO student (id, name, address, status) "
					+ "VALUES (?, ?, ?, ?);");
			stmt.setInt(1, (int) tuple[0]);
			stmt.setString(2, (String) tuple[1]);
			stmt.setString(3, (String) tuple[2]); 
			stmt.setString(4, (String) tuple[3]);
			stmt.executeUpdate();
		}
	}
	
	/**
	 * Creates the Professor table.
	 * @throws SQLException
	 */
	private static void createProfessorTable() throws SQLException
	{
		System.out.println("Creating professor table...");
		con.prepareStatement("CREATE TABLE IF NOT EXISTS professor ("
				+ "id integer PRIMARY KEY,"
				+ "name varchar(64),"
				+ "deptId varchar(16)"
				+ ");").execute();
	}
	
	/**
	 * Adds the tuples to the Professor table.
	 * @throws SQLException
	 */
	private static void populateProfessorTable() throws SQLException
	{
		System.out.println("Populating professor table...");
		Comparable[][] professorTable = data[PROFESSOR_TABLE_ID];
		
		for (int r = 0;r < professorTable.length;r ++)
		{
			Comparable[] tuple = professorTable[r];
			stmt = con.prepareStatement("INSERT INTO professor (id, name, deptId) "
					+ "VALUES (?, ?, ?);");
			stmt.setInt(1, (int) tuple[0]);
			stmt.setString(2, (String) tuple[1]);
			stmt.setString(3, (String) tuple[2]);
			stmt.executeUpdate();
		}
	}
	
	/**
	 * Creates the Course table.
	 * @throws SQLException
	 */
	private static void createCourseTable() throws SQLException
	{
		System.out.println("Creating course table...");
		con.prepareStatement("CREATE TABLE IF NOT EXISTS course ("
				+ "crsCode varchar(16) PRIMARY KEY,"
				+ "deptId varchar(16),"
				+ "crsName varchar(64),"
				+ "descr varchar(256)"
				+ ");").execute();
	}
	
	/**
	 * Adds the tuples to the Course table.
	 * @throws SQLException
	 */
	private static void populateCourseTable() throws SQLException
	{
		System.out.println("Populating course table...");
		Comparable[][] courseTable = data[COURSE_TABLE_ID];
		
		for (int r = 0;r < courseTable.length;r ++)
		{
			Comparable[] tuple = courseTable[r];
			stmt = con.prepareStatement("INSERT INTO course (crsCode, deptId, crsName, descr) "
					+ "VALUES (?, ?, ?, ?);");
			stmt.setString(1, (String) tuple[0]);
			stmt.setString(2, (String) tuple[1]);
			stmt.setString(3, (String) tuple[2]); 
			stmt.setString(4, (String) tuple[3]);
			stmt.executeUpdate();
		}
	}
	
	/**
	 * Creates the teaching table.
	 * @throws SQLException
	 */
	private static void createTeachingTable() throws SQLException
	{
		System.out.println("Creating teaching table...");
		con.prepareStatement("CREATE TABLE IF NOT EXISTS teaching ("
				+ "crsCode varchar(16),"
				+ "semester varchar(16),"
				+ "profId integer,"
				+ "CONSTRAINT crsId PRIMARY KEY (crsCode,semester),"
				+ "CONSTRAINT prof FOREIGN KEY (profId) REFERENCES Professor (id),"
				+ "CONSTRAINT crs FOREIGN KEY (crsCode) REFERENCES Course (crsCode)"
				+ ");").execute();
	}
	
	/**
	 * Adds the tuples to the Teaching table.
	 * @throws SQLException
	 */
	private static void populateTeachingTable() throws SQLException
	{
		System.out.println("Populating teaching table...");
		Comparable[][] teachingTable = data[TEACHING_TABLE_ID];
		
		for (int r = 0;r < teachingTable.length;r ++)
		{
			Comparable[] tuple = teachingTable[r];
			stmt = con.prepareStatement("INSERT INTO teaching (crsCode, semester, profId) "
					+ "VALUES (?, ?, ?);");
			stmt.setString(1, (String) tuple[0]);
			stmt.setString(2, (String) tuple[1]);
			stmt.setInt(3, (int) tuple[2]); 
			stmt.executeUpdate();
		}
	}
	
	/**
	 * Creates the Transcript table.
	 * @throws SQLException
	 */
	private static void createTranscriptTable() throws SQLException
	{
		System.out.println("Creating transcript table...");
		con.prepareStatement("CREATE TABLE IF NOT EXISTS transcript ("
				+ "studId integer,"
				+ "crsCode varchar(16),"
				+ "semester varchar(16),"
				+ "grade varchar(16),"
				+ "CONSTRAINT grad PRIMARY KEY (studId,crsCode,semester),"
				+ "CONSTRAINT stud FOREIGN KEY (studId) REFERENCES Student (id),"
				+ "CONSTRAINT crs FOREIGN KEY (crsCode) REFERENCES Course (crsCode),"
				+ "CONSTRAINT section FOREIGN KEY (crsCode,semester) REFERENCES Teaching (crsCode,semester)"
				+ ");").execute();
	}
	
	/**
	 * Adds the tuples to the Transcript table.
	 * @throws SQLException
	 */
	private static void populateTranscriptTable() throws SQLException
	{
		System.out.println("Populating transcript table...");
		Comparable[][] transcriptTable = data[TRANSCRIPT_TABLE_ID];
		
		for (int r = 0;r < transcriptTable.length;r ++)
		{
			Comparable[] tuple = transcriptTable[r];
			stmt = con.prepareStatement("INSERT INTO transcript (studId, crsCode, semester, grade) "
					+ "VALUES (?, ?, ?, ?);");
			stmt.setInt(1, (int) tuple[0]);
			stmt.setString(2, (String) tuple[1]);
			stmt.setString(3, (String) tuple[2]); 
			stmt.setString(4, (String) tuple[3]);
			stmt.executeUpdate();
		}
	}
	
	/**
	 * Empties out the tables if they already exist.
	 * @throws SQLException
	 */
	private static void clearTables() throws SQLException
	{
		System.out.println("Clearing tables...");
		con.prepareStatement("DELETE FROM transcript *").executeUpdate();
		con.prepareStatement("DELETE FROM teaching *").executeUpdate();
		con.prepareStatement("DELETE FROM course *").executeUpdate();
		con.prepareStatement("DELETE FROM professor *").executeUpdate();
		con.prepareStatement("DELETE FROM student *").executeUpdate();
	}
	
	/**
	 * Create and populate all of the tables.
	 * @throws SQLException
	 */
	private static void createTables() throws SQLException
	{
		createStudentTable();
		createProfessorTable();
		createCourseTable();
		createTeachingTable();
		createTranscriptTable();
		
		clearTables();
		
		populateStudentTable();
		populateProfessorTable();
		populateCourseTable();
		populateTeachingTable();
		populateTranscriptTable();
		
		System.out.println("Tables generated!");
	}
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args)
	{
		createConnection();
		if (con == null) {return;}
		generateTuples();
		
		try {
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
