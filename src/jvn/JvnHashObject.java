package jvn;

import java.util.Hashtable;

public class JvnHashObject {
    private String jon;
    private JvnObject jo;
    private int joi;
    LockState state;
    private Hashtable<JvnRemoteServer, LockState> serversInUse;

    public JvnHashObject(String jon, JvnObject jo, JvnRemoteServer js, int joi){
        serversInUse = new Hashtable<>();
        serversInUse.put(js, LockState.W);
        this.jon = jon;
        this.jo = jo;
        this.joi = joi;
        state = LockState.W;
    }
    public JvnObject getJvnObject(){
        return jo;
    }

    public void setJvnObject(JvnObject jo){
        this.jo = jo;
    }

    public int getJvnObjectId(){
        return joi;
    }

    public String getJvnObjectName(){
        return jon;
    }

    public Hashtable<JvnRemoteServer, LockState> getServersInUse(){
        return serversInUse;
    }

    public void setState(LockState state){
        this.state = state;
    }

    public LockState getState(){
        return state;
    }

    @Override
    public String toString(){
        return "Nom: " + jon + "\n"+
                "Id: " + joi + "\n"+
                "Usage : " + serversInUse.size();
    }
}