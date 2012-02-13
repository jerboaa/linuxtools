/*******************************************************************************
 * Copyright (c) 2009, 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Francois Chouinard - Initial API and implementation
 *   Francois Chouinard - Updated as per TMF Event Model 1.0
 *******************************************************************************/

package org.eclipse.linuxtools.tmf.core.event;

import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;

/**
 * <b><u>TmfDataItem</u></b>
 * <p>
 * A basic implementation of ITmfDataItem.
 * 
 * Notice that for performance reasons TmfDataEvent is NOT immutable. If a copy
 * of an event is needed, use the copy constructor (shallow copy) or the clone()
 * method (deep copy).
 */
public class TmfDataItem implements ITmfDataItem {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    protected ITmfTrace<? extends TmfDataItem> fTrace;
    protected long fRank;
    protected String fSource;
    protected ITmfEventType fType;
    protected ITmfEventContent fContent;
    protected String fReference;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Default constructor
     */
    public TmfDataItem() {
    }

    /**
     * Full constructor
     * 
     * @param trace the parent trace
     * @param rank the vent rank (in the trace)
     * @param source the event source
     * @param type the event type
     * @param reference the event reference
     */
    public TmfDataItem(ITmfTrace<? extends TmfDataItem> trace, long rank,
            String source, TmfEventType type, TmfEventContent content,
            String reference)
    {
        fTrace = trace;
        fRank = rank;
        fSource = source;
        fType = type;
        fContent = content;
        fReference = reference;
    }

    /**
     * Copy constructor
     * 
     * @param event the original event
     */
    public TmfDataItem(TmfDataItem event) {
        if (event == null)
            throw new IllegalArgumentException();
        fTrace = event.fTrace;
        fRank = event.fRank;
        fSource = event.fSource;
        fType = event.fType;
        fContent = event.fContent;
        fReference = event.fReference;
    }

    // ------------------------------------------------------------------------
    // ITmfDataItem
    // ------------------------------------------------------------------------

    /**
     * @return the parent trace
     */
    public ITmfTrace<? extends TmfDataItem> getTrace() {
        return fTrace;
    }

    /**
     * @return the event rank
     */
    public long getRank() {
        return fRank;
    }

    /**
     * @return the event source
     */
    public String getSource() {
        return fSource;
    }

    /**
     * @return the event type
     */
    public ITmfEventType getType() {
        return fType;
    }

    /**
     * @return the event content
     */
    public ITmfEventContent getContent() {
        return fContent;
    }

    /**
     * @return the event reference
     */
    public String getReference() {
        return fReference;
    }

    // ------------------------------------------------------------------------
    // Convenience setters
    // ------------------------------------------------------------------------

    /**
     * @param source the new event source
     */
    public void setSource(String source) {
        fSource = source;
    }

    /**
     * @param type the new event type
     */
    public void setType(TmfEventType type) {
        fType = type;
    }

    /**
     * @param content the event new content
     */
    public void setContent(TmfEventContent content) {
        fContent = content;
    }

    /**
     * @param reference the new event reference
     */
    public void setReference(String reference) {
        fReference = reference;
    }

    // ------------------------------------------------------------------------
    // Cloneable
    // ------------------------------------------------------------------------

    @Override
    public TmfDataItem clone() {
        TmfDataItem clone = null;
        try {
            clone = (TmfDataItem) super.clone();
            clone.fTrace = fTrace;
            clone.fRank = fRank;
            clone.fSource = fSource;
            clone.fType = fType != null ? fType.clone() : null;
            clone.fContent = fContent != null ? fContent.clone() : null;
            clone.fReference = fReference;
        } catch (CloneNotSupportedException e) {
        }
        return clone;
    }

    // ------------------------------------------------------------------------
    // Object
    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fContent == null) ? 0 : fContent.hashCode());
        result = prime * result + (int) (fRank ^ (fRank >>> 32));
        result = prime * result + ((fReference == null) ? 0 : fReference.hashCode());
        result = prime * result + ((fSource == null) ? 0 : fSource.hashCode());
        result = prime * result + ((fTrace == null) ? 0 : fTrace.hashCode());
        result = prime * result + ((fType == null) ? 0 : fType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TmfDataItem other = (TmfDataItem) obj;
        if (fContent == null) {
            if (other.fContent != null)
                return false;
        } else if (!fContent.equals(other.fContent))
            return false;
        if (fRank != other.fRank)
            return false;
        if (fReference == null) {
            if (other.fReference != null)
                return false;
        } else if (!fReference.equals(other.fReference))
            return false;
        if (fSource == null) {
            if (other.fSource != null)
                return false;
        } else if (!fSource.equals(other.fSource))
            return false;
        if (fTrace == null) {
            if (other.fTrace != null)
                return false;
        } else if (!fTrace.equals(other.fTrace))
            return false;
        if (fType == null) {
            if (other.fType != null)
                return false;
        } else if (!fType.equals(other.fType))
            return false;
        return true;
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return "TmfDataEvent [fTrace=" + fTrace + ", fRank=" + fRank
                + ", fSource=" + fSource + ", fType=" + fType + ", fContent="
                + fContent + ", fReference=" + fReference + "]";
    }

}