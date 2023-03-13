import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/message")
public class MessageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Chat";
    private static final String USER = "root";
    private static final String PASS = "17080000";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String recipient = request.getParameter("recipient");
            String sql = "SELECT * FROM messages WHERE (sender='" + currentUser + "' AND recipient='" + recipient
                    + "') OR (sender='" + recipient + "' AND recipient='" + currentUser + "') ORDER BY timestamp ASC";
            rs = stmt.executeQuery(sql);

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Messages with " + recipient + "</title>");
            out.println("<style>");
            out.println("body {font-family: Arial, sans-serif;}");
            out.println("h1 {color: #333333;}");
            out.println(
                    "p.sender {color: #008000; background-color: #E6FFE6; padding: 5px; border-radius: 10px; margin-left: 50%; transform: translateX(-50%); text-align: left; margin-bottom: 10px;}");
            out.println(
                    "p.recipient {color: #0000FF; background-color: #FFFFFF; padding: 5px; border-radius: 10px; margin-right: 50%; transform: translateX(50%); text-align: right; margin-bottom: 10px;}");
            out.println("textarea {width: 100%;}");
            out.println("input[type='submit'] {background-color: #2e7bcf;color: white;padding: 8px 16px;border: none;border-radius: 4px;cursor: pointer;}");
            out.println(".btn-logout {position: absolute;top: 10px;right: 10px;}");
            out.println(".btn-home {background-color: #2e7bcf;color: white;padding: 8px 16px;border: none;border-radius: 4px;cursor: pointer;margin-bottom: 10px;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Messages with " + recipient + "</h1>");

            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
                if (sender.equals(currentUser)) {
                    out.println("<p class='sender'>" + sender + " (" + formattedTime + "): " + content + "</p>");
                } else {
                    out.println("<p class='recipient'>" + sender + " (" + formattedTime + "): " + content + "</p>");
                }
            }

            out.println("<form action='messages' method='POST'>");
            out.println("<input type='hidden' name='recipient' value='" + recipient + "'>");
            out.println("<textarea name='message' rows='4' required=''></textarea><br><p>");
            out.println("<input type='submit' value='Send'>");
            out.println("</form>");

            out.println("<form action='logout' method='POST'>");
            out.println("<input type='submit' class='btn-logout' value='Logout'>");
            out.println("</form>");

            out.println("<form action='home'>");
            out.println("<input type='submit' class='btn-home' value='Home'>");
            out.println("</form>");

            out.println("</body>");
            out.println("</html>");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sender = request.getSession().getAttribute("username").toString();
            String recipient = request.getParameter("recipient");
            String content = request.getParameter("message");
            String sql = "INSERT INTO messages (sender, recipient, content) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setString(3, content);
            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/messages?recipient=" + recipient);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

}
