package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.sql.*;

public class Mokki {

    private final String postinro;
    private final String mokkinimi;
    private final String katuosoite;
    private final double hinta;
    private final String kuvaus;
    private final int henkilomaara;
    private final String varustelu;
    private String alue;

    public Mokki(String postinro, String mokkinimi, String katuosoite, double hinta,
                 String kuvaus, int henkilomaara, String varustelu, String alue) {
        this.postinro = postinro;
        this.mokkinimi = mokkinimi;
        this.katuosoite = katuosoite;
        this.hinta = hinta;
        this.kuvaus = kuvaus;
        this.henkilomaara = henkilomaara;
        this.varustelu = varustelu;
        this.alue = alue;
    }

    public String getAlue() {
        return alue;
    }

    public void setAlue(String alue) {
        this.alue = alue;
    }

    public String getNimi() {
        return mokkinimi;
    }


    public double getHinta() {
        return hinta;
    }

    public String getKatuosoite() {
        return katuosoite;
    }

    public String getPostinro() {
        return postinro;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public int getHenkilomaara() {
        return henkilomaara;
    }

    public String getVarustelu() {
        return varustelu;
    }

    public StringProperty getNimiProperty() {
        return new SimpleStringProperty(mokkinimi);
    }


    public DoubleProperty getHintaProperty() {
        return new SimpleDoubleProperty(hinta);
    }

    public StringProperty getKatuosoiteProperty() {
        return new SimpleStringProperty(katuosoite);
    }

    public StringProperty getPostinroProperty() {
        return new SimpleStringProperty(postinro);
    }

    public StringProperty getKuvausProperty() {
        return new SimpleStringProperty(kuvaus);
    }

    public StringProperty getHenkilomaaraProperty() {
        return new SimpleStringProperty(Integer.toString(henkilomaara));
    }

    public StringProperty getVarusteluProperty() {
        return new SimpleStringProperty(varustelu);
    }

    public static void lisaaMokkiTietokantaan(Connection connection, Mokki mokki) throws SQLException {
        try {
            // Tarkista ensin, onko postinumero jo olemassa tietokannassa
            String checkIfExistsSQL = "SELECT COUNT(*) FROM posti WHERE postinro = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkIfExistsSQL);
            checkStatement.setString(1, mokki.getPostinro());
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) { // Jos postinumeroa ei löytynyt, lisää se
                // SQL-lause, joka lisää postin tiedot tietokantaan
                String insertPostSQL = "INSERT INTO posti (postinro, toimipaikka) VALUES (?, ?)";
                PreparedStatement insertPostStatement = connection.prepareStatement(insertPostSQL);
                insertPostStatement.setString(1, mokki.getPostinro());
                insertPostStatement.setString(2, "Toimipaikka"); // Oletetaan, että toimipaikka on aina sama, voit vaihtaa tarvittaessa
                insertPostStatement.executeUpdate();
            }

            // SQL-lause, joka lisää mökin tiedot tietokantaan
            String insertMokkiSQL = "INSERT INTO mokki (postinro, mokkinimi, katuosoite, hinta, kuvaus, henkilomaara, varustelu, alue_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT alue_id FROM alue WHERE nimi = ?))";
            PreparedStatement insertMokkiStatement = connection.prepareStatement(insertMokkiSQL);
            insertMokkiStatement.setString(1, mokki.getPostinro());
            insertMokkiStatement.setString(2, mokki.getNimi());
            insertMokkiStatement.setString(3, mokki.getKatuosoite());
            insertMokkiStatement.setDouble(4, mokki.getHinta());
            insertMokkiStatement.setString(5, mokki.getKuvaus());
            insertMokkiStatement.setInt(6, mokki.getHenkilomaara());
            insertMokkiStatement.setString(7, mokki.getVarustelu());
            insertMokkiStatement.setString(8, mokki.getAlue()); // Käytetään ComboBoxista valittua alueen nimeä
            insertMokkiStatement.executeUpdate();

            System.out.println("Mökin lisäys tietokantaan onnistui!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static ObservableList<Mokki> haeMokitTietokannasta(Connection connection) throws SQLException {
        ObservableList<Mokki> mokit = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT mokki.*, alue.nimi AS alue FROM mokki INNER JOIN alue ON mokki.alue_id = alue.alue_id");
        while (resultSet.next()) {
            Mokki mokki = new Mokki(
                    resultSet.getString("postinro"),
                    resultSet.getString("mokkinimi"),
                    resultSet.getString("katuosoite"),
                    resultSet.getDouble("hinta"),
                    resultSet.getString("kuvaus"),
                    resultSet.getInt("henkilomaara"),
                    resultSet.getString("varustelu"),
                    resultSet.getString("alue") // Tämä on nyt alueen nimi
            );
            mokit.add(mokki);
        }
        return mokit;
    }

    public static void poistaMokkiTietokannasta(Connection connection, String postinro) throws SQLException {
        try {
            // SQL-lause, joka poistaa mökin tiedot tietokannasta
            String deleteMokkiSQL = "DELETE FROM mokki WHERE postinro = ?";
            PreparedStatement deleteMokkiStatement = connection.prepareStatement(deleteMokkiSQL);
            deleteMokkiStatement.setString(1, postinro);
            int rowsAffected = deleteMokkiStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Mökin poisto tietokannasta onnistui!");
            } else {
                System.out.println("Mökin poistaminen tietokannasta epäonnistui. Mökkiä ei löytynyt.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}