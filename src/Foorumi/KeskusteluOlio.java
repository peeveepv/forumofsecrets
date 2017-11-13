package Foorumi;

class KeskusteluOlio{
    int keskusteluId;
    String nimi;
    String kuvaus;

    public KeskusteluOlio(int id, String nimi, String kuvaus){
        this.keskusteluId = id;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
    }

    public int getKeskusteluId() {
        return keskusteluId;
    }

    public void setKeskusteluId(int keskusteluId) {
        this.keskusteluId = keskusteluId;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String toString() {
        return "Foorumi.KeskusteluOlio{" +
                "keskusteluId=" + keskusteluId +
                ", nimi='" + nimi + '\'' +
                ", kuvaus='" + kuvaus + '\'' +
                '}';
    }
}
