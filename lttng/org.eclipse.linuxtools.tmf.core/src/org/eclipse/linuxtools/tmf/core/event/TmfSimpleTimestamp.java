/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Francois Chouinard - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.tmf.core.event;

/**
 * <b><u>TmfSimpleTimestamp</u></b>
 * <p>
 * A simplified timestamp where scale and precision are set to 0.
 */
public class TmfSimpleTimestamp extends TmfTimestamp {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Default constructor
     */
    public TmfSimpleTimestamp() {
        this(0);
    }

    /**
     * Full constructor
     *
     * @param value the timestamp value
     */
    public TmfSimpleTimestamp(long value) {
        super(value, 0, 0);
    }

    /**
     * Copy constructor
     * 
     * @param timestamp the timestamp to copy
     */
    public TmfSimpleTimestamp(TmfSimpleTimestamp timestamp) {
        if (timestamp == null || timestamp.getScale() != 0 || timestamp.getPrecision() != 0)
            throw new IllegalArgumentException();
        fValue = timestamp.getValue();
        fScale = 0;
        fPrecision = 0;
    }

    // ------------------------------------------------------------------------
    // ITmfTimestamp
    // ------------------------------------------------------------------------

    @Override
    public ITmfTimestamp normalize(long offset, int scale) throws ArithmeticException {
        if (scale == 0) {
            return new TmfSimpleTimestamp(fValue + offset);
        }
        return super.normalize(offset, scale);
    }

    @Override
    public int compareTo(ITmfTimestamp ts, boolean withinPrecision) {
        if (ts instanceof TmfSimpleTimestamp) {
            long delta = fValue - ts.getValue();
            return (delta == 0) ? 0 : (delta > 0) ? 1 : -1;
        }
        return super.compareTo(ts, withinPrecision);
    }

    @Override
    public ITmfTimestamp getDelta(ITmfTimestamp ts) {
        if (ts instanceof TmfSimpleTimestamp) {
            return new TmfSimpleTimestamp(fValue - ts.getValue());
        }
        return super.getDelta(ts);
    }

    // ------------------------------------------------------------------------
    // Object
    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (!(other instanceof TmfSimpleTimestamp))
            return super.equals(other);
        TmfSimpleTimestamp ts = (TmfSimpleTimestamp) other;
        return compareTo(ts, false) == 0;
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return "TmfSimpleTimestamp [fValue=" + fValue + ", fScale=" + fScale + ", fPrecision=" + fPrecision + "]";
    }

}