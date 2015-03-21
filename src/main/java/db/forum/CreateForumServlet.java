package db.forum;

import temletor.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 15.03.2015.
 */
public class CreateForumServlet extends HttpServlet {
    /*
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        String status = request.getParameter("status");

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("status", status);

        Connection connection = null ;


                    // Database
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");

            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            sqlQuery.executeUpdate("INSERT INTO user VALUES ('1','Me','22');");

            rs.close(); rs=null;
            connection.close();
        }
        catch (SQLException ex){
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ){
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        }
        catch (Exception ex){
            System.out.println("Other Error in Main.");
        }
                       //Database!!!!


        response.getWriter().println(PageGenerator.getPage("Index.html", pageVariables));
    }
*/

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        String status = request.getParameter("status");

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("status", status);

        Connection connection = null ;


        // Database
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");

            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            sqlQuery.executeUpdate("INSERT INTO user VALUES ('1','Me','22');");

            rs.close(); rs=null;
            connection.close();
        }
        catch (SQLException ex){
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ){
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        }
        catch (Exception ex){
            System.out.println("Other Error in Main.");
        }
        //Database!!!!

        response.getWriter().println(PageGenerator.getPage("Index.html", pageVariables));
    }

}