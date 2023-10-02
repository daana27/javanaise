/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

 package jvn;

 import java.rmi.registry.LocateRegistry;
 import java.rmi.registry.Registry;
 import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.io.Serializable;
 
 
 public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
 
  private Hashtable<String, JvnObject> hashTableJvo;

  private Registry registry;
 /**
   * Default constructor
   * @throws JvnException
   **/
   private JvnCoordImpl() throws Exception {
     // to be completed
     // creer tableau avec [objet_reference, alias]
     //cretaion du serveur le port
     //tableau avec jvns ids
     hashTableJvo = new Hashtable<>();
     JvnRemoteCoord coord_stub = (JvnRemoteCoord) UnicastRemoteObject.exportObject(this, 6000);
     registry = LocateRegistry.getRegistry();
     registry.rebind("coord", coord_stub);
   }
 
   /**
   *  Allocate a NEW JVN object id (usually allocated to a 
   *  newly created JVN object)
   * @throws java.rmi.RemoteException,JvnException
   **/
   public int jvnGetObjectId() throws java.rmi.RemoteException,jvn.JvnException {
     // to be completed 
     // prend un id disponible, modification des donnees a faire
     //retourne l id utilise
     return 0;
   }
   
   /**
   * Associate a symbolic name with a JVN object
   * @param jon : the JVN object name
   * @param jo  : the JVN object 
   * @param joi : the JVN object identification
   * @param js  : the remote reference of the JVNServer
   * @throws java.rmi.RemoteException,JvnException
   **/
   public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException,jvn.JvnException{
     // to be completed 
     // mettre a jour le tableau
   }
   
   /**
   * Get the reference of a JVN object managed by a given JVN server 
   * @param jon : the JVN object name
   * @param js : the remote reference of the JVNServer
   * @throws java.rmi.RemoteException,JvnException
   **/
   public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException,jvn.JvnException{
     // rutilser js
     System.out.println("lookup sur " + jon);
    return hashTableJvo.get(jon);
   }
   
   /**
   * Get a Read lock on a JVN object managed by a given JVN server 
   * @param joi : the JVN object identification
   * @param js  : the remote reference of the server
   * @return the current JVN object state
   * @throws java.rmi.RemoteException, JvnException
   **/
    public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
     // to be completed
         // demande un lock
     // lock + mise a jour du tableau
     return null;
    }
 
   /**
   * Get a Write lock on a JVN object managed by a given JVN server 
   * @param joi : the JVN object identification
   * @param js  : the remote reference of the server
   * @return the current JVN object state
   * @throws java.rmi.RemoteException, JvnException
   **/
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
     // to be completed
     // demande un lock
     // lock + mise a jour du tableau
     return null;
    }
 
   /**
   * A JVN server terminates
   * @param js  : the remote reference of the server
   * @throws java.rmi.RemoteException, JvnException
   **/
     public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
    // to be completed
    // supprimer les ids qu utilisait le serveur
    // si l objet n'a plus d utilisation, les upprimer
     System.out.println("Recu demande Terminate");
   }
 }
 
  
 