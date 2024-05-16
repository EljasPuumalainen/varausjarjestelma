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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;


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
    private Button lasku = new Button("Laskujen seuranta");

    private TableView<Mokki> mokkiTableView = new TableView<>();
    private ComboBox<String> alueComboBox = new ComboBox<>();
    private ComboBox<String> asiakasCombo = new ComboBox<>();
    private ComboBox<String> mokkiBox = new ComboBox<>();


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
            try {
                katsoVaraukset();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
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

        try {
            // Avaa tietokantayhteys
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", "root", "KISSAmies5");

            // Haetaan alueet tietokannasta
            ObservableList<Alue> alueet = Alue.haeAlueetTietokannasta(connection);

            // Lisää alueiden nimet alueComboBoxiin
            for (Alue alue : alueet) {
                alueComboBox.getItems().add(alue.getNimi());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        lasku.setOnAction(e -> {
            LaskujenSeuranta();
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
                palveluidenHallinta, lasku);
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    public void asiakkaidenHallintaIkkuna() throws ClassNotFoundException {
        Asiakas uusiasiakas = new Asiakas();

        // Luo uusi Borderpane ja textarea
        BorderPane pane = new BorderPane();
        TextArea tiedot = new TextArea();
        tiedot.setEditable(false);
        tiedot.setFont(Font.font(15));

        ListView<String> asiakasLista = new ListView<>(uusiasiakas.asiakas.get());

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
                String enimi = etunimi.getText().trim();
                String sNimi = sukunimi.getText().trim();

                uusiasiakas.kirjoitaPostiTiedot(postinumero.getText(), postitoimipaikka.getText());

                uusiasiakas.kirjoitaAsiakasTiedot(postinumero.getText(), enimi, sNimi,
                        lahiosoite.getText(), sahkoposti.getText(), puhelinnumero.getText());

                uusiasiakas.paivitaAsiakasLista();
                lisaaAsiakasStage.close();
            });

            lisaaAsiakasPane.setAlignment(Pos.CENTER);
            lisaaAsiakasStage.setTitle("Lisää asiakas");
        });

        poistaAsiakas.setOnAction(p -> {
            String nimi = asiakasLista.getSelectionModel().getSelectedItem();
            String[] nimiOsat = nimi.split(" ");

                if (nimiOsat.length >= 2) { // Varmista, että nimi voidaan jakaa etu- ja sukunimeksi
                    String etunimi = nimiOsat[0];
                    String sukunimi = nimiOsat[1];

                    uusiasiakas.poistaAsiakas(etunimi, sukunimi);
                    asiakasLista.getItems().remove(nimi);
                } else {
                    System.out.println("Nimeä ei voi jakaa etu- ja sukunimeksi");
                }
        });



        tarkistaTiedot.setOnAction(t -> {
            String valittu = asiakasLista.getSelectionModel().getSelectedItem();

            String[] nimiOsat = valittu.split(" ");
            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            tiedot.setText(uusiasiakas.haeTiedot(etunimi, sukunimi).getText());

        });
    }

    /**
     * palveluiden hallinta metodi
     */
    public void palveluidenHallintaIkkuna() {
        PalveluidenHallinta palveluidenHallinta1 = new PalveluidenHallinta();
        // Avaa tietokanta yhteys
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", "root", "KISSAmies5");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Luo uusi BorderPane
        BorderPane pane = new BorderPane();
        TextArea tiedot = new TextArea();
        ListView<String> lista = new ListView<>(palveluidenHallinta1.palvelu.get());

        // Luo uusi Stage-olio
        Stage palveluidenHallinta = new Stage();
        Scene kehys = new Scene(pane);

        // Luo buttonit ikkunaan
        Button lisaaPalvelu = new Button("Lisää palvelu");
        Button haePalvelunTiedot1 = new Button("Hae palvelun tiedot");
        Button poistaPalvelu = new Button("Poista palvelu");

        // Luo VBox painikkeille ja aseta ne vasemmalle
        VBox painikeVBox = new VBox(15);
        painikeVBox.setPadding(new Insets(15, 15, 15, 15));
        painikeVBox.getChildren().addAll(lisaaPalvelu, haePalvelunTiedot1, poistaPalvelu);
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
            lisaaMokkiPane.add(new Label(" Palvelu ID: "), 0, 0);
            lisaaMokkiPane.add(new Label("Valitse alue: "), 0, 1);
            lisaaMokkiPane.add(new Label("Palvelun nimi: "), 0,2);
            lisaaMokkiPane.add(new Label("Arvolisäveroton:"), 0, 3);
            lisaaMokkiPane.add(new Label("Kuvaus: "), 0, 4);
            lisaaMokkiPane.add(new Label("Alv: "), 0, 5);





            // Luo textfieldit
            TextField id = new TextField();
            TextField alueId = new TextField();
            TextField palvelunNimi = new TextField();
            TextField kuvaus = new TextField();
            TextField arvolisatonVero = new TextField();
            TextField alv = new TextField();


            // Luo buttonit käyttöliittymään
            Button tallennaPalvelu = new Button("Tallenna");


            // Lisää tiedot ikkunaan
            lisaaMokkiPane.add(id, 1, 0);
            lisaaMokkiPane.add(alueComboBox, 1,1);
            lisaaMokkiPane.add(palvelunNimi, 1,2);
            lisaaMokkiPane.add(arvolisatonVero, 1,3);
            lisaaMokkiPane.add(kuvaus, 1,4);
            lisaaMokkiPane.add(alv, 1, 5);
            lisaaMokkiPane.add(tallennaPalvelu, 1,6);
            lisaaMokkiPane.setAlignment(Pos.CENTER);
            lisaaMokkiStage.setTitle("Lisää palvelu");

            // Luo tallennaPalvelu buttonille toiminto
            tallennaPalvelu.setOnAction(event -> {
                double alvi = Double.parseDouble(arvolisatonVero.getText()) * 0.24;
                double loppusumma = Double.parseDouble(arvolisatonVero.getText()) + alvi;
                palveluidenHallinta1.lisaaPalvelu(Integer.parseInt(id.getText()), alueComboBox.getValue(),
                        palvelunNimi.getText(), kuvaus.getText(), Double.parseDouble(arvolisatonVero.getText()), loppusumma);

                Platform.runLater(() -> {
                    lista.setItems(palveluidenHallinta1.palvelu.get());

                });

                lisaaMokkiStage.close();
            });

        });
        poistaPalvelu.setOnAction(event -> {
            String nimi = lista.getSelectionModel().getSelectedItem();
            if (nimi != null) {
                palveluidenHallinta1.poistaPalvelu(nimi);
                lista.getItems().remove(nimi);
                lista.refresh();
            } else {
                System.out.println("Valitse palvelu, jota haluat poistaa.");
            }


        });
        haePalvelunTiedot1.setOnAction(event -> {
            String nimi = lista.getSelectionModel().getSelectedItem();
            tiedot.setText(palveluidenHallinta1.haePalvelunTiedot(nimi).getText());
        });
    }


    /**
     * Varauksen teko ikkuna
     *
     * @param parentstage
     */
    public void teeVarausIkkuna(Stage parentstage) throws ClassNotFoundException {
        Asiakas nimet = new Asiakas();
        Mokki mokkinimi = new Mokki();
        asiakasCombo.setItems(nimet.haeAsiakasNimet());
        mokkiBox.setItems(mokkinimi.haeMokinNimet());
        VarausikkunaSql varaa = new VarausikkunaSql();
        LaskujenHallinta lasku = new LaskujenHallinta();

        GridPane varauspane = new GridPane();
        varauspane.setVgap(10);
        varauspane.setPadding(new Insets(10));

        TextField nimi = new TextField();
        TextField mokki = new TextField();

        Button vahvista = new Button("Vahvista varaus");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        DatePicker varattuPvm = new DatePicker();
        DatePicker vahvistus = new DatePicker();

        varauspane.add(new Label("Nimi: "), 0, 0);
        varauspane.add(new Label("Mökki: "), 0, 1);
        varauspane.add(new Label("Aloitus pvm: "), 0, 2);
        varauspane.add(new Label("Lopetus pvm: "), 0, 3);
        varauspane.add(new Label("varattu"), 0, 4);
        varauspane.add(new Label("vahvistus"), 0, 5);
        varauspane.add(asiakasCombo, 1, 0);
        varauspane.add(mokkiBox, 1, 1);
        varauspane.add(startDatePicker, 1, 2);
        varauspane.add(endDatePicker, 1, 3);
        varauspane.add(varattuPvm, 1, 4);
        varauspane.add(vahvistus,1,5);
        varauspane.add(vahvista, 0, 6);


        varauspane.setAlignment(Pos.CENTER);

        Scene varaaScene = new Scene(varauspane, 500, 500);
        Stage varaaStage = new Stage();
        varaaStage.setScene(varaaScene);
        varaaStage.initOwner(parentstage); // Aseta pääikkunaksi vanhempi ikkuna
        varaaStage.show();

        vahvista.setOnAction(e -> {

            // Hanki valittu asiakas
            String valittuAsiakasNimi = asiakasCombo.getValue();

            String[] nimiOsat = valittuAsiakasNimi.split(" ");
            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            // Hanki valittu mökki
            String valittuMokinNimi = mokkiBox.getValue();

            // muutetaan datepicker localdate ensin
            LocalDate startdate = startDatePicker.getValue();
            LocalDate enddate = endDatePicker.getValue();
            LocalDate varattudate = varattuPvm.getValue();
            LocalDate vahvistusdate = vahvistus.getValue();

            Timestamp startTimestamp = Timestamp.valueOf(startdate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(enddate.atStartOfDay());
            Timestamp varattuTimestamp = Timestamp.valueOf(varattudate.atStartOfDay());
            Timestamp vahvistusTimestamp = Timestamp.valueOf(vahvistusdate.atStartOfDay());

            // Tarkista, että asiakas ja mökki on valittu
            if (valittuAsiakasNimi != null && valittuMokinNimi != null) {
                // Hanki asiakkaan ja mökin ID:t
                int asiakasId = nimet.haeAsiakasIdNimella(etunimi, sukunimi);
                int mokkiId = mokkinimi.haeMokkiIdNimella(valittuMokinNimi);

                double summa = lasku.haeMokinHinta(mokkiId);

                // Tarkista, että asiakas ja mökki löytyivät tietokannasta
                if (asiakasId != -1 && mokkiId != -1) {
                    // Kutsu metodia, joka lisää varauksen tietokantaan annetuilla tiedoilla
                    varaa.lisaaVarausTietokantaan(asiakasId, mokkiId, varattuTimestamp, vahvistusTimestamp,
                            startTimestamp, endTimestamp);
                    //lasku.luoLasku(etunimi, sukunimi, summa);
                } else {
                    System.out.println("Asiakasta tai mökkiä ei löytynyt tietokannasta.");
                }
            } else {
                System.out.println("Valitse asiakas ja mökki ennen varauksen vahvistamista.");
            }


        });

    }

    /**
     * katsoVaraukset metodi
     * pystyy katsomaan varausten tietoja
     * varaukset avautuu listviewh näkymään
     *
     * @author Eljas
     */
    public void katsoVaraukset() throws ClassNotFoundException {
        Asiakas asiakasnimi = new Asiakas();
        VarausikkunaSql varauksia = new VarausikkunaSql();
        LaskujenHallinta laskuja = new LaskujenHallinta();
        Mokki mokki = new Mokki();
        BorderPane paneeli = new BorderPane();
        TextArea tiedot = new TextArea();
        tiedot.setEditable(false);
        ListView<String> lista = new ListView<>();
        lista.setItems(varauksia.varaus.get());

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15, 15, 15, 15));

        Button poistavaraus = new Button("poista varaus");
        Button tarkistaTiedot = new Button("tarkista tiedot");
        Button teevaraus = new Button("tee varaus");
        Button luolasku = new Button("luo lasku");


        vbox.getChildren().addAll(teevaraus, poistavaraus, tarkistaTiedot, luolasku);

        paneeli.setRight(vbox);
        paneeli.setLeft(lista);
        paneeli.setCenter(tiedot);

        Scene varausIkkuna = new Scene(paneeli);
        Stage varausStage = new Stage();

        varausStage.setScene(varausIkkuna);
        varausStage.show();
        varausStage.setTitle("Varausten hallinta");

        teevaraus.setOnAction(e -> {
            try {
                teeVarausIkkuna(varausStage);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        tarkistaTiedot.setOnAction(t ->{

            String valittuAsiakasNimi = lista.getSelectionModel().getSelectedItem();

            String[] nimiOsat = valittuAsiakasNimi.split(" ");

            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            int id = varauksia.haeVarausId(etunimi, sukunimi);

            tiedot.setText(varauksia.haeVarauksenTiedot(id).getText());
        });

        /*luolasku.setOnAction(l -> {
            String valittu = lista.getSelectionModel().getSelectedItem();

            String[] nimiOsat = valittu.split(" ");

            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            int varausId = varauksia.haeVarausId(etunimi, sukunimi);
            double summa = laskuja.haeMokinHinta(mokki.haeMokkiIdNimella(mokkiBox.getValue()));
            double alv;
            int i = 0;

            laskuja.LaskuTauluun(i++,varausId, summa*1.24,false ,summa);
        });*/


    }
    /**
     * Mökkien hallinta stage ja sen ominaisuudet/toiminnot
     */
    public void mokkienHallinta() {
        try {
            // Avaa tietokantayhteys
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", "root", "KISSAmies5");

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

            TableColumn<Mokki, Double> hintaColumn = new TableColumn<>("Päivä hinta");
            hintaColumn.setCellValueFactory(cellData -> cellData.getValue().getHintaProperty().asObject());

            TableColumn<Mokki, String> kuvausColumn = new TableColumn<>("Kuvaus");
            kuvausColumn.setCellValueFactory(cellData -> cellData.getValue().getKuvausProperty());

            TableColumn<Mokki, String> henkilomaaraColumn = new TableColumn<>("Henkilömäärä");
            henkilomaaraColumn.setCellValueFactory(cellData -> cellData.getValue().getHenkilomaaraProperty());

            TableColumn<Mokki, String> varusteluColumn = new TableColumn<>("Varustelu");
            varusteluColumn.setCellValueFactory(cellData -> cellData.getValue().getVarusteluProperty());

            mokkiTableView.getColumns().addAll(postinumeroColumn, nimiColumn, katuosoiteColumn, hintaColumn, kuvausColumn,
                    henkilomaaraColumn, varusteluColumn);

            // Haetaan tiedot tietokannasta
            ObservableList<Mokki> mokit = Mokki.haeMokitTietokannasta(connection);
            mokkiTableView.setItems(mokit);

            ObservableList<Alue> alueet = Alue.haeAlueetTietokannasta(connection);
            alueComboBox.getItems().clear(); // Tyhjennetään ComboBox ensin
            for (Alue alue : alueet) {
                alueComboBox.getItems().add(alue.getNimi());
            }




            // Aseta TableView BorderPaneen
            pane.setCenter(mokkiTableView);

            Button lisaaMokki = new Button("Lisää");
            Button poistaNappi = new Button("Poista");

            // Aseta napit VBoxiin
            VBox buttonBox = new VBox(lisaaMokki, poistaNappi);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(15, 15, 15, 15));
            buttonBox.setSpacing(10);

            // Aseta VBox vasempaan reunaan
            pane.setLeft(buttonBox);

            mokkiIkkuna.show();

            //Alueiden combobox


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
                lisaaMokkiPane.add(new Label("Päivä hinta: "), 0, 3);
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
                            mokkiTableView.setItems(Mokki.haeMokitTietokannasta(connection));lisaaMokkiStage.close();
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", "root", "KISSAmies5");

            Stage alueIkkuna = new Stage();
            BorderPane pane = new BorderPane();
            Scene scene = new Scene(pane, 900, 400);
            alueIkkuna.setScene(scene);
            alueIkkuna.setTitle("Alueiden hallinta");

            TableView alueTableView = new TableView<>();


            TableColumn<Alue, String> nimiColumn = new TableColumn<>("Alueen nimi");
            nimiColumn.setCellValueFactory(cellData -> cellData.getValue().nimiProperty());

            alueTableView.getColumns().addAll(nimiColumn);

            // Haetaan tiedot tietokannasta
            ObservableList<Alue> alueet = Alue.haeAlueetTietokannasta(connection);
            alueTableView.setItems(alueet);

            ObservableList<String> alueetNimet = FXCollections.observableArrayList();
            for (Alue alue : alueet) {
                alueetNimet.add(alue.getNimi());
            }
            alueComboBox.setItems(alueetNimet);


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

                pane1.add(new Label("Nimi: "), 0, 0);

                TextField nimi = new TextField();

                pane1.add(nimi, 1, 0);

                pane1.setAlignment(Pos.CENTER);
                lisaaAlueStage.setTitle("Lisää Alue");

                Button lisaaAlueBT = new Button("Lisää Alue");

                pane1.add(lisaaAlueBT, 1,1);

                lisaaAlueBT.setOnAction(ev -> {
                    try {
                        // Tarkista, että nimi-kenttä on täytetty ennen tietojen tallentamista
                        if (!nimi.getText().isEmpty()) {
                            // Luo uusi Alue-olio syötetyllä nimellä
                            Alue uusiAlue = new Alue(nimi.getText());

                            // Lisää Alue tietokantaan
                            uusiAlue.lisaaAlueTietokantaan(connection);

                            alueComboBox.getItems().add(uusiAlue.getNimi());


                            // Päivitä TableView hakeaksesi uudet tiedot tietokannasta
                            alueTableView.setItems(Alue.haeAlueetTietokannasta(connection));
                            lisaaAlueStage.close();
                        } alueTableView.setItems(Alue.haeAlueetTietokannasta(connection));

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void LaskujenSeuranta(){

        LaskujenHallinta laskuja = new LaskujenHallinta();
        laskuja.teeKysely();

        Stage laskuStage = new Stage();
        BorderPane laskuPaneeli = new BorderPane();

        ListView<String> laskuLista = new ListView<>(laskuja.laskut.get());
        TextField haeNimella = new TextField("Hae lasku nimellä");
        Button tarkista = new Button("katso tiedot");
        VBox boksi = new VBox(haeNimella, tarkista);
        boksi.setPadding(new Insets(160,15,15,15));
        laskuPaneeli.setRight(boksi);

        laskuPaneeli.setLeft(laskuLista);

        TextArea laskut = new TextArea();
        laskuPaneeli.setCenter(laskut);

        laskuLista.scrollTo(haeNimella.getText());

        Scene laskuScene = new Scene(laskuPaneeli,900, 500);
        laskuStage.setScene(laskuScene);
        laskuStage.setTitle("laskujenhallinta ikkuna");
        laskuStage.show();

        tarkista.setOnAction(e -> {
            String valittu = laskuLista.getSelectionModel().getSelectedItem();

            String[] nimiOsat = valittu.split(" ");
            String etunimi = nimiOsat[0];
            String sukunimi = nimiOsat[1];

            haeNimella.setText(laskuja.haeLaskuTiedot(etunimi, sukunimi).getText());
        });
    }

}