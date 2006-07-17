package org.intermine.dataloader;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.List;

import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;

import org.apache.log4j.Logger;

/**
 * Object for holding hint data for the getEquivalentObjects method in IntegrationWriters.
 *
 * @author Matthew Wakeling
 */
public class EquivalentObjectHints
{
    private static final Logger LOG = Logger.getLogger(EquivalentObjectHints.class);

    private boolean databaseEmptyChecked = false;
    private boolean databaseEmpty = false;

    private ObjectStore os;

    /**
     * Constructor.
     *
     * @param os an ObjectStore of a production database
     */
    public EquivalentObjectHints(ObjectStore os) {
        this.os = os;
    }

    /**
     * Returns true if the database was empty at the start of the run.
     *
     * @return a boolean
     */
    public boolean databaseEmpty() {
        if (databaseEmptyChecked) {
            return databaseEmpty;
        }
        // Okay, we haven't run the query yet.
        try {
            Query q = new Query();
            QueryClass qc = new QueryClass(InterMineObject.class);
            q.addFrom(qc);
            q.addToSelect(qc);
            List results = os.execute(q, 0, 1, false, false, os.getSequence());
            if (results.isEmpty()) {
                databaseEmpty = true;
            }
            databaseEmptyChecked = true;
        } catch (ObjectStoreException e) {
            LOG.error("Error checking database", e);
            return false;
        }
        return databaseEmpty;
    }

    /**
     * Returns true if there were no instances of the given class in the database.
     *
     * @param clazz the class, must be in the model
     * @return a boolean
     */
    public boolean classNotExists(Class clazz) {
        // For now, we can just ignore all the parameters, and check to see if the database is empty
        // or not.
        return databaseEmpty();
    }

    /**
     * Returns true if there were no instances of the given class with the given field set to the
     * given value.
     *
     * @param clazz the class, must be in the model
     * @param fieldName the name of the field
     * @param value the value
     * @return a boolean
     */
    public boolean pkQueryFruitless(Class clazz, String fieldName, Object value) {
        // For now, we can just ignore all the parameters, and check to see if the database is empty
        // or not.
        return databaseEmpty();
    }
}
