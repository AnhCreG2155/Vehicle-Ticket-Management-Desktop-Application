package DatabaseConnect;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/do_an_co_so";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1125678932";
   
    public static Connection DBConnection() throws SQLException {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy driver MySQL: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try (Connection conn = DBConnection()) {
            System.out.println("Kết nối thành công!");
        } catch (SQLException e) {
            System.err.println("Kết nối thất bại: " + e.getMessage());
        }
    }
}
