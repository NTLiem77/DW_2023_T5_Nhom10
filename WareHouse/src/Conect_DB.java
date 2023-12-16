import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conect_DB {
	public Connection	connection(String serverName,String port,String dbName,  String user, String password) {
		
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://"+serverName+":"+port+"/"+dbName;
            // Open a connection
            Connection connection = DriverManager.getConnection(url, user, password);

            // Do something with the connection (e.g., execute SQL queries)

            // Close the connection when done
            return connection;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	
    public static void main(String[] args) {
       System.out.println(new Conect_DB().connection("127.0.0.1","3306" , "Control","root","123456789"));
    }
	
}

