package com.github.p4535992.util.xml.impl;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
public class WhatToShowNodeFilter implements NodeFilter {

    private final int filter;

    public WhatToShowNodeFilter(int whatToShow) {
        this.filter = whatToShow;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.NodeFilter#acceptNode(org.w3c.dom.Node)
     */
    @Override
    public short acceptNode(Node arg0) {
        if (null == arg0) {
            return FILTER_REJECT;
        }

        if ((filter & (1 << (arg0.getNodeType() - 1))) != 0) {
            return FILTER_ACCEPT;
        }
        return FILTER_SKIP;
    }
}