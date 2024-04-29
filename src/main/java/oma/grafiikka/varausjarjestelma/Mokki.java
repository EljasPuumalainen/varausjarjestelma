package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Mokki {

        private final String nimi;
        private final String sijainti;
        private final double hinta;
        private final String katuosoite;
        private final String postinro;
        private final String kuvaus;
        private final int henkilomaara;
        private final String varustelu;

        public Mokki(String nimi, String sijainti, double hinta, String katuosoite, String postinro,
                     String kuvaus, int henkilomaara, String varustelu) {
            this.nimi = nimi;
            this.sijainti = sijainti;
            this.hinta = hinta;
            this.katuosoite = katuosoite;
            this.postinro = postinro;
            this.kuvaus = kuvaus;
            this.henkilomaara = henkilomaara;
            this.varustelu = varustelu;
        }

        public String getNimi() {
            return nimi;
        }

        public String getSijainti() {
            return sijainti;
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
            return new SimpleStringProperty(nimi);
        }

        public StringProperty getSijaintiProperty() {
            return new SimpleStringProperty(sijainti);
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
    }