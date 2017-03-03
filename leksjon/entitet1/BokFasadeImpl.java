/*
 * BokFasadeImpl.java
 *
 * Created on December 8, 2006, 4:37 PM
 */

package leksjon.entitet1;
import java.rmi.server.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.persistence.*;

public class BokFasadeImpl extends UnicastRemoteObject implements BokFasade{
    private BokDAO dao;
    //public BokFasadeImpl() {}
    public BokFasadeImpl(BokDAO dao) throws RemoteException{
        this.dao = dao;
    }
    public synchronized void lagreNyBok(Bok bok) throws RemoteException{ 
        dao.lagreNyBok(bok);
    }
    public Bok finnBok(String isbn) throws RemoteException {
        return dao.finnBok(isbn);
    }
    public synchronized void endreBok(Bok bok) throws RemoteException { 
        dao.endreBok(bok);
    }
    public synchronized void slettBok(String isbn) throws RemoteException {
        dao.slettBok(isbn);
    }
    public List<Bok> getAlleBoker() throws RemoteException {
        return dao.getAlleBoker();
    }
    public int getAntallBoker() throws RemoteException {
        return dao.getAntallBoker();
    }
    public List<Bok> getBokerForForfatter(String forfatter) throws RemoteException{
        return dao.getBokerForForfatter(forfatter);
    }

    public static void main(String args[]) throws Exception{
        EntityManagerFactory emf = null;
        try{
            BokFasade fasade = null;
            Registry registry = null;
            System.out.println("Starter tjener...");
            //try{
            emf = Persistence.createEntityManagerFactory("LeksjonStandaloneEntitetPU");
            EntityManager em = emf.createEntityManager();
            System.out.println("EntityManager og Factory opprettet...");
            registry = LocateRegistry.createRegistry(1099);//start rmiregistry
            System.out.println("RmiRegistry startet....");
            fasade = new BokFasadeImpl(new BokDAO(emf));
            System.out.println("Fasaden er opprettet...");
            registry.rebind("BokFasade",fasade);
            System.out.println("Fasaden er bundet i rmiregistret...");

            javax.swing.JOptionPane.showConfirmDialog(null, "Trykk for å avslutt tjener");
        }finally{
            emf.close();
            System.exit(0);//MERK! Ved kjøring i netbeans må man velge 
        //Runtime > Processes > Terminate Process for å avslutte prosessen!
        }
    }
}