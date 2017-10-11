/*
 * (c) Copyright 2014 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 12.01.2014 by Andreas Beckers
 */
package de.beckers.members.model;


/**
 * @author Andreas Beckers
 */
public enum DocumentType {
    REGISTRATION(false, "REG"),
    CERTIFICATE(false, "CERT"),
    ID(false, "ID"),
    PHOTO(false, "PHOTO"),
    PASS(true, "PASS"),
    ATTACHMENT(true, "ATT");

    private final boolean _needsTeam;
    private final String _shortName;

    private DocumentType(boolean needsTeam, String shortName) {
        _needsTeam = needsTeam;
        _shortName = shortName;
    }

    public boolean needsTeam() {
        return _needsTeam;
    }

    public String getShortName() {
        return _shortName;
    }
}
