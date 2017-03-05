/**
 * Created by Kjosavik on 3/3/2017.
 */


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
    public void slettAlt(){
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
    public void transfer(int kontonrFra, int kontonrTil, double belop){
        if (belop <= 0) {
            System.err.println("Error: beløp må være større enn 0");
            return;
        }
        EntityManager em = getEM();
        try{
            em.getTransaction().begin();
            Konto til = em.find(Konto.class, kontonrTil);
            Konto fra = em.find(Konto.class, kontonrFra);
            fra.setSaldo(fra.getSaldo() - belop);
            til.setSaldo(til.getSaldo() + belop);
            //Ventetid
            Thread.sleep(10000L);
            em.merge(til);
            em.merge(fra);
            em.getTransaction().commit();
        } catch (InterruptedException e) {
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


    public static void main(String[] args) throws Exception{
        EntityManagerFactory emf = null;
        KontoDao kdao = null;
        try {
            emf = Persistence.createEntityManagerFactory("kontoEntity4");
            System.out.println("kommer hit");
            kdao = new KontoDao(emf);
            Konto konto1 = new Konto(1, 250, "Erik");
            Konto konto2 = new Konto(2, 500, "Bob");
            Konto konto3 = new Konto(3, 1000, "Maria");

            kdao.lagreKonto(konto1);
            kdao.lagreKonto(konto2);
            kdao.lagreKonto(konto3);
        }finally{
            emf.close();
            System.exit(0);
        }

    }




}
