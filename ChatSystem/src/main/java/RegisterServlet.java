import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/Chat";
	private static final String USER = "root";
	private static final String PASS = "17080000";

	@SuppressWarnings("resource")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    String username = request.getParameter("username");
	    String password = request.getParameter("password");
	    String confirm_password = request.getParameter("confirmPassword");
	    String email = request.getParameter("email");
	    
	    if (!password.equals(confirm_password)) {
	        response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
	        out.println("<html><body><script>alert('Die beiden Passwörter stimmen nicht überein!');"
	        		+ " window.location='register.html';</script></body></html>");
	        return;
	    }
	    
	    Connection conn = null;
	    PreparedStatement stmt = null;

	    try {
	        Class.forName(JDBC_DRIVER);
	        conn = DriverManager.getConnection(DB_URL, USER, PASS);

	        // Vérifier si l'utilisateur existe déjà
	        String checkUserSql = "SELECT * FROM users WHERE username=?";
	        stmt = conn.prepareStatement(checkUserSql);
	        stmt.setString(1, username);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            // L'utilisateur existe déjà, envoyer une erreur
	            response.setContentType("text/html");
	            PrintWriter out = response.getWriter();
	            out.println("<html><body><script>alert('Der Benutzername ist bereits vergeben!');"
	                    + " window.location='register.html';</script></body></html>");
	            return;
	        }
	        rs.close();

	        // Vérifier si l'adresse email existe déjà
	        String checkEmailSql = "SELECT * FROM users WHERE email=?";
	        stmt = conn.prepareStatement(checkEmailSql);
	        stmt.setString(1, email);
	        rs = stmt.executeQuery();
	        if (rs.next()) {
	            // L'adresse email existe déjà, envoyer une erreur
	            response.setContentType("text/html");
	            PrintWriter out = response.getWriter();
	            out.println("<html><body><script>alert('Die E-Mail-Adresse ist bereits vergeben!');"
	                    + " window.location='register.html';</script></body></html>");
	            return;
	        }
	        rs.close();

	        // Insérer l'utilisateur dans la base de données
	        String insertSql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
	        stmt = conn.prepareStatement(insertSql);
	        stmt.setString(1, username);
	        stmt.setString(2, password);
	        stmt.setString(3, email);
	        stmt.executeUpdate();

	        // Rediriger vers la page de connexion
	        response.sendRedirect("login.html");
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
