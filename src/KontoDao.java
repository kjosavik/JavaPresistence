/**
 * Created by Kjosavik on 3/3/2017.
 */
import javax.persistence.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.persistence.*;


public class KontoDao {
    private EntityManagerFactory emf;

    public KontoDao(EntityManagerFactory emf){
        this.emf = emf;
    }

    private void closeEm(EntityManager em){
        if (em != null && em.isOpen()) em.close();
    }

    public void lagreKonto(Konto konto){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(konto);
            em.getTransaction().commit();
        }finally{
            closeEm(em);
        }
    }

    public static void main(String[] args) throws Exception{
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("LeksjonStandaloneEntitetPU");

            KontoDao kdao = new KontoDao(emf);
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
