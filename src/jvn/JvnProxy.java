package jvn;

import irc.TargetMethod;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
    JvnObject jo;
    static JvnServerImpl js;

    private JvnProxy(Serializable sentence, String joName){
        try {
            js = JvnServerImpl.jvnGetServer();
            jo = js.jvnLookupObject(joName);
            if (jo == null) {
                jo = js.jvnCreateObject(sentence);
                jo.jvnUnLock();
                js.jvnRegisterObject(joName, jo);
            }
        } catch (Exception e) {
            System.out.println("error in proxy : " + e.getMessage());
        }
    }
    public static Serializable newInstance(Serializable sentence, String joName){
            return (Serializable) java.lang.reflect.Proxy.newProxyInstance(
                    sentence.getClass().getClassLoader(),
                    sentence.getClass().getInterfaces(),
                    new JvnProxy(sentence,joName ));
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if(method.getAnnotation(TargetMethod.class).name().equals("write")){
            jo.jvnLockWrite();
        } else {
            jo.jvnLockRead();
        }
        result = method.invoke(jo.jvnGetSharedObject(), args);
        jo.jvnUnLock();
        return result;
    }
}

