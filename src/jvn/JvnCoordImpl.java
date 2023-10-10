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
import java.util.List;

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
        hashTableIdtoHashObject = new Hashtable<>();
        hashTableNameToId = new Hashtable<>();
    }

    /**
     *  Allocate a NEW JVN object id (usually allocated to a
     *  newly created JVN object)
     * @throws java.rmi.RemoteException,JvnException
     **/
    public synchronized int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {
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
    public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException{
        System.out.println("jcoord: register");
        int id = jo.jvnGetObjectId();
        if(hashTableIdtoHashObject.get(id) != null || hashTableNameToId.get(jon) != null)
            System.out.println("objet deja existant ! ");
        hashTableNameToId.put(jon, id);
        hashTableIdtoHashObject.put(id, new JvnHashObject(jon, jo, js, id));
        System.out.println("joitmp = " + id + " joi = " + joi);
        System.out.println(hashTableIdtoHashObject.get(id).getJvnObject().getState());
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     * @param jon : the JVN object name
     * @param js : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException{
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
    public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
        System.out.println("Coord jvnLockRead : " + joi);
        JvnHashObject hashObject = hashTableIdtoHashObject.get(joi);
        System.out.println("hashobject id : " + hashObject.getJvnObjectId());
        JvnObject jvnObject = hashObject.getJvnObject();
        JvnObject.State state = jvnObject.getState();
        if(state == JvnObject.State.NL || state == JvnObject.State.R){
            jvnObject.setState(JvnObject.State.R);
            hashObject.addJvnServer(js);
            hashObject.addToJsLock(js);
            hashTableIdtoHashObject.put(joi, hashObject);
            return jvnObject;
        } else {
            System.out.println("state = " + state);
            JvnRemoteServer jsLock = hashObject.getListServerLock().get(0);
            JvnObject sr =(JvnObject) jsLock.jvnInvalidateWriterForReader(joi);
            sr.setState(JvnObject.State.R);
            hashObject.setJvnObject(sr);
            hashObject.addJvnServer(js);
            hashObject.addToJsLock(js);
            hashTableIdtoHashObject.put(joi, hashObject);
            System.out.println("coord : id = + " + joi + " state = " + hashTableIdtoHashObject.get(joi).getJvnObject().getState());
            return sr;
        }
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
        System.out.println("Coord : jvnLockWrite : " + joi + " state = " + hashTableIdtoHashObject.get(joi).getJvnObject().getState());
        JvnHashObject hashObject = hashTableIdtoHashObject.get(joi);
        JvnObject jvnObject = hashObject.getJvnObject();
        JvnObject.State state = jvnObject.getState();
        if(state == JvnObject.State.NL){
            jvnObject.setState(JvnObject.State.W);
            hashObject.addToJsLock(js);
            return jvnObject;
        } else if (state == JvnObject.State.R){
            List<JvnRemoteServer> jsLock = hashObject.getListServerLock();
            System.out.println("size = " + jsLock.size());
            for (JvnRemoteServer jrs : jsLock) {
                if(!jrs.equals(js)){
                    System.out.println("jrs : " + jrs + " js :" + js);
                    jrs.jvnInvalidateReader(hashObject.getJvnObjectId());
                }
            }
            hashObject.emptyLockServer();
            hashObject.emptyInUse();
            hashObject.addJvnServer(js);
            hashObject.addToJsLock(js);
            jvnObject.setState(JvnObject.State.W);
            System.out.println("jcoord lockWrite : " + hashTableIdtoHashObject.get(joi).getJvnObject().getState());
            return jvnObject;
        } else if(state == JvnObject.State.W){
            JvnRemoteServer jsLock = hashObject.getListServerLock().get(0);
            Serializable sr = jsLock.jvnInvalidateWriter(joi);
            hashTableIdtoHashObject.get(joi).setJvnObject((JvnObject) sr);
            hashObject.deleteLockServer();
            hashObject.addToJsLock(js);
            jvnObject.setState(JvnObject.State.W);
            return sr;
        }
        return null;
    }

    /**
     * A JVN server terminates
     * @param js  : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
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
 
  
 