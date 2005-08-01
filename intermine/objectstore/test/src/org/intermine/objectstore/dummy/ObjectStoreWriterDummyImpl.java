package org.intermine.objectstore.dummy;

import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsInfo;

import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreWriter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

/**
 * A dummy ObjectStoreWriter to help with testing.  Most methods throw an
 * UnsupportedOperationException.
 *
 * @author Kim Rutherford
 */

public class ObjectStoreWriterDummyImpl implements ObjectStoreWriter
{
    private Map storedObjects = new HashMap();
    private ObjectStore os;
    
    // used to generate unique IDs
    private int idCounter = 0;

    /**
     * Create a new ObjectStoreWriterDummyImpl object.
     */
    public ObjectStoreWriterDummyImpl(ObjectStore os) {
        this.os = os;
    }

    public ObjectStore getObjectStore() {
        return os;
    }

    public void store(InterMineObject o) throws ObjectStoreException {
        if (o.getId() == null) {
            while (storedObjects.containsKey(new Integer(idCounter))) {
                idCounter++;
            }
            o.setId(new Integer(idCounter));
            storedObjects.put(new Integer(idCounter), o);
        } else {
            storedObjects.put(o.getId(), o);
        }
    }

    public void delete(InterMineObject o) throws ObjectStoreException {
        storedObjects.remove(o.getId());
    }

    public boolean isInTransaction() throws ObjectStoreException {
       throw new UnsupportedOperationException();
    }

    public void beginTransaction() throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public void commitTransaction() throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public void abortTransaction() throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public void close() throws ObjectStoreException {
       // do nothing
    }

    public Results execute(Query q) throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public List execute(Query q, int start, int limit, boolean optimise, boolean explain,
                        int sequence) throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public InterMineObject getObjectById(Integer id) throws ObjectStoreException {
        return (InterMineObject) storedObjects.get(id);
    }

    public InterMineObject getObjectById(Integer id, Class clazz) throws ObjectStoreException {
        return getObjectById(id);
    }

    public List getObjectsByIds(Collection ids) throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public void prefetchObjectById(Integer id) {
        throw new UnsupportedOperationException();
    }

    public void invalidateObjectById(Integer id) {
        throw new UnsupportedOperationException();
    }

    public Object cacheObjectById(Integer id, InterMineObject obj) {
        throw new UnsupportedOperationException();
    }

    public void flushObjectById() {
        throw new UnsupportedOperationException();
    }

    public InterMineObject pilferObjectById(Integer id) {
        throw new UnsupportedOperationException();
    }

    public ResultsInfo estimate(Query q) throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public int count(Query q, int sequence) throws ObjectStoreException {
        throw new UnsupportedOperationException();
    }

    public Model getModel() {
        return os.getModel();
    }

    public InterMineObject getObjectByExample(InterMineObject o, Set fieldNames) {
        throw new UnsupportedOperationException();
    }

    public boolean isMultiConnection() {
        throw new UnsupportedOperationException();
    }

    public int getSequence() {
        throw new UnsupportedOperationException();
    }

    public int getMaxLimit() {
        throw new UnsupportedOperationException();
    }

    public int getMaxOffset() {
        throw new UnsupportedOperationException();
    }

    public long getMaxTime() {
        throw new UnsupportedOperationException();
    }

    public Map getStoredObjects() {
        return storedObjects;
    }
}
