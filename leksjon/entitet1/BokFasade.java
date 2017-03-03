/*
 * BokFasade.java
 *
 * Created on December 8, 2006, 4:43 PM
 */

package leksjon.entitet1;
import java.rmi.*;
import java.util.List;

public interface BokFasade extends Remote{
    public void lagreNyBok(Bok bok) throws RemoteException;
    public Bok finnBok(String isbn) throws RemoteException;
    public void endreBok(Bok bok) throws RemoteException;
    public void slettBok(String isbn) throws RemoteException;
    public List<Bok> getAlleBoker() throws RemoteException;
    public int getAntallBoker() throws RemoteException;
    public List<Bok> getBokerForForfatter(String forfatter) throws RemoteException;
}
