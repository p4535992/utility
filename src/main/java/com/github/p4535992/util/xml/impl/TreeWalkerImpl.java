package com.github.p4535992.util.xml.impl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class TreeWalkerImpl implements TreeWalker {

    private Node walkerRoot;
    private Node current;
    private int whatToShow;
    private NodeFilter filter;
    private NodeFilter defaultFilter;
    private boolean entitiyReferenceExpansion;
    private boolean noFilter = true;

    public TreeWalkerImpl(Node root, int whatToShow, NodeFilter filter,
                          boolean entityReferenceExpansion) throws DOMException {
        if (null == root) {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR,
                    "Root can't be a null.");
        }
        this.walkerRoot = root;
        this.current = root;
        this.whatToShow = whatToShow;
        this.filter = filter;
        this.noFilter = (null == filter);
        this.entitiyReferenceExpansion = entityReferenceExpansion;
        this.defaultFilter = new WhatToShowNodeFilter(whatToShow);
    }

    private short eval(Node target) {
        short flag = defaultFilter.acceptNode(target);

        // If the node is skipped by whatToShow flag, a NodeFiilter will not be
        // called.
        if (noFilter || flag == NodeFilter.FILTER_SKIP) {
            return flag;
        }
        return filter.acceptNode(target);
    }

    @SuppressWarnings("fallthrough")
    private Node getVisibleNextSibling(Node target, Node root) {
        if (target == root) {
            return null;
        }
        Node tmpN = target.getNextSibling();
        if (null == tmpN) {
            Node tmpP = target.getParentNode();
            if (eval(tmpP) == NodeFilter.FILTER_SKIP) {
                return getVisibleNextSibling(tmpP, root);
            }
            return null;
        }
        switch (eval(tmpN)) {
            case NodeFilter.FILTER_ACCEPT:
                return tmpN;
            case NodeFilter.FILTER_SKIP:
                Node tmpC = getVisibleFirstChild(tmpN);
                if (null != tmpC) {
                    return tmpC;
                }
                // case NodeFilter.FILTER_REJECT:
            default:
                return getVisibleNextSibling(tmpN, root);
        }
    }

    @SuppressWarnings("fallthrough")
    private Node getVisiblePreviousSibling(Node target, Node root) {
        if (target == root) {
            return null;
        }
        Node tmpN = target.getPreviousSibling();
        if (null == tmpN) {
            Node tmpP = target.getParentNode();
            if (eval(tmpP) == NodeFilter.FILTER_SKIP) {
                return getVisiblePreviousSibling(tmpP, root);
            }
            return null;
        }
        switch (eval(tmpN)) {
            case NodeFilter.FILTER_ACCEPT:
                return tmpN;
            case NodeFilter.FILTER_SKIP:
                Node tmpC = getVisibleLastChild(tmpN);
                if (null != tmpC) {
                    return tmpC;
                }
                // case NodeFilter.FILTER_REJECT:
            default:
                return getVisiblePreviousSibling(tmpN, root);
        }
    }

    @SuppressWarnings("fallthrough")
    private Node getVisibleFirstChild(Node target) {
        if (!entitiyReferenceExpansion
                && Node.ENTITY_REFERENCE_NODE == target.getNodeType()) {
            return null;
        }
        Node tmpN = target.getFirstChild();
        if (null == tmpN) {
            return null;
        }

        switch (eval(tmpN)) {
            case NodeFilter.FILTER_ACCEPT:
                return tmpN;
            case NodeFilter.FILTER_SKIP:
                Node tmpN2 = getVisibleFirstChild(tmpN);
                if (null != tmpN2) {
                    return tmpN2;
                }
                // case NodeFilter.FILTER_REJECT:
            default:
                return getVisibleNextSibling(tmpN, target);
        }
    }

    @SuppressWarnings("fallthrough")
    private Node getVisibleLastChild(Node target) {
        if (!entitiyReferenceExpansion
                && Node.ENTITY_REFERENCE_NODE == target.getNodeType()) {
            return null;
        }
        Node tmpN = target.getLastChild();
        if (null == tmpN) {
            return null;
        }

        switch (eval(tmpN)) {
            case NodeFilter.FILTER_ACCEPT:
                return tmpN;
            case NodeFilter.FILTER_SKIP:
                Node tmpN2 = getVisibleLastChild(tmpN);
                if (null != tmpN2) {
                    return tmpN2;
                }
                // case NodeFilter.FILTER_REJECT:
            default:
                return getVisiblePreviousSibling(tmpN, target);
        }
    }

    private Node getVisibleParent(Node target) {
        if (target == walkerRoot) {
            return null;
        }
        Node tmpN = target.getParentNode();
        if (null == tmpN) {
            return null;
        }
        switch (eval(tmpN)) {
            case NodeFilter.FILTER_ACCEPT:
                return tmpN;
            // case NodeFilter.FILTER_SKIP:
            // case NodeFilter.FILTER_REJECT:
            default:
                return getVisibleParent(tmpN);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#firstChild()
     */
    @Override
    public Node firstChild() {
        Node result = getVisibleFirstChild(current);
        if (null != result) {
            current = result;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#getCurrentNode()
     */
    @Override
    public Node getCurrentNode() {
        return current;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#getExpandEntityReferences()
     */
    @Override
    public boolean getExpandEntityReferences() {
        return entitiyReferenceExpansion;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#getFilter()
     */
    @Override
    public NodeFilter getFilter() {
        return filter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#getRoot()
     */
    @Override
    public Node getRoot() {
        return walkerRoot;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#getWhatToShow()
     */
    @Override
    public int getWhatToShow() {
        return whatToShow;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#lastChild()
     */
    @Override
    public Node lastChild() {
        Node result = getVisibleLastChild(current);
        if (null != result) {
            current = result;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#nextNode()
     */
    @Override
    public Node nextNode() {
        // search child
        Node tmpN = getVisibleFirstChild(current);
        if (null != tmpN) {
            current = tmpN;
            return tmpN;
        }

        // search sibling
        tmpN = getVisibleNextSibling(current, walkerRoot);
        if (null != tmpN) {
            current = tmpN;
            return tmpN;
        }

        // search parent's sibling
        Node tmpP = getVisibleParent(current);
        while (null != tmpP) {
            tmpN = getVisibleNextSibling(tmpP, walkerRoot);
            if (null != tmpN) {
                current = tmpN;
                return tmpN;
            } else {
                tmpP = getVisibleParent(tmpP);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#nextSibling()
     */
    @Override
    public Node nextSibling() {
        Node result = getVisibleNextSibling(current, walkerRoot);
        if (null != result) {
            current = result;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#parentNode()
     */
    @Override
    public Node parentNode() {
        Node result = getVisibleParent(current);
        if (null != result) {
            current = result;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#previousNode()
     */
    @Override
    public Node previousNode() {
        // search previous sibling
        Node tmpN = getVisiblePreviousSibling(current, walkerRoot);
        // no sibling, search parent
        if (null == tmpN) {
            tmpN = getVisibleParent(current);
            if (null != tmpN) {
                current = tmpN;
                return tmpN;
            }
            return null;
        }

        // search last child of previous sibling
        Node tmpC = getVisibleLastChild(tmpN);
        while (null != tmpC) {
            tmpN = tmpC;
            tmpC = getVisibleLastChild(tmpN);
        }
        //if (tmpN != null) {
        current = tmpN;
        return tmpN;
        //}
        //return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#previousSibling()
     */
    @Override
    public Node previousSibling() {
        Node result = getVisiblePreviousSibling(current, walkerRoot);
        if (null != result) {
            current = result;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.traversal.TreeWalker#setCurrentNode(org.w3c.dom.Node)
     */
    @Override
    public void setCurrentNode(Node arg0) {
        if (arg0 == null) {
            System.out.println("Current node can't be null.");
        }
        current = arg0;
    }

}