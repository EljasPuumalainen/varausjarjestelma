package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;


import java.sql.*;

public class PalveluidenHallinta {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "KISSAmies5";

    public ObservableList<String> palveluidenData = FXCollections.observableArrayList();
    public ReadOnlyListProperty<String> palvelu = new SimpleListProperty<>(palveluidenData);

    public PalveluidenHallinta()  {
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)
        ) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select nimi from palvelu");

            while (resultSet.next()) {
                // Käsittele tulokset täällä
                String column1 = resultSet.getString("nimi");

                palveluidenData.add(column1);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    // Päivitetty lisaaPalvelu-metodi PalveluidenHallinta-luokassa
    public void lisaaPalvelu(int palveluId, String alueNimi, String nimi, String kuvaus, double hinta, double alv) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // Etsi alue_id alueen nimen perusteella
            String alueIdQuery = "SELECT alue_id FROM alue WHERE nimi = ?";
            PreparedStatement alueIdStatement = conn.prepareStatement(alueIdQuery);
            alueIdStatement.setString(1, alueNimi);
            ResultSet alueIdResult = alueIdStatement.executeQuery();
            if (alueIdResult.next()) {
                int alueId = alueIdResult.getInt("alue_id");
                String sql = "INSERT INTO palvelu (palvelu_id, alue_id, nimi, kuvaus, hinta, alv) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setInt(1, palveluId);
                statement.setInt(2, alueId);
                statement.setString(3, nimi);
                statement.setString(4, kuvaus);
                statement.setDouble(5, hinta);
                statement.setDouble(6, alv);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Palvelu lisätty onnistuneesti");
                } else {
                    System.out.println("Palvelun lisääminen epäonnistui");
                }
            } else {
                System.out.println("Aluetta ei löytynyt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void poistaPalvelu(String nimi) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "DELETE FROM palvelu WHERE nimi = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nimi);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Palvelu poistettu onnistuneesti.");
            } else {
                System.out.println("Palvelun poistaminen epäonnistui.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public TextArea haePalvelunTiedot(String nimi) {
        TextArea tiedot = new TextArea();
        tiedot.clear();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "SELECT palvelu.*, alue.nimi AS alue FROM palvelu INNER JOIN alue on palvelu.alue_id = alue.alue_id WHERE palvelu.nimi = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nimi);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double hinta = resultSet.getDouble("hinta");
                double alv = resultSet.getDouble("alv");
                double pelkkaAlvHinta = alv - hinta;
                String pyoristettyAlvHinta = String.format("%.2f", pelkkaAlvHinta);
                tiedot.setText("Nimi: " + resultSet.getString("nimi") +
                        "\nKuvaus: " + resultSet.getString("kuvaus") +
                        "\nArvolisäveroton hinta: " + hinta +
                        "\nAlv: " + pyoristettyAlvHinta +
                        "\nKokonaishinta: " + alv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tiedot;
    }





}



