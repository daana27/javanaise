package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{
    State state;
    Serializable object;
    int joi;
    JvnObjectImpl(Serializable o, int joi){
        object = o;
        state = State.W;
        this.joi = joi;
    }

    public void setId(int joi){
        this.joi = joi;
    }

    public void setState(State st){
        state = st;
    }

    public State getState(){
        return state;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        System.out.println("joi de l objet = " + joi);
        if(state == State.RC){
            state = State.R;
        } else if(state == State.NL){
            state = State.R;
            object = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
        } else if(state == State.W || state == State.WC){
            state = State.RWC;
            object = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        if(state == State.WC || state == State.RWC){
            state = State.W;
        } else if (state == State.RC || state == State.R ||state == State.NL ) {
            state = State.W;
            object = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
        }
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        if(state == State.R){
          state = State.RC;
        } else if(state == State.W){
            state = State.WC;
        }
        notifyAll();
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return joi;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return this.object;
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
        if(state == State.RWC || state == State.RC){
            state = State.NL;
        }
        else if(state == State.W){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = State.NL;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        if(state == State.RWC || state == State.WC){
            state = State.NL;
            return this;
        } else if(state == State.W){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = State.NL;
            return this;
        } else{
            System.out.println("etat non concordant avec invalidate writer");
            return null;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        if(state == State.RWC || state == State.WC){
            state = State.R;
            return this;
        } else if(state == State.W){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = State.R;
            return this;
        } else{
            System.out.println("etat non concordant avec invalidate writer for reader");
            return null;
        }
    }



}
