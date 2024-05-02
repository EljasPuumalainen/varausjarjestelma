package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Alue {

    private final int alueId;
    private final String nimi;

    public Alue(int alueId, String nimi) {
        this.alueId = alueId;
        this.nimi = nimi;
    }

    public int getAlueId() {
        return alueId;
    }

    public String getNimi() {
        return nimi;
    }
    public IntegerProperty getAlueIdProperty() {
        return new SimpleIntegerProperty(alueId);
    }
    public StringProperty getNimiProperty() {
        return new SimpleStringProperty(nimi);
    }



    public static ObservableList<Alue> haeAlueetTietokannasta(Connection connection) throws SQLException {
        ObservableList<Alue> alueet = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM alue");
        while (resultSet.next()) {
            int alueId = resultSet.getInt("alue_id");
            String nimi = resultSet.getString("nimi");
            Alue alue = new Alue(alueId, nimi);
            alueet.add(alue);
        }
        return alueet;
    }
}
