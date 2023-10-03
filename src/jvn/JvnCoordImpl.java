/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.io.Serializable;
import java.util.Iterator;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int joi = 1;
    private Hashtable<Integer, JvnHashObject> hashTableIdtoHashObject;
    private Hashtable<String, Integer> hashTableNameToId;
    private Registry registry;
    /**
     * Default constructor
     * @throws JvnException
     **/
    JvnCoordImpl() throws Exception {
        // to be completed
        // creer tableau avec [objet_reference, alias]
        //cretaion du serveur le port
        //tableau avec jvns ids
        hashTableIdtoHashObject = new Hashtable<>();
        hashTableNameToId = new Hashtable<>();
    }

    /**
     *  Allocate a NEW JVN object id (usually allocated to a
     *  newly created JVN object)
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {
        // prend un id disponible, modification des donnees a faire
        return joi++;
    }

    /**
     * Associate a symbolic name with a JVN object
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     //* @param joi : the JVN object identification
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException{
        System.out.println("jcoord: register");
        int joitmp = this.jvnGetObjectId();
        hashTableNameToId.put(jon, joitmp);
        hashTableIdtoHashObject.put(joitmp, new JvnHashObject(jon, jo, js, joitmp));
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     * @param jon : the JVN object name
     * @param js : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException{
        // rutilser js
        System.out.println("jcoord: lookup sur " + jon);
        if(hashTableNameToId.get(jon) == null){
            return null;
        }
        else{
            System.out.println(js);
            int id = hashTableNameToId.get(jon);
            if(!hashTableIdtoHashObject.get(id).isJvnServerUsing(js))
                hashTableIdtoHashObject.get(id).addJvnServer(js);
            System.out.println(hashTableIdtoHashObject.get(id));
            return hashTableIdtoHashObject.get(hashTableNameToId.get(jon)).getJvnObject();
        }
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
        // si l objet n'a plus d utilisation, les upprimer
        Iterator<JvnHashObject> it = hashTableIdtoHashObject.elements().asIterator();

        while(it.hasNext()){
            JvnHashObject jvnho = it.next();
            if(jvnho.isJvnServerUsing(js)){
                int jsuser = jvnho.removeJvnServer(js);
                if(jsuser == 0)
                    System.out.println("Implementation jvnTerminate à completer lorsqu un objet n est plus utilise");
            }


        }

        System.out.println("Recu demande Terminate");
    }
}
 
  
 