import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/jordan";
    private static final String USER = "root";
    private static final String PASSWORD = "17080000";

    // Paramètres du pool de connexions
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 100;
    private static final int MAX_WAIT_TIME_SECONDS = 10;

    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);

    static {
        // Chargement du pilote MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }

        // Initialisation du pool de connexions
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            try {
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                connectionPool.offer(connection);
            } catch (SQLException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * Obtient une connexion à la base de données à partir du pool de connexions.
     *
     * @return une connexion à la base de données
     * @throws RuntimeException si la connexion ne peut pas être obtenue
     */
    public static Connection getConnection() {
        try {
            Connection connection = connectionPool.poll(MAX_WAIT_TIME_SECONDS, TimeUnit.SECONDS);
            if (connection == null) {
                // Si le pool de connexions est plein, créer une nouvelle connexion
                if (connectionPool.size() < MAX_POOL_SIZE) {
                    Connection newConnection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                    connectionPool.offer(newConnection);
                    return newConnection;
                } else {
                    throw new RuntimeException("Timeout while waiting for a database connection");
                }
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for a database connection", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    /**
     * Libère une connexion à la base de données en la remettant dans le pool.
     *
     * @param connection la connexion à libérer
     * @throws RuntimeException si la connexion ne peut pas être remise dans le pool
     */
    public static void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            if (!connection.isClosed() && connectionPool.offer(connection)) {
                return;
            }
        } catch (SQLException e) {
            // Ignorer l'erreur et continuer avec la fermeture de la connexion
        }

        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to release database connection", e);
        }
    }
}
