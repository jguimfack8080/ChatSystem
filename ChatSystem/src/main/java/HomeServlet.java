import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/Chat";
	private static final String USER = "root";
	private static final String PASS = "17080000";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }
		
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName(JDBC_DRIVER);
	        conn = DriverManager.getConnection(DB_URL, USER, PASS);
	        stmt = conn.createStatement();
	        session = request.getSession();
	        String currentUser = (String) session.getAttribute("username");
	        String sql = "SELECT * FROM users WHERE username != '" + currentUser + "'";
	        rs = stmt.executeQuery(sql);

	        PrintWriter out = response.getWriter();
	        out.println("<html>");
	        out.println("<head>");
	        out.println("<title>Users List</title>");
	        out.println("<style>");
	        out.println("ul {list-style-type: none;padding: 0;margin: 0;}");
	        out.println("li {padding: 8px 16px;border-bottom: 1px solid #ddd;}");
	        out.println("li:last-child {border-bottom: none;}");
	        out.println("a {display: block;color: #000;}");
	        out.println("a:hover {background-color: #ddd;}");
	        out.println("form {margin-top: 16px;}");
	        out.println("input[type=submit] {background-color: #2e7bcf;color: white;padding: 8px 16px;border: none;border-radius: 4px;cursor: pointer;}");
	        out.println("</style>");
	        out.println("</head>");
	        out.println("<body>");
	        out.println("<h1>List of Users</h1>");
	        out.println("<ul>");

	        while (rs.next()) {
	            String username = rs.getString("username");
	            out.println("<li><a href='messages?recipient=" + username + "'>" + username + "</a></li>");
	        }

	        out.println("</ul>");
	        out.println("<form action='logout' method='POST'>");
	        out.println("<input type='submit' value='Logout'>");
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

}
