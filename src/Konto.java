/**
 * Created by Kjosavik on 3/3/2017.
 */
public class Konto {

    private int kontonummer;
    private double saldo;
    private String eier;

    public String getEier() {
        return eier;
    }

    public void setEier(String eier) {
        this.eier = eier;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getKontonummer() {

        return kontonummer;
    }

    public void setKontonummer(int kontonummer) {
        this.kontonummer = kontonummer;
    }

    public void trekk(double belop){
        saldo -= belop;
    }

    public String toString (){
        return "Kontonummer: " + kontonummer + ", Saldo: " + saldo + " Eier: " + eier;
    }
}
