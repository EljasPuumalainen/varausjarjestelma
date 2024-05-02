package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Mokki {

    private final String postinro;
    private final String mokkinimi;
    private final String katuosoite;
    private final double hinta;
    private final String kuvaus;
    private final int henkilomaara;
    private final String varustelu;

    public Mokki(String postinro, String mokkinimi, String katuosoite, double hinta,
                 String kuvaus, int henkilomaara, String varustelu) {
        this.postinro = postinro;
        this.mokkinimi = mokkinimi;
        this.katuosoite = katuosoite;
        this.hinta = hinta;
        this.kuvaus = kuvaus;
        this.henkilomaara = henkilomaara;
        this.varustelu = varustelu;
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
        String sql = "INSERT INTO mokki (postinro, mokkinimi, katuosoite, hinta, kuvaus, henkilomaara, varustelu) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, mokki.getPostinro());
        preparedStatement.setString(2, mokki.getNimi());
        preparedStatement.setString(3, mokki.getKatuosoite());
        preparedStatement.setDouble(4, mokki.getHinta());
        preparedStatement.setString(5, mokki.getKuvaus());
        preparedStatement.setInt(6, mokki.getHenkilomaara());
        preparedStatement.setString(7, mokki.getVarustelu());
        preparedStatement.executeUpdate();
    }

    public static ObservableList<Mokki> haeMokitTietokannasta(Connection connection) throws SQLException {
        ObservableList<Mokki> mokit = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM mokki");
        while (resultSet.next()) {
            Mokki mokki = new Mokki(
                    resultSet.getString("postinro"),
                    resultSet.getString("mokkinimi"), // Muutettu "nimi" -> "mokkinimi"
                    resultSet.getString("katuosoite"),
                    resultSet.getDouble("hinta"),
                    resultSet.getString("kuvaus"),
                    resultSet.getInt("henkilomaara"),
                    resultSet.getString("varustelu")
            );
            mokit.add(mokki);
        }
        return mokit;
    }

}