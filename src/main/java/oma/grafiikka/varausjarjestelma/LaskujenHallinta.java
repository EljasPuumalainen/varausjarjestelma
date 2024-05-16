package oma.grafiikka.varausjarjestelma;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.sql.*;

public class LaskujenHallinta {

    public ObservableList<String> laskudata = FXCollections.observableArrayList();
    public ReadOnlyListProperty<String> laskut = new SimpleListProperty<>(laskudata);

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "KISSAmies5";

    private String kysely = "create view laskutus as\n" +
            "select a.etunimi, a.sukunimi, l.summa, l.maksettu\n" +
            "from asiakas a join varaus v\n" +
            "on a.asiakas_id = v.asiakas_id\n" +
            "join lasku l\n" +
            "on v.varaus_id = l.varaus_id";

    public void teeKysely(){
        try {
            // Luodaan yhteys tietokantaan
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // Tarkistetaan, onko näkymä jo olemassa
            if (isViewExists(connection, "laskutus")) {
                System.out.println("Näkymä on jo olemassa. Suoritetaan kysely...");
                executeQuery(connection, "SELECT * FROM laskutus");
            } else {
                System.out.println("Näkymää ei ole vielä olemassa. Luodaan näkymä...");
                createView(connection, kysely);
                System.out.println("Näkymä luotiin onnistuneesti.");
            }

            // Suljetaan yhteys
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tarkista, onko näkymä olemassa
    private static boolean isViewExists(Connection connection, String laskutus) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getTables(null, null, laskutus, null);
        return resultSet.next();
    }

    // Suorita kysely
    private void executeQuery(Connection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Haetaan etunimi ja sukunimi laskutusnäkymästä
            String etunimi = resultSet.getString("etunimi");
            String sukunimi = resultSet.getString("sukunimi");

            // Lisätään etunimi ja sukunimi laskudata-listaan
            laskudata.add(etunimi + " " + sukunimi);
        }
    }

    // Luo näkymä
    private static void createView(Connection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public TextArea haeLaskuTiedot(String etunimi, String sukunimi){

        TextArea laskujenTiedot = new TextArea();
        laskujenTiedot.clear();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM laskutus WHERE etunimi = ? AND sukunimi = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, etunimi);
            statement.setString(2, sukunimi);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String etunimi1 = resultSet.getString("etunimi");
                String sukunimi1 = resultSet.getString("sukunimi");
                String summa = resultSet.getString("summa");
                String maksettu = resultSet.getString("maksettu");


                laskujenTiedot.setText("Etunimi: " + etunimi1 + "\n" +
                        "Sukunimi: " + sukunimi1 + "\n" +
                        "summa: " + summa + "\n" +
                        "maksettu: " + maksettu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return laskujenTiedot;
    }

    public void luoLasku( String etunimi, String sukunimi, double summa) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // Asetetaan automaattinen commit pois päältä

            // Luodaan uusi lasku varaukselle
            String sql = "INSERT INTO laskutus (etunimi, sukunimi, summa, maksettu) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, etunimi);
            statement.setString(2, sukunimi);
            statement.setDouble(3, summa);
            statement.setBoolean(4, false); // Oletetaan, että lasku ei ole vielä maksettu
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Haetaan generoitu avain (lasku_id)
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedLaskuId = generatedKeys.getInt(1);
                    System.out.println("Uusi lasku luotu onnistuneesti, lasku_id: " + generatedLaskuId);
                } else {
                    System.out.println("Laskun luominen epäonnistui: lasku_id ei saatu.");
                }
            } else {
                System.out.println("Laskun luominen epäonnistui.");
            }

            conn.commit(); // Commitoidaan transaktio
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public double haeMokinHinta(int mokkiId) {
        double mokinHinta = 0.0;

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "SELECT hinta FROM mokki WHERE mokki_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, mokkiId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                mokinHinta = resultSet.getDouble("hinta");
            } else {
                System.out.println("Mökin hintaa ei löytynyt.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mokinHinta;
    }

    public void LaskuTauluun(int lasku_id, int varaus_id, double alv, boolean maksettu, double summa){
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "INSERT INTO lasku (varaus_id, alv, maksettu, summa) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setInt(1, varaus_id);
            statement.setDouble(2, alv);
            statement.setBoolean(3, maksettu);
            statement.setDouble(4, summa);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Uusi lasku luotu onnistuneesti.");
            } else {
                System.out.println("Laskun luominen epäonnistui.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

