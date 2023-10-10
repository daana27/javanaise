package jvn;

import java.util.ArrayList;
import java.util.List;

public class JvnHashObject {
    private String jon;
    private JvnObject jo;
    private int joi;
    private List<JvnRemoteServer> jrsInUse;
    private List<JvnRemoteServer> jsCurrentLock;

    public JvnHashObject(String jon, JvnObject jo, JvnRemoteServer js, int joi){
        jrsInUse = new ArrayList<>();
        jsCurrentLock = new ArrayList<>();
        this.jon = jon;
        this.jo = jo;
        this.joi = joi;
        jsCurrentLock.add(js);
        this.jo.setId(joi);
        jrsInUse.add(js);
    }

    public void emptyLockServer(){
        jsCurrentLock.clear();
    }

    public void emptyInUse(){
        jrsInUse.clear();
    }
    public void deleteLockServer(){
        jsCurrentLock.remove(0);
    }
    public JvnObject getJvnObject(){
        return jo;
    }
    public void setJvnObject(JvnObject jo){
        this.jo = jo;
    }

    public void addToJsLock(JvnRemoteServer jrs){
        jsCurrentLock.add(jrs);
    }
    public int getJvnObjectId(){
        return joi;
    }

    public String getJvnObjectName(){
        return jon;
    }

    public List<JvnRemoteServer> getJvnObjectServerList(){
        return jrsInUse;
    }
    public boolean isJvnServerUsing(JvnRemoteServer js){
        return jrsInUse.contains(js);
    }

    public void addJvnServer(JvnRemoteServer js){
        jrsInUse.add(js);
    }

    public int removeJvnServer(JvnRemoteServer js){
        if (this.isJvnServerUsing(js))
            jrsInUse.remove(js);
        return jrsInUse.size();
    }
    public List<JvnRemoteServer> getListServerLock(){
        return jsCurrentLock;
    }

    @Override
    public String toString(){
        return "Nom: " + jon + "\n"+
                "Id: " + joi + "\n"+
                "Usage : " + jrsInUse.size();
    }
}