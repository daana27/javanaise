package jvn;

import java.util.ArrayList;
import java.util.List;

public class JvnHashObject {
    private String jon;
    private JvnObject jo;
    private int joi;
    private List<JvnRemoteServer> jrs;
    private JvnRemoteServer js;

    public JvnHashObject(String jon, JvnObject jo, JvnRemoteServer js, int joi){
        this.jon = jon;
        this.jo = jo;
        this.js = js;
        this.joi = joi;
        jrs = new ArrayList<>();
        jrs.add(js);
    }

    public JvnObject getJvnObject(){
        return jo;
    }
    public int getJvnObjectId(){
        return joi;
    }

    public String getJvnObjectName(){
        return jon;
    }

    public List<JvnRemoteServer> getJvnObjectServerList(){
        return jrs;
    }
    public boolean isJvnServerUsing(JvnRemoteServer js){
        return jrs.contains(js);
    }

    public void addJvnServer(JvnRemoteServer js){
        jrs.add(js);
    }

    public int removeJvnServer(JvnRemoteServer js){
        if (this.isJvnServerUsing(js))
            jrs.remove(js);
        return jrs.size();
    }

    @Override
    public String toString(){
        return "Nom: " + jon + "\n"+
                "Id: " + joi + "\n"+
                "Nombre d'utilisateurs : " + jrs.size();
    }
}