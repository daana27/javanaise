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
import java.util.*;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int joi = 1;
    private Hashtable<Integer, JvnHashObject> hashTableIdtoHashObject;
    private Hashtable<String, Integer> hashTableNameToId;

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
        if(hashTableIdtoHashObject.get(id) != null || hashTableNameToId.get(jon) != null){
            System.out.println("objet deja existant ! ");
            htString(id);
        } else {
            hashTableNameToId.put(jon, id);
            //hashTableIdtoHashObject.put(id, new JvnHashObject(jon, jo, js, id));
            hashTableIdtoHashObject.put(id, new JvnHashObject(jon, new JvnObjectImpl(jo.jvnGetSharedObject(), id, LockState.NL), js, id));
            htString(id);
        }
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     * @param jon : the JVN object name
     * @param js : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException{
        System.out.println("jcoord: lookup sur " + jon);
        if(hashTableNameToId.get(jon) == null){
            return null;
        } else{
            hashTableIdtoHashObject.get(hashTableNameToId.get(jon)).getServersInUse().put(js, LockState.NL);
            JvnObject jo = hashTableIdtoHashObject.get(hashTableNameToId.get(jon)).getJvnObject();
            htString(jo.jvnGetObjectId());
            return jo;
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
        JvnObject jvnObject = hashObject.getJvnObject();
        Hashtable<JvnRemoteServer, LockState> ht = hashObject.getServersInUse();
        if(hashObject.state == LockState.NL || hashObject.state == LockState.R){
            ht.put(js, LockState.R);
            hashObject.setState(LockState.R);
        } else {
            System.out.println("jstate = " + hashObject.state);
            Set<JvnRemoteServer> keys = ht.keySet();
            for(JvnRemoteServer key: keys){
                if(ht.get(key) == LockState.W && !key.equals(js)){
                    Serializable serializable = key.jvnInvalidateWriterForReader(joi);
                    hashObject.setState(LockState.R);
                    ht.put(key, LockState.R);
                    ht.put(js, LockState.R);
                    hashObject.setJvnObject(new JvnObjectImpl(serializable, hashObject.getJvnObjectId(), LockState.NL));
                    htString(joi);
                    return serializable;
                }
            }
        }
        htString(joi);
        return jvnObject.jvnGetSharedObject();
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
        System.out.println("coord lockWrite : state = " + hashTableIdtoHashObject.get(joi).getState());
        JvnHashObject hashObject = hashTableIdtoHashObject.get(joi);
        JvnObject jvnObject = hashObject.getJvnObject();
        Serializable serializable;
        Hashtable<JvnRemoteServer, LockState> ht = hashObject.getServersInUse();
        if(hashObject.state == LockState.NL){
            hashObject.setState(LockState.W);
            ht.put(js, LockState.W);
        } else if (hashObject.state == LockState.R){
            System.out.println("coord lockWrite : state = R");
            Set<JvnRemoteServer> keys = ht.keySet();
            for(JvnRemoteServer key: keys){
                if(ht.get(key) == LockState.R && !key.equals(js)){
                    key.jvnInvalidateReader(joi);
                    hashObject.setState(LockState.W);
                    ht.put(key, LockState.NL);
                    ht.put(js, LockState.W);
                }
            }
        } else if(hashObject.state == LockState.W){
            Set<JvnRemoteServer> keys = ht.keySet();
            for(JvnRemoteServer key: keys){
                if(ht.get(key) == LockState.W && !key.equals(js)){
                    serializable = key.jvnInvalidateWriter(joi);
                    ht.put(key, LockState.NL);
                    ht.put(js, LockState.W);
                    hashObject.setJvnObject(new JvnObjectImpl(serializable, hashObject.getJvnObjectId(), LockState.NL));
                    htString(joi);
                    return serializable;
                }
            }
        }
        htString(joi);
        return jvnObject.jvnGetSharedObject();
    }

    /**
     * A JVN server terminates
     * @param js  : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        for (JvnHashObject jvnHashObject : hashTableIdtoHashObject.values()) {
            Hashtable<JvnRemoteServer, LockState> ht = jvnHashObject.getServersInUse();
            if (ht.containsKey(js)){
                LockState jhoState = jvnHashObject.getState();
                if(jhoState == LockState.W){
                    if(ht.get(js) == LockState.W){
                        System.out.println("terminate invalidate writer");
                        Serializable serializable = js.jvnInvalidateWriter(jvnHashObject.getJvnObjectId());
                        System.out.println("terminate invalidate writer");
                        jvnHashObject.setJvnObject(new JvnObjectImpl(serializable, jvnHashObject.getJvnObjectId(), LockState.NL));
                        jvnHashObject.setState(LockState.NL);
                    }
                    ht.remove(js);
                } else if (jhoState == LockState.R){
                    if(ht.get(js) == LockState.NL){
                        ht.remove(js);
                        return;
                    }
                    int count = 0;
                    for(LockState lockState : ht.values()){
                        if(lockState == LockState.R){
                            count++;
                            if(count >= 2){
                                ht.remove(js);
                                return;
                            }
                        }
                    }
                    jvnHashObject.setState(LockState.NL);
                }
                ht.remove(js);
            }
        }
        System.out.println("Terminate");
    }

    private void htString(int joi){
        StringBuilder str = new StringBuilder();
        JvnHashObject ho = hashTableIdtoHashObject.get(joi);
        Hashtable<JvnRemoteServer, LockState> ht = ho.getServersInUse();
        Set<JvnRemoteServer> keys = ht.keySet();
        str.append("[ ");
        for(JvnRemoteServer key: keys){
            str.append(ht.get(key)).append(", ");
        }
        str.append(" ]");
        System.out.println(str);
    }
}
 
  
 