/**
 * Created by Kjosavik on 3/3/2017.
 */


import javafx.util.converter.PercentageStringConverter;

import javax.persistence.*;
import java.util.List;



public class KontoDao {
    private EntityManagerFactory emf;

    public KontoDao(EntityManagerFactory emf){
        this.emf = emf;
    }

    private void closeEm(EntityManager em){
        if (em != null && em.isOpen()) em.close();
    }

    public EntityManager getEM(){
        return emf.createEntityManager();
    }

    public void lagreKonto(Konto konto){
        EntityManager em = getEM();
        try{
            em.getTransaction().begin();
            em.persist(konto);
            em.getTransaction().commit();
        }finally{
            closeEm(em);
        }
    }

    public Konto finnKonto(int knr){
        EntityManager em = getEM();
        try{
            return em.find(Konto.class, knr);
        }finally{
            closeEm(em);
        }
    }
    public void trekk(int kontonr, double belop){
        if (belop <= 0) {
            System.err.println("Error: beløp må være større enn 0");
            return;
        }
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Konto funnet = em.find( Konto.class, kontonr );
            funnet.trekk(belop);
            em.merge(funnet);
            em.getTransaction().commit();
        }finally {
            closeEm(em);
        }
    }
    public void delete(){
        String toSQL = "SELECT OBJECT(o) FROM Konto o";
        EntityManager em = getEM();
        Query query = em.createQuery(toSQL);
        List<Konto> kontoer = query.getResultList();
        try {
            em.getTransaction().begin();
            kontoer.forEach(em::remove);
            em.getTransaction().commit();
        } finally {
            closeEm(em);
        }
    }

    public void updateKonto(Konto konto){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(konto);
            em.getTransaction().commit();
        }finally {
            closeEm(em);
        }
    }
    public void transaksjon(int kontonrFra, int kontonrTil, double belop){
        if (belop <= 0) {
            System.err.println("Må være større enn 0");
            return;
        }
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Konto til = em.find(Konto.class, kontonrTil);
            Konto fra = em.find(Konto.class, kontonrFra);
            em.refresh(til);
            em.refresh(fra);
            fra.setSaldo(fra.getSaldo() - belop);
            til.setSaldo(til.getSaldo() + belop);
            Thread.sleep(20000L);
            em.merge(til);
            em.merge(fra);
            em.getTransaction().commit();
        } catch (RollbackException e){
            if(e.getCause() instanceof OptimisticLockException) {
                em.clear();
                em.close();
                System.err.println("Transaksjon feilet pga. låsing, prøver igjen straks");
                transaksjon(kontonrFra, kontonrTil, belop);
                return;
            }
        }catch (InterruptedException e){
             e.printStackTrace();
        } finally {
            closeEm(em);
        }
    }
    public List<Konto> getKontoerMedMinstBelop(double belop){
        EntityManager em = getEM();
        try{
            Query query = em.createQuery("SELECT OBJECT(o) FROM Konto o WHERE o.saldo >" + belop);
            return query.getResultList();
        } finally {
            closeEm(em);
        }
    }




    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        KontoDao kdao = null;

        if(args[0].equals("delete")){
            try{
                emf = Persistence.createEntityManagerFactory("konto");
                kdao = new KontoDao(emf);
                kdao.delete();
                System.out.println("Alt ble slettet");
            }finally {
                emf.close();
            }
        }

        if (args[0].equals("create")) {
            try {
                //System.out.println("kommer hit");
                emf = Persistence.createEntityManagerFactory("konto");
                //System.out.println("kommer hit2");
                kdao = new KontoDao(emf);
                Konto konto1 = new Konto(1, 250, "Erik");
                Konto konto2 = new Konto(2, 500, "Bob");
                Konto konto3 = new Konto(3, 1000, "Maria");

                kdao.lagreKonto(konto1);
                kdao.lagreKonto(konto2);
                kdao.lagreKonto(konto3);
                System.out.println("3 kontoer opprettet");
            } catch (Exception e) {
                System.err.println("ERROR while createing");
                e.printStackTrace();
            } finally {
                try {
                    emf.close();
                } catch (NullPointerException e) {
                    System.err.println("Fachkckck");
                    e.printStackTrace();

                }
            }
        }

        /**
         * Denne finner alle kontoer med belop over 750 kr. Hvis du har  kjort den vanlige
         * create-metoden skal det bare vaere en konto med dette belopet.
         * Kontonr 3 med 1000kr som tilhorer Maria
         *
         * Den bytter sa navn pa eiere til Alf
         */
        if (args[0].equals("find")){
            try {
                emf = Persistence.createEntityManagerFactory("konto");
                kdao = new KontoDao(emf);
                List<Konto> liste = kdao.getKontoerMedMinstBelop(750);//bare en person med belop over 1000
                //kontonummer 3 skal vaere eneste i lista
                Konto konto = liste.get(0);
                if(konto.getKontonummer() == 3){
                    konto.setEier("Alf");
                    kdao.updateKonto(konto);
                }else{
                    System.out.println("noe er feil");
                }
                Konto resultat = kdao.finnKonto(konto.getKontonummer());
                if(resultat.getEier() == "Alf"){
                    System.out.println("Success: " + konto.toString());
                }else{
                    System.out.println("ERROR: " + konto.toString());
                }
            }finally {
                emf.close();
            }
            System.exit(0);
        }

        /**
         * Denne kjorer en overforing med threadsleep
         */
        if(args[0].equals("overfor")){
            try{
                emf = Persistence.createEntityManagerFactory("konto");
                kdao = new KontoDao(emf);
                kdao.transaksjon(1,2,100);
            }finally {
                emf.close();
            }
        }

        if(args[0].equals("resultat")){
            try{
                emf = Persistence.createEntityManagerFactory("konto");
                kdao = new KontoDao(emf);
                List<Konto> res = kdao.getKontoerMedMinstBelop(0);
                for (Konto konto: res){
                    System.out.print(konto + "\n");
                }
            }finally {
                emf.close();
            }
        }

        if(args[0].equals("noRollback")){
            try{
                emf = Persistence.createEntityManagerFactory("konto");
                kdao = new KontoDao(emf);
                kdao.transaksjon(1,2,100);
            }catch(javax.persistence.RollbackException e ){
                kdao.transaksjon(1,2,100);
                System.out.println("Provde pa nytt");
            }finally {
                emf.close();
            }
        }

    }
}
