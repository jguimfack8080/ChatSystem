import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@WebServlet("/centralVrai")

public class centralCoronaApp extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			// Si la session n'existe pas, redirection vers la page de connexion
			response.sendRedirect("login.html");
			return;
		}
		// Recuperation des variables de sessions depuis la page login.html
		String username = (String) session.getAttribute("username");
		String firstName = (String) session.getAttribute("firstName");
		String lastName = (String) session.getAttribute("lastName");
		String email = (String) session.getAttribute("email");
		String city = (String) session.getAttribute("city");
		String postalCode = (String) session.getAttribute("postalCode");

		// Recuperation des informations de rendez-vous depuis le formulaire de la page
		// central.html
		String date1 = request.getParameter("date1");
		String heure1 = request.getParameter("heure1");
		String date2 = request.getParameter("date2");
		String heure2 = request.getParameter("heure2");
		String vaccine = request.getParameter("vaccine");

		// Verification si toutes les donnes du formulaire ont ete remplies.
		if (date1 != null && !date1.isEmpty() && heure1 != null && !heure1.isEmpty() && date2 != null
				&& !date2.isEmpty() && heure2 != null && !heure2.isEmpty() && vaccine != null && !vaccine.isEmpty())

		{

			// Declaration de mes variables de ssessions qui recuperent les informations des
			// rendez-vous
			session.setAttribute("date1", date1);
			session.setAttribute("heure1", heure2);
			session.setAttribute("date2", date2);
			session.setAttribute("heure2", heure2);
			session.setAttribute("vaccine", vaccine);

			// Verification de la validite des dates et heures de rendez-vous
			// Verification de la validite des dates et heures de rendez-vous
			
			
			
			// Obtenez la date actuelle
			LocalDate currentDate = LocalDate.now();

			// Analysez les dates de rendez-vous à partir des données de formulaire
			
			// Vérifiez si l'une des dates de rendez-vous est dans le passé
			
			
			
			if (!date1.equals(date2)) 
			{
				// Verification que la date2 est minimum 14 apres la date 1
				LocalDate d1 = LocalDate.parse(date1);
				LocalDate d2 = LocalDate.parse(date2);
				long diff = ChronoUnit.DAYS.between(d1, d2);
				
//Verification si la date entre n'est pas dans le passe
				
				if(d1.isBefore(currentDate) || d2.isBefore(currentDate)) {
					response.getWriter().println("<br><h2>Das ausgewählte Datum darf nicht in der Vergangenheit liegen</h2>");
					return;
				}
				
				if (diff < 14) 
				{
					System.out.println(d1);
					System.out.println(currentDate);
					response.getWriter().println(
							"<br><h2>Das Datum des zweiten Termins muss mindestens 14 Tage nach dem Datum des ersten Termins liegen.</h2>");

					return;
				}
				
				
				
			}

			// Verification de la disponibilite des heures de rendez-vous choisies
			try 
			{

				// Connexion a la base de donnees
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DatabaseConnection.getConnection();

				// Verification de la disponibilite de l'heure1
				// Compter le nombre de rendez-vous pour l'heure 1 a la date 1
				PreparedStatement stmt1 = con
						.prepareStatement("SELECT COUNT(*) FROM appointmentsApp WHERE date1=? AND heure1=?");
				stmt1.setString(1, date1);
				stmt1.setString(2, heure1);
				ResultSet rs1 = stmt1.executeQuery();
				rs1.next();
				int count1 = rs1.getInt(1);

				// Verifier si le creneau horaire 1 a la date 1 est disponible
				if (count1 >= 2) {
					response.getWriter().println(
							"<br><h2>Die für den ersten Termin gewählte Uhrzeit ist nicht mehr verfügbar. Bitte wählen Sie eine andere.<h2>");
					return;
				}

				// Compter le nombre de rendez-vous pour l'heure 2 a la date 2
				PreparedStatement stmt2 = con
						.prepareStatement("SELECT COUNT(*) FROM appointmentsApp WHERE date2=? AND heure2=?");
				stmt2.setString(1, date2);
				stmt2.setString(2, heure2);
				ResultSet rs2 = stmt2.executeQuery();
				rs2.next();
				int count2 = rs2.getInt(1);

				// Verifier si le creneau horaire 2 a la date 2 est disponible
				if (count2 >= 2) {

					response.getWriter().println(
							"<br><h2>Die für den zweiten Termin gewählte Uhrzeit ist nicht mehr verfügbar. Bitte wählen Sie eine andere.</h2>");
					return;
				}

				// Verification pour que l'utilisateur ne puisse pas reserve un meme rendez vous
				// plusieurs fois

				// Compter le nombre de rendez-vous pour l'heure 1 a la date 1
				PreparedStatement stmt3 = con.prepareStatement(
						"SELECT COUNT(*) FROM appointmentsApp WHERE date1=? AND heure1=? AND username=?");
				stmt3.setString(1, date1);
				stmt3.setString(2, heure1);
				stmt3.setString(3, username);
				ResultSet rs3 = stmt3.executeQuery();
				rs3.next();
				count1 = rs3.getInt(1);

				// Verifier si l'utilisateur connecte a deja reserve ce creneau horaire
				if (count1 > 0) {

					response.getWriter().println(
							"Sie haben bereits einen Termin für die Uhrzeit des ersten gewählten Zeitfensters gebucht.");
					return;
				}

				// Compter le nombre de rendez-vous pour l'heure 2 a la date 2
				PreparedStatement stmt4 = con.prepareStatement(
						"SELECT COUNT(*) FROM appointmentsApp WHERE date2=? AND heure2=? AND username=?");
				stmt4.setString(1, date2);
				stmt4.setString(2, heure2);
				stmt4.setString(3, username);
				ResultSet rs4 = stmt4.executeQuery();
				rs4.next();
				count2 = rs4.getInt(1);

				// Verifier si l'utilisateur connecte a deja reserve ce creneau horaire
				if (count2 > 0) 
				{
					response.getWriter().println(
							"Sie haben bereits einen Termin für die Uhrzeit des zweiten gewählten Zeitfensters gebucht.");
					return;
				}

				// Enregistrement du rendez-vous dans la base de donnees
				PreparedStatement stmt5 = con.prepareStatement(
						"INSERT INTO appointmentsApp (username, first_name, last_name, email, city, postal_code, date1, heure1, date2, heure2, vaccine) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				stmt5.setString(1, username);
				stmt5.setString(2, firstName);
				stmt5.setString(3, lastName);
				stmt5.setString(4, email);
				stmt5.setString(5, city);
				stmt5.setString(6, postalCode);
				stmt5.setString(7, date1);
				stmt5.setString(8, heure1);
				stmt5.setString(9, date2);
				stmt5.setString(10, heure2);
				stmt5.setString(11, vaccine);
				int rowsAffected = stmt5.executeUpdate();
				if (rowsAffected > 0) {
					//response.getWriter().println("Ihr Termin ist bestätigt");
					response.sendRedirect("confirmation.html");
				} else {
					response.getWriter().println(
							"Ihr Termin konnte nicht gespeichert werden. Bitte versuchen Sie es zu einem späteren Zeitpunkt erneut.");
				}
				con.close();
			} catch (Exception e) {
				response.getWriter().println("Erreur : " + e.getMessage());
			}

			// Toutes les donn�es ont �t� remplies

		} else {

			// Verification pour chaque Donnee
			if (date1 != null && date1.isEmpty()) {

				response.getWriter().println("<br><h2>Bitte wählen Sie ein Datum für den ersten Termin</h2>");
				return;
			}

			if (heure1 != null && heure1.isEmpty()) {

				response.getWriter().println("<br><h2>Bitte wählen Sie eine Uhrzeit für den ersten Termin</h2>");
				return;
			}

			if (date2 != null && date2.isEmpty()) {

				response.getWriter().println("<br><h2>Wählen Sie ein Datum für den zweiten Termin</h2>");
				return;
			}

			if (heure2 != null && heure2.isEmpty()) {

				response.getWriter().println("<br><h2>Bitte wählen Sie eine Uhrzeit für den zweiten Termin</h2>");
				return;
			}

			if (vaccine != null && vaccine.isEmpty()) {

				response.getWriter().println("<br><h2>Bitte wählen Sie die gewünschte Impfstoffmarke aus.</h2>");
				return;
			}

			// response.getWriter().println("Veuillez remplir tous les champs");
		}

	}
}