/*
 * (c) Copyright 2013 Troisdorf Jets
 * All Rights Reserved.
 *
 * created 20.11.2013 by Andreas Beckers
 */
package de.beckers.members.model;

/**
 * @author Andreas Beckers
 */
public enum RelationType {
    PARENT,
    GRANDPARENT,
    SIBLING,
    CHILD,
    GRANDCHILD;

    public RelationType getOpposite() {
        switch (this) {
        case CHILD:
            return PARENT;
        case GRANDPARENT:
            return GRANDCHILD;
        case PARENT:
            return CHILD;
        case SIBLING:
            return SIBLING;
        case GRANDCHILD:
            return GRANDPARENT;
        default:
            return null;
        }
    }
}
