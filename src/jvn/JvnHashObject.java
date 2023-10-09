package jvn;

import java.util.ArrayList;
import java.util.List;

public class JvnHashObject {
    private String jon;
    private JvnObject jo;
    private int joi;
    private List<JvnRemoteServer> jrsInUse;
    private List<JvnRemoteServer> jsWantLock;
    private List<JvnRemoteServer> jsCurrentLock;

    public JvnHashObject(String jon, JvnObject jo, JvnRemoteServer js, int joi){
        jrsInUse = new ArrayList<>();
        jsWantLock = new ArrayList<>();
        jsCurrentLock = new ArrayList<>();
        this.jon = jon;
        this.jo = jo;
        this.joi = joi;
        jo.setId(joi);
        jrsInUse.add(js);
    }

    public JvnObject getJvnObject(){
        return jo;
    }
    public JvnObject getObject(){
        return jo;
    }

    public void addJrsInUse(JvnRemoteServer jrs){
        jrsInUse.add(jrs);
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