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

    public synchronized void setId(int joi){
        this.joi = joi;
    }

    public synchronized void setState(State st){
        state = st;
    }

    public synchronized State getState(){
        return state;
    }

    @Override
    public synchronized void jvnLockRead() throws JvnException {
        System.out.println("lockRead: joi de l objet = " + joi + " et state = " + state);
        System.out.println("object: reader, current state = " + JvnServerImpl.jvnGetServer().getState(joi));
        if(state == State.RC){
            state = State.R;
        } else if(state == State.NL){
            System.out.println("object jvnLockRead: NL");
            object = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
            state = State.R;
        } else if(state == State.W || state == State.WC){
            state = State.RWC;
        }
        JvnServerImpl.jvnGetServer().setLock(joi, state);
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
        System.out.println("lockWrite: joi de l objet = " + joi + " et state = " + state);
        if(state == State.WC || state == State.RWC){
            state = State.W;
        } else if (state == State.RC || state == State.R ||state == State.NL ) {
            object = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
            state = State.W;
        }
        JvnServerImpl.jvnGetServer().setLock(joi, state);
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        if(state == State.R){
          state = State.RC;
        } else if(state == State.W){
            state = State.WC;
        }
        JvnServerImpl.jvnGetServer().setLock(joi, state);
        System.out.println("unlock: joi de l objet = " + joi + " et state = " + state);
        notifyAll();
    }

    @Override
    public synchronized int jvnGetObjectId() throws JvnException {
        return joi;
    }

    @Override
    public synchronized Serializable jvnGetSharedObject() throws JvnException {
        return this.object;
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
        System.out.println("object: invalidate reader, current state = " + state);
        if(state == State.RWC || state == State.RC){
            System.out.println("object: invalidate reader " + state);
            state = State.NL;
            JvnServerImpl.jvnGetServer().setLock(joi, state);
        } else if(state == State.R){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = State.NL;
            JvnServerImpl.jvnGetServer().setLock(joi, state);
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        System.out.println("object: invalidateWriter, current state = " + state);
        if(state == State.RWC || state == State.WC){
            state = State.NL;
            JvnServerImpl.jvnGetServer().setLock(joi, state);
            return this;
        } else if(state == State.W){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = State.NL;
            JvnServerImpl.jvnGetServer().setLock(joi, state);
            return this;
        } else{
            System.out.println("etat non concordant avec invalidate writer");
            return null;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        System.out.println("object: invalidateWriterForReader, current state = " + state);
        if(state == State.RWC || state == State.WC){
            state = State.RC;
            JvnServerImpl.jvnGetServer().setLock(joi, state);
            return this;
        } else if(state == State.W){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = State.RC;
            JvnServerImpl.jvnGetServer().setLock(joi, state);
            return this;
        } else{
            System.out.println("etat non concordant avec invalidate writer for reader");
            return null;
        }
    }
}