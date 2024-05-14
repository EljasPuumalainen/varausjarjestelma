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
            String sql = "SELECT * FROM laskus WHERE etunimi = ? AND sukunimi = ?";
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

}

