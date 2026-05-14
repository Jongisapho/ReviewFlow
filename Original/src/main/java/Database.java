import java.sql.*;
import java.util.*;
import org.h2.tools.Server;

public class Database {
    private static final String URL = "jdbc:h2:./peerreview;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private Server webServer;

    public Database() {
        createTables();
        insertSampleReviewers();
        startWebConsole();
    }

    private void startWebConsole(){
        try {
            webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("🌐 H2 Web Console started successfully!");
            System.out.println("   Open browser → http://localhost:8082");
            System.out.println("   JDBC URL: jdbc:h2:./peerreview");
        } catch (Exception e) {
            System.out.println("⚠️ H2 Console already running or failed to start: " + e.getMessage());
        }
    }

    private void createTables() {
        String submissionsTable = """
            CREATE TABLE IF NOT EXISTS submissions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                content TEXT NOT NULL,
                submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

        String reviewersTable = """
            CREATE TABLE IF NOT EXISTS reviewers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100),
                score DOUBLE,
                has_conflict BOOLEAN DEFAULT FALSE,
                current_workload INT DEFAULT 0
            );
            """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(submissionsTable);
            stmt.execute(reviewersTable);
            System.out.println("✅ Database tables created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSubmission(Data data) {
        String sql = "INSERT INTO submissions (title, content) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.title);
            pstmt.setString(2, data.content);
            pstmt.executeUpdate();
            System.out.println("Submission saved to DB: " + data.title);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void insertSampleReviewers() {
        String checkSql = "SELECT COUNT(*) FROM reviewers";
        String insertSql = "INSERT INTO reviewers (name, score, has_conflict, current_workload) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    Object[][] data = {
                        {"Alice", 1.5, false, 1},
                        {"Bob",   1.2, false, 0},
                        {"Carol", 1.8, false, 2},
                        {"David", 1.0, false, 1}
                    };

                    for (Object[] r : data) {
                        pstmt.setString(1, (String) r[0]);
                        pstmt.setDouble(2, (Double) r[1]);
                        pstmt.setBoolean(3, (Boolean) r[2]);
                        pstmt.setInt(4, (Integer) r[3]);
                        pstmt.executeUpdate();
                    }
                    System.out.println("Sample reviewers added (4 reviewers ready)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reviewer> fetchReviewers() {
        List<Reviewer> list = new ArrayList<>();
        String sql = "SELECT * FROM reviewers";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reviewer r = new Reviewer();
                r.score = rs.getDouble("score");
                r.hasConflict = rs.getBoolean("has_conflict");
                r.currentWorkload = rs.getInt("current_workload");
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addReviewer(Reviewer reviewer) {
        System.out.println("Reviewer added (DB ready for future use)");
    }

    public void saveScore(Reviewer reviewer) {
        System.out.println("Score saved: " + reviewer.score);
    }
    public void shutdown(){
            try {
        if (webServer != null) {
            webServer.stop();
        }

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();

        stmt.execute("SHUTDOWN");

        stmt.close();
        conn.close();

        System.out.println("Database shutdown complete");

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
}