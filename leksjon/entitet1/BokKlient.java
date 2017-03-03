/*
 * BokKlient.java
 *
 * Created on December 8, 2006, 5:03 PM
 */

package leksjon.entitet1;
import java.rmi.*;
import java.util.List;
public class BokKlient {
    public static void main(String args[]) throws Exception{
        String url = "rmi://localhost/BokFasade";
        BokFasade fasade = (BokFasade)Naming.lookup(url);
        //lager en bok med setMetodene i bok
        Bok bok = new Bok();
        bok.setIsbn("ISBN-1");
        bok.setAntallSider(33);
        bok.setForfatter("Lille");
        bok.setPris(300);
        bok.setTittel("Testbok1");
        fasade.lagreNyBok(bok);//lagrer boka

        //lager en ny bok med konstruktor i stedet for setMetodene
        bok = new Bok("ISBN-2","EnTittel",200,"Forfatter",50);//tar alle parametre Id som lages automatisk
        fasade.lagreNyBok(bok);       

        //Skriv ut bøkene som er lagret
        System.out.println("Følgende bøker er lagret i DB:");
        List<Bok> liste = fasade.getAlleBoker();
        for (Bok b : liste){
            System.out.println("---" + b);
        }

        bok = (Bok)liste.get(0);
        bok.setTittel("Endret tittel");
        fasade.endreBok(bok);

        bok = fasade.finnBok(bok.getIsbn());//henter ut boka på nytt
        System.out.println("Bok er nå endret, og blitt slik: " + bok);

        //finner antall bøker i db
        int antall = fasade.getAntallBoker();
        System.out.println("Antall bøker i db=" +antall);

        //lister ut alle bøker for en bestemt forfatter
        liste = fasade.getBokerForForfatter("Forfatter");
        System.out.println("Følgende bøker finnes for denne forfatteren: " + liste.size());
        for (Bok b : liste){
            System.out.println("\t" + b.getTittel());
        }
    }
}