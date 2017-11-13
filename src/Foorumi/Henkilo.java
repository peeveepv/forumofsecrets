package Foorumi;

public class Henkilo {

    private int hloid;
    private String kayttajanimi, nimimerkki, kuvaus, rooli;

    public Henkilo(int hloid, String kayttajanimi, String nimimerkki, String kuvaus, String rooli) {
        this.hloid = hloid;
        this.kayttajanimi = kayttajanimi;
        this.nimimerkki = nimimerkki;
        this.kuvaus = kuvaus;
        this.rooli = rooli;
    }

    public String getRooli() {

        return rooli;
    }

    public String getKuvaus() {

        return kuvaus;
    }

    public String getNimimerkki() {

        return nimimerkki;
    }

    public String getKayttajanimi() {

        return kayttajanimi;
    }

    public int getHloid() {

        return hloid;
    }
}
