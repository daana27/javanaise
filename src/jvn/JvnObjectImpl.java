package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{
    LockState state;
    Serializable object;
    int joi;
    JvnObjectImpl(Serializable o, int joi){
        object = o;
        state = LockState.W;
        this.joi = joi;
    }

    public JvnObjectImpl(Serializable o, int joi, LockState state){
        object = o;
        this.state = state;
        this.joi = joi;
    }

    @Override
    public synchronized void jvnLockRead() throws JvnException {
        if(state == LockState.RC){
            state = LockState.R;
        } else if(state == LockState.NL){
            object = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
            state = LockState.R;
        } else if(state == LockState.W || state == LockState.WC){
            state = LockState.RWC;
        }
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
        if(state == LockState.WC || state == LockState.RWC){
            state = LockState.W;
        } else if (state == LockState.RC || state == LockState.R ||state == LockState.NL ) {
            object = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
            state = LockState.W;
        }
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        if(state == LockState.R){
          state = LockState.RC;
        } else if(state == LockState.W || state == LockState.RWC){
            state = LockState.WC;
        }
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
        if(state == LockState.RC){
            state = LockState.NL;
        } else if(state == LockState.R || state == LockState.RWC ){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = LockState.NL;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        if(state == LockState.WC){
            state = LockState.NL;
            return object;
        } else if(state == LockState.RWC ||state == LockState.W){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = LockState.NL;
            return object;
        } else{
            return null;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        if(state == LockState.WC){
            state = LockState.RC;
            return object;
        } else if (state == LockState.RWC){
            state = LockState.R;
            return object;
        } else if(state == LockState.W ) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            state = LockState.RC;
            return object;
        }else{
            return null;
        }
    }
}