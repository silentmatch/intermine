package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.FileConverter;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

/**
 * A FileConverter that automatically sets the dataSets collection of objects as they are stored.
 *
 * @author Kim Rutherford
 */

public abstract class BioFileConverter extends FileConverter
{
    private final Map<String, String> dataSets = new HashMap<String, String>();
    private final Map<String, String> dataSources = new HashMap<String, String>();
    private Set<String> synonyms = new HashSet<String>();
    private Set<String> crossReferences = new HashSet<String>();
    private Map<String, String> organisms = new HashMap<String, String>();

    /**
     * Create a new BioFileConverter.
     * @param writer the Writer used to output the resultant items
     * @param model the data model
     * @param dataSourceName the DataSource name
     * @param dataSetTitle the DataSet title
     */
    public BioFileConverter (ItemWriter writer, Model model,
                             String dataSourceName, String dataSetTitle) {
        super(writer, model);
        String dataSource = null;
        String dataSet = null;
        if (StringUtils.isNotEmpty(dataSourceName) && StringUtils.isNotEmpty(dataSetTitle)) {
            dataSource = getDataSource(dataSourceName);
            dataSet = getDataSet(dataSetTitle, dataSource);
        }
        setStoreHook(new BioStoreHook(model, dataSet, dataSource));
    }


    /**
     * Create a new BioFileConverter.
     * @param writer the Writer used to output the resultant items
     * @param model the data model
     */
    public BioFileConverter (ItemWriter writer, Model model) {
        super(writer, model);
        setStoreHook(new BioStoreHook(model, "", ""));
    }


    /**
     * Return a DataSource item for the given title
     * @param name the DataSource name
     * @return the DataSource Item
     */
    public String getDataSource(String name) {
        if (name == null) {
            return null;
        }
        String refId = dataSources.get(name);
        if (refId == null) {
            Item dataSource = createItem("DataSource");
            dataSource.setAttribute("name", name);
            try {
                store(dataSource);
            } catch (ObjectStoreException e) {
                throw new RuntimeException("failed to store DataSource with name: " + name, e);
            }
            refId = dataSource.getIdentifier();
            dataSources.put(name, refId);
        }
        return refId;
    }


    /**
     * Return a DataSet ref with the given details.
     *
     * @param title the DataSet title
     * @param dataSourceRefId the DataSource referenced by the the DataSet
     * @return the DataSet Item
     */
    public String getDataSet(String title, String dataSourceRefId) {
        String refId = dataSets.get(title);
        if (refId == null) {
            Item dataSet = createItem("DataSet");
            dataSet.setAttribute("name", title);
            dataSet.setReference("dataSource", dataSourceRefId);
            try {
                store(dataSet);
            } catch (ObjectStoreException e) {
                throw new RuntimeException("failed to store DataSet with title: " + title, e);
            }
            refId = dataSet.getIdentifier();
            dataSets.put(title, refId);
        }
        return refId;
    }

    /**
     * Create a new Synonym.  Keeps a map of already processed synonyms, ignores duplicates.
     * The "store" param should be true only if the subject has already been stored.  Storing a
     * synonym first can signficantly slow down the build process.
     * @param item object
     * @param type the Synonym type, eg. identifier, name
     * @param value the Synonym value
     * @param isPrimary true if this is a primary identifier, false if not, null if don't know
     * @param store if true, will store item
     * @throws ObjectStoreException if the synonym can't be stored
     * @throws SAXException if the synonym can't be stored
     * @return the synonym item or null if this is a duplicate
     */
    public Item createSynonym(Item item, String type, String value, String isPrimary,
            boolean store)
        throws SAXException, ObjectStoreException {
        return createSynonym(item.getIdentifier(), type, value, isPrimary, store);
    }

    /**
     * Create a new Synonym.  Keeps a map of already processed synonyms, ignores duplicates.
     * The "store" param should be true only if the subject has already been stored.  Storing a
     * synonym first can signficantly slow down the build process.
     * @param subjectId id representing the object (eg. Gene) this synonym describes.
     * @param type the Synonym type, eg. identifier, name
     * @param value the Synonym value
     * @param isPrimary true if this is a primary identifier, false if not, null if don't know
     * @param store if true, will store item
     * @throws ObjectStoreException if the synonym can't be stored
     * @throws SAXException if the synonym can't be stored
     * @return the synonym item or null if this is a duplicate
     */
    public Item createSynonym(String subjectId, String type, String value, String isPrimary,
            boolean store)
        throws SAXException, ObjectStoreException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String key = subjectId + type + value;
        if (!synonyms.contains(key)) {
            Item synonym = createItem("Synonym");
            synonym.setAttribute("type", type);
            synonym.setAttribute("value", value);
            synonym.setReference("subject", subjectId);
            if (!StringUtils.isEmpty(isPrimary)) {
                synonym.setAttribute("isPrimary", isPrimary);
            }
            synonyms.add(key);
            if (store) {
                store(synonym);
            }
            return synonym;
        }
        return null;
    }

    /**
     * Create a new CrossReference.  Keeps a map of already processed items, ignores duplicates.
     * The "store" param should be true only if the subject has already been stored.  Storing a
     * CrossReference first can signficantly slow down the build process.
     * @param subjectId id representing the object (eg. Gene) this CrossReference describes.
     * @param value identifier
     * @param dataSource external database
     * @param store if true, will store item
     * @throws ObjectStoreException if the synonym can't be stored
     * @throws SAXException if the synonym can't be stored
     * @return the synonym item or null if this is a duplicate
     */
    public Item createCrossReference(String subjectId, String value, String dataSource,
            boolean store)
        throws SAXException, ObjectStoreException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String key = subjectId + value;
        if (!crossReferences.contains(key)) {
            Item item = createItem("CrossReference");
            item.setAttribute("identifier", value);
            item.setReference("subject", subjectId);
            item.setReference("source", getDataSource(dataSource));
            crossReferences.add(key);
            if (store) {
                store(item);
            }
            return item;
        }
        return null;
    }

    /**
     * The Organism item created from the taxon id passed to the constructor.
     * @param taxonId NCBI taxonomy id of organism to create
     * @return the refId representing the Organism Item
     */
    public String getOrganism(String taxonId) {
        String refId = organisms.get(taxonId);
        if (refId == null) {
            Item organism = createItem("Organism");
            organism.setAttribute("taxonId", taxonId);
            try {
                store(organism);
            } catch (ObjectStoreException e) {
                throw new RuntimeException("failed to store organism with taxonId: " + taxonId, e);
            }
            refId = organism.getIdentifier();
            organisms.put(taxonId, refId);
        }
        return refId;
    }

    /**
     * Make a Location between a Feature and a Chromosome.
     * @param chromosomeId Chromosome Item identifier
     * @param sequenceFeatureId the Item identifier of the feature
     * @param start the start position
     * @param end the end position
     * @param strand the strand
     * @return the new Location object
     */
    protected Item makeLocation(String chromosomeId, String sequenceFeatureId, int start, int end,
            int strand) {
        Item location = createItem("Location");

        if (start < end) {
            location.setAttribute("start", String.valueOf(start));
            location.setAttribute("end", String.valueOf(end));
        } else {
            location.setAttribute("start", String.valueOf(end));
            location.setAttribute("end", String.valueOf(start));
        }
        location.setAttribute("strand", String.valueOf(strand));
        location.setReference("locatedOn", chromosomeId);
        location.setReference("feature", sequenceFeatureId);

        try {
            store(location);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("failed to store location", e);
        }

        return location;
    }
}
