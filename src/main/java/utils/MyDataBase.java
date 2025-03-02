package utils;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {


    private final String url ="jdbc:mysql://localhost:3306/mdintech";
    private final String username = "root";
    private final String pwd = "";

    private Connection con;

    public static MyDataBase instance;

    private MyDataBase(){
        try {
            con = DriverManager.getConnection(url,username,pwd);
            System.out.println("connected!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance(){
        if (instance == null)
            instance = new MyDataBase();
        return instance;
    }


    public Connection getCon() {
        return con;
    }


}
