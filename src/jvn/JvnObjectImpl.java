package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{
    LockState state;
    Serializable object;
    int joi;
    JvnObjectImpl(Serializable o, int joi){
        object = o;
        state = LockState.NL;
        this.joi = joi;
    }

    @Override
    public synchronized void jvnLockRead() throws JvnException {
        System.out.println("lockRead: joi de l objet = " + joi + " et state = " + state);
        if(state == LockState.RC){
            state = LockState.R;
        } else if(state == LockState.NL){
            System.out.println("object jvnLockRead: NL");
            object = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
            state = LockState.R;
        } else if(state == LockState.W || state == LockState.WC){
            state = LockState.RWC;
        }
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
        System.out.println("lockWrite: joi de l objet = " + joi + " et state = " + state);
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
        } else if(state == LockState.W){
            state = LockState.WC;
        }
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
        if(state == LockState.RC){
            System.out.println("object: invalidate reader " + state);
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
        System.out.println("object: invalidateWriter, current state = " + state);
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
            System.out.println("etat non concordant avec invalidate writer");
            return null;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        System.out.println("object: invalidateWriterForReader, current state = " + state);
        if(state == LockState.WC){
            state = LockState.RC;
            return object;
        } else if (state == LockState.RWC || state == LockState.R){
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
            System.out.println("etat non concordant avec invalidate writer for reader");
            return null;
        }
    }
}