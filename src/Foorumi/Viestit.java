package Foorumi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Viestit {
    private int id;
    private int kirjoittaja;
    private int keskusteluid;
    private String otsikko;
    private String viesti;
    private int vastaus = 0;
    private Date kirjoitettu;
    private Date viimeksimuutettu;

    public Viestit(int id, int kirjoittaja, int keskusteluid, String otsikko, String viesti, int vastaus, Date kirjoitettu) {
        this.id = id;
        this.kirjoittaja = kirjoittaja;
        this.keskusteluid = keskusteluid;
        this.otsikko = otsikko;
        this.viesti = viesti;
        this.vastaus = vastaus;
        this.kirjoitettu = kirjoitettu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKirjoittaja() {
        return kirjoittaja;
    }

    public void setKirjoittaja(int kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }

    public int getKeskusteluid() {
        return keskusteluid;
    }

    public void setKeskusteluid(int keskusteluid) {
        this.keskusteluid = keskusteluid;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public String getViesti() {
        return viesti;
    }

    public void setViesti(String viesti) {
        this.viesti = viesti;
    }

    public int getVastaus() {
        return vastaus;
    }

    public void setVastaus(int vastaus) {
        this.vastaus = vastaus;
    }

    public Date getKirjoitettu() {
        return kirjoitettu;
    }

    public void setKirjoitettu(Date kirjoitettu) {
        this.kirjoitettu = kirjoitettu;
    }
}
