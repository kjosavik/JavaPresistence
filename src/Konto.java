import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Created by Kjosavik on 3/3/2017.
 */

@Entity


public class Konto implements Serializable{

    @Id
    private int kontonummer;
    private double saldo;
    private String eier;

    @Version
    private int vers;

    public Konto(){}

    public Konto(int kontonummer, double saldo, String eier) {
        this.kontonummer = kontonummer;
        this.saldo = saldo;
        this.eier = eier;
        vers = 0;
    }

    public int getVers(){
        return vers;
    }

    public boolean oppdaterVers(int versDao){
        if(vers == versDao){
            vers++;
            return true;
        }
        return false;
    }

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
