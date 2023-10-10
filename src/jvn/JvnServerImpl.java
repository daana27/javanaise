/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.Hashtable;


public class JvnServerImpl
		extends UnicastRemoteObject
		implements JvnLocalServer, JvnRemoteServer{

	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton
	private static JvnServerImpl js = null ;

	private JvnRemoteCoord jvnRemoteCoord;

	private Hashtable<Integer, JvnObject> joiToJvnObject;

	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		Registry registry = LocateRegistry.getRegistry( 6090);
		jvnRemoteCoord = (JvnRemoteCoord) registry.lookup("coord");
		joiToJvnObject = new Hashtable<>();
	}

	/**
	 * Static method allowing an application to get a reference to 
	 * a JVN server instance
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				System.out.println(e);
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 * @throws JvnException
	 **/
	public  synchronized void jvnTerminate() throws jvn.JvnException {
		try {
			jvnRemoteCoord.jvnTerminate(js);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creation of a JVN object
	 * @param o : the JVN object state
	 * @throws JvnException
	 **/
	public  synchronized JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException {
		int joi;
		try {
			joi = jvnRemoteCoord.jvnGetObjectId();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		return new JvnObjectImpl(o, joi);
	}

	/**
	 *  Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo : the JVN object 
	 * @throws JvnException
	 **/
	public  synchronized void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException, RemoteException {
		jvnRemoteCoord.jvnRegisterObject(jon, jo, this);
		joiToJvnObject.put(jo.jvnGetObjectId(), jo);
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * @param jon : the JVN object name
	 * @return the JVN object 
	 * @throws JvnException
	 * @throws RemoteException
	 **/
	public  synchronized JvnObject jvnLookupObject(String jon) throws jvn.JvnException{
		try {
			System.out.println("js: lookup");
			JvnObject jvnObject = jvnRemoteCoord.jvnLookupObject(jon, this);
			if(jvnObject == null)
				return null;
			joiToJvnObject.put(jvnObject.jvnGetObjectId(), jvnObject);
			return jvnObject;
		} catch (RemoteException e) {
			return null;
		}
	}

	/**
	 * Get a Read lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public synchronized Serializable jvnLockRead(int joi) throws JvnException {
		try {
			return jvnRemoteCoord.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get a Write lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public synchronized Serializable jvnLockWrite(int joi) throws JvnException {
		try {
			return jvnRemoteCoord.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Invalidate the Read lock of the JVN object identified by id 
	 * called by the JvnCoord
	 * @param joi : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized void jvnInvalidateReader(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		System.out.println("js : ivalidateReader ");
		JvnObject jvnObject = joiToJvnObject.get(joi);
		jvnObject.jvnInvalidateReader();
	}

	/**
	 * Invalidate the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		System.out.println("js : ivalidateWriter ");
		return joiToJvnObject.get(joi).jvnInvalidateWriter();
	}

	/**
	 * Reduce the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		System.out.println("js : invalidateWriterForReader ");
		return joiToJvnObject.get(joi).jvnInvalidateWriterForReader();
	}
}
 
  
 