/*
 * Bok.java
 *
 * November 2008, av Tomas Holt
 */

package leksjon.entitet1;

import java.util.*;
import javax.persistence.*;
import java.io.*;

@Entity
@NamedQuery(name="finnAntallBoker", query="SELECT COUNT(o) from Bok o")

public class Bok implements Serializable{
    
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String isbn;
    private String tittel;
    private double pris;
    private String forfatter;
    private int antallSider; 
   
    public Bok(){}//m ha en konstruktor med tom parameterliste ihht JavaBeans standarden
    
    public Bok(String isbn, String tittel, double pris, String forfatter, int antallSider){
        this.isbn = isbn;
        this.tittel = tittel;
        this.pris = pris;
        this.forfatter = forfatter;
        this.antallSider = antallSider;
    }
    
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getTittel(){
        return tittel;
    }
    
    public void setTittel(String tittel){
        this.tittel = tittel;
    }
    
    public double getPris(){
        return pris;
    }
    
    public void setPris(double pris){
        this.pris = pris;
    }
    
    public String getForfatter(){
        return forfatter;
    }
    
    public void setForfatter(String forfatter){
        this.forfatter = forfatter;
    }
    
    public int getAntallSider(){
        return antallSider;
    }
    
    public void setAntallSider(int ant){
        this.antallSider = ant;
    }
    
    public String toString(){
        return "Bok: Isbn: " + isbn + " .Tittel: " + tittel + " .Forfatter " + forfatter + " .Pris: " + pris + " .Antall sider: " + antallSider;
    }
}
