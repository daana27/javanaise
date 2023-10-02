package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{

    Serializable object;

    JvnObjectImpl(Serializable o){
        object = o;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'jvnLockRead'");
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
