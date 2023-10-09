package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{
    State state;
    Serializable object;
    int joi;
    JvnObjectImpl(Serializable o){
        object = o;
        state = State.NL;
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
        JvnServerImpl.jvnGetServer().jvnLockRead(joi);
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnLockWrite'");
    }

    @Override
    public void jvnUnLock() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnUnLock'");
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnGetObjectId'");
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnGetSharedObject'");
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnInvalidateReader'");
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnInvalidateWriter'");
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnInvalidateWriterForReader'");
    }



}
