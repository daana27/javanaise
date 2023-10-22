package jvn;

import irc.Sentence;
import irc.TargetMethod;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnProxy implements InvocationHandler {
    JvnObject jo;
    static JvnServerImpl js;

    private JvnProxy(Serializable sentence){
        try {
            js = JvnServerImpl.jvnGetServer();
            jo = js.jvnLookupObject("IRC");

            if (jo == null) {
                jo = js.jvnCreateObject(sentence);
                jo.jvnUnLock();
                js.jvnRegisterObject("IRC", jo);
            }
            System.out.println("newInstance jo : " + jo);
        } catch (Exception e) {
            System.out.println("proxy : " + e.getMessage());
        }
    }
    public static Serializable newInstance(Serializable sentence){
            return (Serializable) java.lang.reflect.Proxy.newProxyInstance(
                    sentence.getClass().getClassLoader(),
                    sentence.getClass().getInterfaces(),
                    new JvnProxy(sentence));

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

