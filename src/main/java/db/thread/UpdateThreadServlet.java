package db.thread;

import db.user.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

/**
 * Created by vitaly on 23.06.15.
 */
public class UpdateThreadServlet extends HttpServlet {
    private Connection connection;
    public UpdateThreadServlet(Connection connection){ this.connection = connection; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject JSONRequest = new JSONObject(jb.toString());

        short status = 0;
        String message = "";

        long thread = 0;
        String messageThread = JSONRequest.getString("message");
        String slug = JSONRequest.getString("slug");

        if (JSONRequest.has("thread") && slug != null && messageThread != null) {
            thread = JSONRequest.getLong("thread");
        } else {
            status = 3;
            message = "Incorrect JSON";
        }

        int result = 0;

        try {
            Statement sqlQuery = connection.createStatement();
            if (status == 0) {
                String query;
                query = "update thread set " +
                        "message = '" + messageThread + "', " +
                        "slug = '" + slug + "' " +
                        "where id = " + thread + ";";
                result = sqlQuery.executeUpdate(query);
                if (result != 1) {
                    status = 1;
                    message = "There is no such thread";
                }
            }
            createResponse(response, status, message, thread);
            sqlQuery.close();
            sqlQuery = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createResponse(HttpServletResponse response, short status, String message, long thread) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        JSONObject data = null;
        if (status == 0) {
            data = getThreadDetailsById((int)thread, false, false);
            if (data == null) {
                status = 1;
                message = "There is no such thread";
            }
        }
        obj.put("response", status == 0? data: message);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }

    //TODO - вынести в вфункцию

    public JSONObject getThreadDetailsById(int id, boolean user, boolean forum) throws IOException, SQLException{
        //Остановился тут!!!!!!!


        ResultSet resultSet;
        ResultSet resultSetCount;

        PreparedStatement pstmt = connection.prepareStatement("select * from thread where id = ?");
        pstmt.setInt(1, id);

        resultSet = pstmt.executeQuery();

        PreparedStatement pstmtCountPosts = connection.prepareStatement("select count(*) as amount from post where thread = " + id + " and isDeleted = 0;");

        resultSetCount = pstmtCountPosts.executeQuery();

        JSONObject data = new JSONObject();

        if (resultSet.next() && resultSetCount.next()) {
            data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
            data.put("dislikes", resultSet.getInt("dislikes"));
            if (forum) {
                //TODO  - исправить - вынести в отельную функцию


                JSONObject forumData = new JSONObject();
                try {
                    ResultSet resultSetForum;

                    PreparedStatement pstmtForum = connection.prepareStatement("select * from forum where short_name = ?");
                    pstmtForum.setString(1, resultSet.getString("forum"));
                    resultSetForum = pstmtForum.executeQuery();

                    if (resultSetForum.next()) {
                        forumData.put("user", resultSetForum.getString("user_email"));
                        forumData.put("name", resultSetForum.getString("name"));
                        forumData.put("id", resultSetForum.getInt("id"));
                        forumData.put("short_name", resultSetForum.getString("short_name"));
                    }
                    pstmtForum.close();
                    pstmtForum = null;

                    resultSetForum.close();
                    resultSetForum = null;

                }catch(SQLException ex) {
                    System.out.println("SQLException caught");
                    System.out.println("---");
                    while (ex != null) {
                        System.out.println("Message   : " + ex.getMessage());
                        System.out.println("SQLState  : " + ex.getSQLState());
                        System.out.println("ErrorCode : " + ex.getErrorCode());
                        System.out.println(ex.getMessage());
                    }
                    System.out.println("---");
                    ex = ex.getNextException();
                }
                data.put("forum", forumData);

            } else {
                data.put("forum", resultSet.getString("forum"));
            }
            data.put("id", resultSet.getInt("id"));
            data.put("isClosed", resultSet.getBoolean("isClosed"));
            data.put("isDeleted", resultSet.getBoolean("isDeleted"));
            data.put("likes", resultSet.getInt("likes"));
            data.put("message", resultSet.getString("message"));
            data.put("points", resultSet.getInt("likes") - resultSet.getInt("dislikes") );
            data.put("posts", resultSetCount.getInt("amount"));
            data.put("slug", resultSet.getString("slug"));
            data.put("title", resultSet.getString("title"));
            if (user) {
                data.put("user", UserInfo.getFullUserInfo(connection, resultSet.getString("user_email")).get("response"));
            } else {
                data.put("user", resultSet.getString("user_email"));
            }
        } else {
            String message = "There is no thread with such id!";
            data.put("error", message);
        }

        pstmtCountPosts.close();
        pstmtCountPosts = null;

        pstmt.close();
        pstmt = null;

        resultSet.close();
        resultSet = null;

        resultSetCount.close();
        resultSetCount = null;

        return data;
    }


}
