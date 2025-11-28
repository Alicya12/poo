import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.FileWriter;

public class RentalSummary {

    private String url;
    private String user;
    private String password;

    public RentalSummary(String propertiesPath) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesPath));

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("db.properties está incompleto!");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro lendo db.properties: " + e.getMessage());
        }
    }

    public void gerarResumo(double minimo) {
        String sql =
            "SELECT c.first_name, c.last_name, SUM(p.amount) AS total " +
            "FROM customer c " +
            "JOIN payment p ON p.customer_id = c.customer_id " +
            "GROUP BY c.first_name, c.last_name " +
            "HAVING SUM(p.amount) > ? " +
            "ORDER BY total DESC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minimo);
            ResultSet rs = stmt.executeQuery();

            // ---------- GERAR CSV ----------
            FileWriter csv = new FileWriter("rentals.csv");
            csv.write("first_name,last_name,total\n");

            System.out.println("Clientes que gastaram acima de " + minimo + ":");

            while (rs.next()) {
                String fname = rs.getString("first_name");
                String lname = rs.getString("last_name");
                double total = rs.getDouble("total");

                System.out.println(fname + " " + lname + " -> R$ " + total);

                csv.write(fname + "," + lname + "," + total + "\n");
            }

            csv.close();
            System.out.println("\nArquivo CSV gerado com sucesso: rentals.csv");

        } catch (Exception e) {
            System.out.println("Erro no banco: " + e.getMessage());
        }
    }
}

