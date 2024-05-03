package oma.grafiikka.varausjarjestelma;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.SelectionMode;



public class Kayttoliittyma extends Application {

    private Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    private Button varaustenHallinta = new Button("Varausten hallinta");
    private Button asikkaidenHallinta = new Button("Asiakkaiden hallinta");
    private Button mokkienHallinta = new Button("Mökkien hallinta");
    private Button alueidenHallinta = new Button("Alueiden hallinta");
    private Button palveluidenHallinta = new Button("Palveluiden hallinta");
    private TableView<Mokki> mokkiTableView = new TableView<>();

    @Override
    public void start(Stage primaryStage) {

        BorderPane pane = new BorderPane();

        Scene scene = new Scene(pane, 1000, 500);
        primaryStage.setTitle("Varausjärjestelmä");
        primaryStage.setScene(scene);
        primaryStage.show();

        Text text = new Text("Valitse mitä haluat tehdä?");
        text.setFont(Font.font(30));

        BorderPane.setAlignment(text, Pos.TOP_CENTER);
        BorderPane.setMargin(text, new Insets(20, 0, 0, 0));


        pane.setTop(text);
        pane.setCenter(getHbox());

        //Varausten hallinta ikkuna
        varaustenHallinta.setOnAction(e -> {
            //primaryStage.close();
            katsoVaraukset();
        });

        //Mokkien hallinta ikkua
        mokkienHallinta.setOnMouseClicked(e -> {
            mokkienHallinta();
        });
        // Asiakkaiden hallinta ikkuna
        asikkaidenHallinta.setOnAction(e -> {
            try {
                asiakkaidenHallintaIkkuna();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Palveluiden hallinta ikkuna
        palveluidenHallinta.setOnAction(e -> {
            palveluidenHallintaIkkuna();
        });

        alueidenHallinta.setOnAction(e -> {
            alueidenHallintaIkkuna();
        });
    }

    /**
     * getHbox metodi
     * hboxiin asetetaan buttonit
     * buttoneista päästää uusiin ikkunoihin
     *
     * @return hBox
     * @author Konsta
     */
    public HBox getHbox() {

        HBox hBox = new HBox(15);

        hBox.getChildren().addAll(varaustenHallinta, asikkaidenHallinta, mokkienHallinta, alueidenHallinta,
                palveluidenHallinta);
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    public void asiakkaidenHallintaIkkuna() throws ClassNotFoundException {
        Asiakas asiakas = new Asiakas();

        // Luo uusi Borderpane ja textarea
        BorderPane pane = new BorderPane();
        TextArea tiedot = new TextArea();
        tiedot.setEditable(false);
        tiedot.setFont(Font.font(15));

        ListView<String> asiakasLista = new ListView<>(asiakas.asiakas.get());

        // Luo uusi Stage-olio
        Stage asiakasHallinta = new Stage();
        Scene kehys = new Scene(pane);

        // Luo buttonit ikkunaan
        Button lisaaAsiakas = new Button("Lisää asiakas");

        Button poistaAsiakas = new Button("Poista asiakas");
        Button tarkistaTiedot = new Button("Tarkista tiedot");

        // Luo VBox painikkeille ja aseta ne vasemmalle
        VBox painikeVBox = new VBox(15);
        painikeVBox.setPadding(new Insets(15, 15, 15, 15));
        painikeVBox.getChildren().addAll(lisaaAsiakas, poistaAsiakas, tarkistaTiedot);
        pane.setRight(painikeVBox);

        // Aseta TextArea ja ListView BorderPaneen
        pane.setCenter(tiedot);
        pane.setLeft(asiakasLista);

        // Aseta uusi Scene Stageen ja näytä ikkuna
        asiakasHallinta.setScene(kehys);
        asiakasHallinta.show();
        asiakasHallinta.setTitle("Asiakkaiden hallinta");

        // Luo lisaaAsiakas buttonille toiminto
        lisaaAsiakas.setOnMouseClicked(e -> {
            Stage lisaaAsiakasStage = new Stage();
            GridPane lisaaAsiakasPane = new GridPane();
            Scene lisaaAsiakasScene = new Scene(lisaaAsiakasPane, 300,300);
            lisaaAsiakasStage.setScene(lisaaAsiakasScene);
            lisaaAsiakasStage.show();
            lisaaAsiakasPane.setVgap(5);

            // Lisää labelit ikkunaan
            lisaaAsiakasPane.add(new Label("Etunimi: "), 0,0);
            lisaaAsiakasPane.add(new Label("Sukunimi:"), 0, 1);
            lisaaAsiakasPane.add(new Label("Lähiosoite: "), 0, 2);
            lisaaAsiakasPane.add(new Label("Postinumero: "), 0, 3);
            lisaaAsiakasPane.add(new Label("postitoimipaikka: "),0, 4);
            lisaaAsiakasPane.add(new Label("Sähköposti: "), 0, 5);
            lisaaAsiakasPane.add(new Label("Puhelinnumero: "), 0, 6);

            // Luo textfieldit
            TextField etunimi = new TextField();
            TextField sukunimi = new TextField();
            TextField lahiosoite = new TextField();

            TextField postinumero = new TextField();
            TextField postitoimipaikka = new TextField();

            TextField sahkoposti = new TextField();
            TextField puhelinnumero = new TextField();

            //Luo button
            Button tallennaAsiakas = new Button("Tallenna");

            // Lisää textfieldit ja button ikkunaan
            lisaaAsiakasPane.add(etunimi, 1, 0);
            lisaaAsiakasPane.add(sukunimi, 1,1);
            lisaaAsiakasPane.add(lahiosoite, 1,2);
            lisaaAsiakasPane.add(postinumero, 1, 3);
            lisaaAsiakasPane.add(postitoimipaikka, 1, 4);
            lisaaAsiakasPane.add(sahkoposti, 1,5);
            lisaaAsiakasPane.add(puhelinnumero, 1, 6);
            lisaaAsiakasPane.add(tallennaAsiakas, 1, 7);

            tallennaAsiakas.setOnAction(a -> {

                asiakas.kirjoitaPostiTiedot(postinumero.getText(), postitoimipaikka.getText());

                asiakas.kirjoitaAsiakasTiedot(postinumero.getText(), etunimi.getText(), sukunimi.getText(),
                        lahiosoite.getText(), sahkoposti.getText(), puhelinnumero.getText());

                Platform.runLater(() -> {
                    asiakasLista.setItems(asiakas.asiakas.get());
                });

                asiakasHallinta.show();
                lisaaAsiakasStage.close();

            });
            lisaaAsiakasPane.setAlignment(Pos.CENTER);
            lisaaAsiakasStage.setTitle("Lisää asiakas");
        });

        poistaAsiakas.setOnAction(p -> {

            String nimi = asiakasLista.getSelectionModel().getSelectedItem();

            String[] nimiOsat = nimi.split(" ");
            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            asiakas.poistaAsiakas(etunimi, sukunimi);

            asiakasLista.getItems().remove(nimi);
        });

        tarkistaTiedot.setOnAction(t -> {
            String valittu = asiakasLista.getSelectionModel().getSelectedItem();

            String[] nimiOsat = valittu.split(" ");
            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            tiedot.setText(asiakas.haeTiedot(etunimi, sukunimi).getText());

        });
    }

    /**
     * palveluiden hallinta metodi
     */
    public void palveluidenHallintaIkkuna() {
        // Luo uusi BorderPane
        BorderPane pane = new BorderPane();
        TextArea tiedot = new TextArea();
        ListView<String> lista = new ListView<>();

        // Luo uusi Stage-olio
        Stage palveluidenHallinta = new Stage();
        Scene kehys = new Scene(pane);

        // Luo buttonit ikkunaan
        Button lisaaPalvelu = new Button("Lisää palvelu");
        Button muokkaaPalvelua = new Button("Muokkaa palveluita");
        Button poistaPalvelu = new Button("Poista palvelu");

        // Luo VBox painikkeille ja aseta ne vasemmalle
        VBox painikeVBox = new VBox(15);
        painikeVBox.setPadding(new Insets(15, 15, 15, 15));
        painikeVBox.getChildren().addAll(lisaaPalvelu, muokkaaPalvelua, poistaPalvelu);
        pane.setRight(painikeVBox);

        // Aseta TextArea ja ListView BorderPaneen
        pane.setCenter(tiedot);
        pane.setLeft(lista);


        // Aseta uusi Scene Stageen ja näytä ikkuna
        palveluidenHallinta.setScene(kehys);
        palveluidenHallinta.show();
        palveluidenHallinta.setTitle("Palveluiden hallinta");

        // Luo lisaaPalvelu buttonille toiminto
        lisaaPalvelu.setOnMouseClicked(e -> {
            Stage lisaaMokkiStage = new Stage();
            GridPane lisaaMokkiPane = new GridPane();
            Scene lisaaMokkiScene = new Scene(lisaaMokkiPane, 300,300);
            lisaaMokkiStage.setScene(lisaaMokkiScene);
            lisaaMokkiStage.show();
            lisaaMokkiPane.setVgap(5);

            // Luo labelit
            lisaaMokkiPane.add(new Label("Palvelun nimi: "), 0,0);
            lisaaMokkiPane.add(new Label("Hinta:"), 0, 1);

            // Luo textfieldit
            TextField palvelunNimi = new TextField();
            TextField hinta = new TextField();


            Button tallennaPalvelu = new Button("Tallenna");

            // Lisää tiedot ikkunaan
            lisaaMokkiPane.add(palvelunNimi, 1,0);
            lisaaMokkiPane.add(hinta, 1,1);
            lisaaMokkiPane.add(tallennaPalvelu, 1,2);

            lisaaMokkiPane.setAlignment(Pos.CENTER);
            lisaaMokkiStage.setTitle("Lisää palvelu");

        });
    }

    /**
     * Varauksen teko ikkuna
     *
     * @param parentstage
     */
    public void teeVarausIkkuna(Stage parentstage) {
        GridPane varauspane = new GridPane();
        varauspane.setVgap(10);
        varauspane.setPadding(new Insets(10));

        TextField tf1 = new TextField();
        TextField tf2 = new TextField();

        Button vahvista = new Button("Vahvista varaus");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        varauspane.add(new Label("Nimi: "), 0, 0);
        varauspane.add(new Label("Mökki: "), 0, 1);
        varauspane.add(new Label("Aloitus pvm: "), 0, 2);
        varauspane.add(new Label("Lopetus pvm: "), 0, 3);
        varauspane.add(tf1, 1, 0);
        varauspane.add(tf2, 1, 1);
        varauspane.add(startDatePicker, 1, 2);
        varauspane.add(endDatePicker, 1, 3);
        varauspane.add(vahvista, 0, 4);


        varauspane.setAlignment(Pos.CENTER);

        Scene varaaScene = new Scene(varauspane, 500, 500);
        Stage varaaStage = new Stage();
        varaaStage.setScene(varaaScene);
        varaaStage.initOwner(parentstage); // Aseta pääikkunaksi vanhempi ikkuna
        varaaStage.show();

        vahvista.setOnAction(e -> {

            Alert ilmoitus = new Alert(Alert.AlertType.CONFIRMATION);
            ilmoitus.setContentText("KIITTI FYRKOISTA, SENKIN TYHMÄ");
            ilmoitus.setHeaderText("TYHMÄ");
            ilmoitus.setTitle("AHHAHHAHAHAHHAHHA");
            ilmoitus.show();
            varaaStage.close();

        });
    }

    /**
     * katsoVaraukset metodi
     * pystyy katsomaan varausten tietoja
     * varaukset avautuu listviewh näkymään
     *
     * @author Eljas
     */
    public void katsoVaraukset() {
        BorderPane paneeli = new BorderPane();
        TextArea tiedot = new TextArea();
        ListView<String> lista = new ListView<>();

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15, 15, 15, 15));

        Button poistavaraus = new Button("poista varaus");
        Button muokkaavarausta = new Button("muokkaa varausta");
        Button teevaraus = new Button("tee varaus");


        vbox.getChildren().addAll(teevaraus, poistavaraus, muokkaavarausta);

        paneeli.setRight(vbox);
        paneeli.setLeft(lista);
        paneeli.setCenter(tiedot);

        Scene varausIkkuna = new Scene(paneeli);
        Stage varausStage = new Stage();

        varausStage.setScene(varausIkkuna);
        varausStage.show();
        varausStage.setTitle("Varausten hallinta");

        teevaraus.setOnAction(e -> {
            teeVarausIkkuna(varausStage);
        });
    }

    /**
     * Mökkien hallinta stage ja sen ominaisuudet/toiminnot
     */
    public void mokkienHallinta() {
        try {
            // Avaa tietokantayhteys
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", "root", "root");

            Stage mokkiIkkuna = new Stage();
            BorderPane pane = new BorderPane();
            Scene scene = new Scene(pane, 900, 400);
            mokkiIkkuna.setScene(scene);
            mokkiIkkuna.setTitle("Mökkien hallinta");

            mokkiTableView = new TableView<>();

            TableColumn<Mokki, String> postinumeroColumn = new TableColumn<>("Postinumero");
            postinumeroColumn.setCellValueFactory(cellData -> cellData.getValue().getPostinroProperty());

            TableColumn<Mokki, String> nimiColumn = new TableColumn<>("Mökin nimi");
            nimiColumn.setCellValueFactory(cellData -> cellData.getValue().getNimiProperty());

            TableColumn<Mokki, String> katuosoiteColumn = new TableColumn<>("Katuosoite");
            katuosoiteColumn.setCellValueFactory(cellData -> cellData.getValue().getKatuosoiteProperty());

            TableColumn<Mokki, Double> hintaColumn = new TableColumn<>("Hinta");
            hintaColumn.setCellValueFactory(cellData -> cellData.getValue().getHintaProperty().asObject());

            TableColumn<Mokki, String> kuvausColumn = new TableColumn<>("Kuvaus");
            kuvausColumn.setCellValueFactory(cellData -> cellData.getValue().getKuvausProperty());

            TableColumn<Mokki, String> henkilomaaraColumn = new TableColumn<>("Henkilömäärä");
            henkilomaaraColumn.setCellValueFactory(cellData -> cellData.getValue().getHenkilomaaraProperty());

            TableColumn<Mokki, String> varusteluColumn = new TableColumn<>("Varustelu");
            varusteluColumn.setCellValueFactory(cellData -> cellData.getValue().getVarusteluProperty());

            mokkiTableView.getColumns().addAll(nimiColumn, katuosoiteColumn, postinumeroColumn, hintaColumn, henkilomaaraColumn, varusteluColumn);

            // Haetaan tiedot tietokannasta
            ObservableList<Mokki> mokit = Mokki.haeMokitTietokannasta(connection);
            mokkiTableView.setItems(mokit);

            // Aseta TableView BorderPaneen
            pane.setCenter(mokkiTableView);

            Button lisaaMokki = new Button("Lisää");
            Button muokkaaNappi = new Button("Muokkaa");
            Button poistaNappi = new Button("Poista");

            // Aseta napit VBoxiin
            VBox buttonBox = new VBox(lisaaMokki, muokkaaNappi, poistaNappi);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(15, 15, 15, 15));
            buttonBox.setSpacing(10);

            // Aseta VBox vasempaan reunaan
            pane.setLeft(buttonBox);

            mokkiIkkuna.show();

            //Alueiden combobox
            ComboBox<String> alueComboBox = new ComboBox<>();
            ObservableList<String> alueet = FXCollections.observableArrayList("Ruka", "Tahko", "Ylläs");
            alueComboBox.setItems(alueet);

            lisaaMokki.setOnMouseClicked(e -> {
                Stage lisaaMokkiStage = new Stage();
                GridPane lisaaMokkiPane = new GridPane();
                Scene lisaaMokkiScene = new Scene(lisaaMokkiPane, 300, 300);
                lisaaMokkiStage.setScene(lisaaMokkiScene);
                lisaaMokkiStage.show();
                lisaaMokkiPane.setVgap(5);

                lisaaMokkiPane.add(new Label("Postinumero: "), 0, 0);
                lisaaMokkiPane.add(new Label("Nimi:"), 0, 1);
                lisaaMokkiPane.add(new Label("Katuosoite: "), 0, 2);
                lisaaMokkiPane.add(new Label("Hinta: "), 0, 3);
                lisaaMokkiPane.add(new Label("Kuvaus: "), 0, 4);
                lisaaMokkiPane.add(new Label("Henkilömäärä: "), 0, 5);
                lisaaMokkiPane.add(new Label("Varustelu: "), 0, 6);

                TextField nimi = new TextField();
                TextField katuosoite = new TextField();
                TextField postinumero = new TextField();
                TextField hinta = new TextField();
                TextField kuvaus = new TextField();
                TextField henkilomaara = new TextField();
                TextField varustelu = new TextField();

                Button lisaaMokki2 = new Button("Lisää mökki");


                lisaaMokkiPane.add(new Label("Alue"), 0, 7);
                lisaaMokkiPane.add(alueComboBox, 1, 7);

                lisaaMokkiPane.add(postinumero, 1, 0);
                lisaaMokkiPane.add(nimi, 1, 1);
                lisaaMokkiPane.add(katuosoite, 1, 2);
                lisaaMokkiPane.add(hinta, 1, 3);
                lisaaMokkiPane.add(kuvaus, 1, 4);
                lisaaMokkiPane.add(henkilomaara, 1, 5);
                lisaaMokkiPane.add(varustelu, 1, 6);
                lisaaMokkiPane.add(lisaaMokki2, 1, 8);

                lisaaMokkiPane.setAlignment(Pos.CENTER);
                lisaaMokkiStage.setTitle("Lisää mökki");

                // Luo lisaaMokki buttonille toiminto
                lisaaMokki2.setOnAction(ev -> {
                    try {
                        // Tarkista, että kaikki kentät ovat täytettyjä ennen tietojen tallentamista
                        if (!postinumero.getText().isEmpty() && !nimi.getText().isEmpty()
                                && !katuosoite.getText().isEmpty() && !hinta.getText().isEmpty()
                                && !kuvaus.getText().isEmpty() && !henkilomaara.getText().isEmpty()
                                && !varustelu.getText().isEmpty() && alueComboBox.getValue() != null) {

                            // Luo uusi Mokki-olio syötetyillä tiedoilla
                            Mokki uusiMokki = new Mokki(
                                    postinumero.getText(),
                                    nimi.getText(),
                                    katuosoite.getText(),
                                    Double.parseDouble(hinta.getText()),
                                    kuvaus.getText(),
                                    Integer.parseInt(henkilomaara.getText()),
                                    varustelu.getText(),
                                    alueComboBox.getSelectionModel().getSelectedItem() // Lisätään valittu alue Mokki-oliolle
                            );

                            // Lisää Mokki tietokantaan
                            Mokki.lisaaMokkiTietokantaan(connection, uusiMokki);

                            // Päivitä TableView hakeaksesi uudet tiedot tietokannasta
                            mokkiTableView.setItems(Mokki.haeMokitTietokannasta(connection));
                        }  mokkiTableView.setItems(Mokki.haeMokitTietokannasta(connection));

                    } catch (SQLException | NumberFormatException ex) {
                        ex.printStackTrace();
                        // Voit lisätä tässä käyttöliittymässä ilmoituksen virheestä, esim. Alert
                    }
                });
            });

            poistaNappi.setOnAction(e -> {
                // Hae valittu mökki
                Mokki valittuMokki = mokkiTableView.getSelectionModel().getSelectedItem();
                if (valittuMokki != null) {
                    try {
                        // Kutsu poistometodia ja välitä tietokantayhteys sekä valitun mökin postinumero
                        Mokki.poistaMokkiTietokannasta(connection, valittuMokki.getPostinro());

                        // Poista valittu mökki myös TableView:sta
                        mokkiTableView.getItems().remove(valittuMokki);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Käsittele virhe
                    }
                } else {
                    // Jos mitään ei ole valittu, näytä ilmoitus käyttäjälle
                    System.out.println("Valitse ensin mökki, jonka haluat poistaa.");
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void alueidenHallintaIkkuna() {

        try {
            // Avaa tietokantayhteys
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", "root", "root");

            Stage alueIkkuna = new Stage();
            BorderPane pane = new BorderPane();
            Scene scene = new Scene(pane, 900, 400);
            alueIkkuna.setScene(scene);
            alueIkkuna.setTitle("Alueiden hallinta");

            TableView alueTableView = new TableView<>();

            TableColumn<Alue, Integer> alueIDColumn = new TableColumn<>("Alue ID");
            alueIDColumn.setCellValueFactory(cellData -> cellData.getValue().getAlueIdProperty().asObject());

            TableColumn<Alue, String> nimiColumn = new TableColumn<>("Alueen nimi");
            nimiColumn.setCellValueFactory(cellData -> cellData.getValue().getNimiProperty());

            alueTableView.getColumns().addAll(alueIDColumn, nimiColumn);

            // Haetaan tiedot tietokannasta
            ObservableList<Alue> alueet = Alue.haeAlueetTietokannasta(connection);
            alueTableView.setItems(alueet);


            // Aseta TableView näkyväksi
            pane.setCenter(alueTableView);

            alueIkkuna.show();

            Button lisaaAlue = new Button("Lisää");
            Button poistaAlue = new Button("Poista");

            // Aseta napit VBoxiin
            VBox buttonBox = new VBox(lisaaAlue, poistaAlue);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(15, 15, 15, 15));
            buttonBox.setSpacing(10);

            // Aseta VBox vasempaan reunaan
            pane.setLeft(buttonBox);

            lisaaAlue.setOnAction(e -> {
                Stage lisaaAlueStage = new Stage();
                GridPane pane1 = new GridPane();
                Scene lisaaMokkiScene = new Scene(pane1, 300, 300);
                lisaaAlueStage.setScene(lisaaMokkiScene);
                lisaaAlueStage.show();
                pane1.setVgap(5);

                pane1.add(new Label("Alue ID: "), 0, 0);
                pane1.add(new Label("Nimi: "), 0, 1);

                TextField alueID = new TextField();
                TextField nimi = new TextField();

                pane1.add(alueID, 1, 0);
                pane1.add(nimi, 1, 1);

                pane1.setAlignment(Pos.CENTER);
                lisaaAlueStage.setTitle("Lisää Alue");

                Button lisaaAlueBT = new Button("Lisää Alue");

                pane1.add(lisaaAlueBT, 1,2);

                lisaaAlueBT.setOnAction(ev -> {
                    try {
                        // Tarkista, että kaikki kentät ovat täytettyjä ennen tietojen tallentamista
                        if (!alueID.getText().isEmpty() && !nimi.getText().isEmpty()) {
                            // Luo uusi Alue-olio syötetyillä tiedoilla
                            Alue uusiAlue = new Alue(Integer.parseInt(alueID.getText()), nimi.getText());

                            // Lisää Alue tietokantaan
                            uusiAlue.lisaaAlueTietokantaan(connection);

                            // Päivitä TableView hakeaksesi uudet tiedot tietokannasta
                            alueTableView.setItems(Alue.haeAlueetTietokannasta(connection));
                        } alueTableView.setItems(Alue.haeAlueetTietokannasta(connection));

                    } catch (SQLException | NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                });

                poistaAlue.setOnAction(ev -> {
                    Alue valittuAlue = (Alue) alueTableView.getSelectionModel().getSelectedItem();
                    if (valittuAlue != null) {
                        try {
                            valittuAlue.poistaAlueTietokannasta(connection);
                            alueTableView.getItems().remove(valittuAlue);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}