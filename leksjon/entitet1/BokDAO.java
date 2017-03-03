/*
 * BokDAO.java
 *
 * Created on November 27, 2008
/*
 * BokDAO.java
 *
 * Created on November 27, 2008, av Tomas Holt
 * Kommentar: Unntakshånteringen er av enkleste type!
 * Alle unntak (exceptions) som kastes av EntityManager arver 
 * PersistenceException. Alle slike unntak (bortsett fra de knyttet til spørringer) 
 * i en transaksjon fører til automatisk tilbakerulling (rollback) av transaksjonen.
 * Å gjøre tilbakerulling i koden er derfor unødvendig (om ikke andre typer unntak kan oppstå).
 * Ved tilbakerulling vil alle entiteter bli frikoblet (detached), da man ikke
 * vet om integriteten på objektene er god. Den naturlig løsningen på dette problemet
 * er å lukke EntityManager, og evt. prøve det man hold på med en nye EntityManager.
 * En EntityManger er et lettvektsobjekt, og det koster derfor lite å opprette 
 * slike (i motsetning til EntityManagerFactory - som setter opp oppkolinger
 * til databasen (gjerne i en pool)).
 *
 */

package leksjon.entitet1;

import javax.persistence.*;
import java.util.List;

public class BokDAO{
    private EntityManagerFactory emf;
    /* OBS! EntityManagerFactory er thread safe, men det er ikke 
    * EntityManger! Objektvariabel medfører at vi må synkronisere metodene.
    * Vi løser det med å ha EntityManger bare lokalt. Unngår trådproblematikk!
    */
    //private EntityManager em;
  
            
    public BokDAO(EntityManagerFactory emf){
        this.emf = emf;
    }
    
    //Metoden er laget for å lagre nye bøker
    //Merk at persist() vil fungere som en SQL INSERT
    //Boka (ISBN) kan derfor ikke vre lagret i DB fra før!!
    public void lagreNyBok(Bok bok){
        EntityManager em  = getEM();
        try{
             em.getTransaction().begin();
             em.persist(bok);//fører boka inn i lagringskontekt (persistence context)
             em.getTransaction().commit();//lagring skjer her
        }finally{
            lukkEM(em);
        }
    }
    
    //Finner en bok basert på primærtnøkkel
    public Bok finnBok(String isbn){
        EntityManager em = getEM();
        try{
            return em.find(Bok.class, isbn);
        }finally{
            lukkEM(em);
        }
    }
    
    //Endrer en eksisterenede bok, vi bruker merge for å sikre at boka
    //føres inn i lagringskonteksten (må det om den har vært serialisert)
    public void endreBok(Bok bok){
        EntityManager em = getEM();
        try{
            em.getTransaction().begin();
            Bok b = em.merge(bok);//sørger for å føre entiteten inn i lagringskonteksten
            em.getTransaction().commit();//merk at endringene gjort utenfor transaksjonen blir lagret!!!
        }finally{
            lukkEM(em);
        }
    }
    
    public void slettBok(String isbn){
        EntityManager em = getEM();
        try{
            Bok b = finnBok(isbn);
            em.getTransaction().begin();
            em.remove(b);//remove må kalles i en transaksjon
            em.getTransaction().commit();
        }finally{
            lukkEM(em);
        }
    }
    
    //sprring som henter alle bker
    public List<Bok> getAlleBoker(){
        EntityManager em = getEM();
        try{
            Query q = em.createQuery("SELECT OBJECT(o) FROM Bok o");
            //SELECT o FROM BOK o gir samme resultat
            //MERK at Bok må ha stor B (samme som klassenavn)
            return q.getResultList();
        }finally{
            lukkEM(em);
        }
    }
    
    //her bruker vi en navngitt sprring (NamedQuery). Denne finner du i Bok-klassen
    //Slike legges alts i entitetsklassen og gir
    //mulighet for optimalisering av sprring ala PreparedStatement
    public int getAntallBoker(){
        EntityManager em = getEM();
        try{
            Query q = em.createNamedQuery("finnAntallBoker");
            Long ant = (Long)q.getSingleResult();
            return ant.intValue();
        }finally{
            lukkEM(em);
        }
    }
    
    //Merk at begge sprringene i metoden fungerer (en utkommentert) 
    //Ofte kan nok den frste vre  foretrekke.
    public List<Bok> getBokerForForfatter(String forfatter){
        EntityManager em = getEM();
        try{
            Query q = em.createQuery("SELECT OBJECT(a) FROM Bok a WHERE a.forfatter= :forfatter");
            //Query q = em.createQuery("SELECT OBJECT(a) FROM Bok a WHERE a.forfatter='" +forfatter + "'");
            q.setParameter("forfatter",forfatter);
            return q.getResultList();
        }finally{
            lukkEM(em);
        }
    }
     
    private EntityManager getEM(){
        return emf.createEntityManager();
    }
    
    private void lukkEM(EntityManager em){
        if (em != null && em.isOpen()) em.close();
    }
       
    /* En liten "testklient" */
    public static void main(String args[]) throws Exception{
        EntityManagerFactory emf = null;
        BokDAO fasade = null;
        System.out.println("starter2...");
        try{
            emf = Persistence.createEntityManagerFactory("LeksjonStandaloneEntitetPU");
	    //LeksjonStandaloneEntitetPU=Persistence Unit Name, se persistence.xml
            System.out.println("konstruktor ferdig " + emf);
            fasade = new BokDAO(emf);
            System.out.println("konstruktor ferdig");
            
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
            
            //Skriv ut bkene som er lagret
            System.out.println("Følgende bøker er lagret i DB:");
            List<Bok> liste = fasade.getAlleBoker();
            for (Bok b : liste){
                System.out.println("---" + b);
            }
            
            bok = (Bok)liste.get(0);
            bok.setTittel("Endret tittel");
            fasade.endreBok(bok);
            
            bok = fasade.finnBok(bok.getIsbn());//henter ut boka p nytt
            System.out.println("Bok er nå endret, og blitt slik: " + bok);
            
            //finner antall bker i db
            int antall = fasade.getAntallBoker();
            System.out.println("Antall bøker i db=" +antall);
            
            //lister ut alle bker for en bestemt forfatter
            liste = fasade.getBokerForForfatter("Forfatter");
            System.out.println("Følgende bøker finnes for denne forfatteren: " + liste.size());
            for (Bok b : liste){
                System.out.println("\t" + b.getTittel());
            }
        }finally{
            emf.close();
        }
    }
}