package org.intermine.bio.web.model;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Map;

/**
 * This is a Java Bean to represent Cytoscape Web node data.
 * Easy to be extended.
 *
 * @author Fengyuan Hu
 *
 */
public class CytoscapeNetworkNodeData
{
    private String soureceId; // TODO change to id
    private String sourceLabel; // sometimes no values
    private String featueType; //e.g. miRNA/TF
    private String canonicalName;
    private String position;
    private String organism;
    private Map<String, String> extraInfo;

    /**
     * Contructor
     */
    public CytoscapeNetworkNodeData() {
        soureceId = null;
        sourceLabel = null;
        featueType = null;
        canonicalName = null;
        position = null;
        organism = null;
        extraInfo = null;
    }

    /**
     * @return the soureceId
     */
    public String getSoureceId() {
        return soureceId;
    }

    /**
     * @param soureceId the soureceId to set
     */
    public void setSoureceId(String soureceId) {
        this.soureceId = soureceId;
    }

    /**
     * @return the sourceLabel
     */
    public String getSourceLabel() {
        return sourceLabel;
    }

    /**
     * @param sourceLabel the sourceLabel to set
     */
    public void setSourceLabel(String sourceLabel) {
        this.sourceLabel = sourceLabel;
    }

    /**
     * @return the featueType
     */
    public String getFeatueType() {
        return featueType;
    }

    /**
     * @param featueType the featueType to set
     */
    public void setFeatueType(String featueType) {
        this.featueType = featueType;
    }

    /**
     * @return the canonicalName
     */
    public String getCanonicalName() {
        return canonicalName;
    }

    /**
     * @param canonicalName the canonicalName to set
     */
    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the organism
     */
    public String getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(String organism) {
        this.organism = organism;
    }

    /**
     * @return the extraInfo
     */
    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    /**
     * @param extraInfo the extraInfo to set
     */
    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * @param obj a CytoscapeNetworkNodeData object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CytoscapeNetworkNodeData) {
            CytoscapeNetworkNodeData m = (CytoscapeNetworkNodeData) obj;
            return (soureceId.equals(m.getSoureceId()));
        }
        return false;
    }

    /**
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return soureceId.hashCode();
    }
}
