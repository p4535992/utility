package com.github.p4535992.util.xml.other;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTitleElement;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * Utility class to print out DOM
 */
@SuppressWarnings({"nls","unused"})
public class DomPrintUtil {

  /**
   * Default encoding of this utility. (UTF8)
   */
  public static final String UTF8 = "UTF8";

  private static final String LINE_SEP = System.getProperty("line.separator");
  private static final String EMPTY_STR = "";

  private static final String LT = "<";
  private static final String GT = ">";
  private static final String AMP = "&";
  private static final String QUAT = "\"";
  private static final String SINGLE_QUAT = "'";

  private static final String ESC_LT = "&lt;";
  private static final String ESC_GT = "&gt;";
  private static final String ESC_AMP = "&amp;";

  private final Document document;
  private int whatToShow = NodeFilter.SHOW_ALL;
  private NodeFilter nodeFilter = null;
  private boolean entityReferenceExpansion = false;

  private boolean indent = true;
  private boolean escapeTagBracket = false;

  private AttributeFilter attrFilter = null;

  /**
   * AttributeFilter defines the behavior of a filter that is used for
   * converting attributes of each Element into String.
   */
  public interface AttributeFilter {

    /**
     * Check whether a specified attribute is converted into String.
     *
     * @param element
     *            the target Element
     * @param attr
     *            the target attribute
     * @return true to print the attribute, false to ignore the attribute
     */
    boolean acceptNode(Element element, Node attr);
  }

  /**
   * Constructor of DOM print utility.
   *
   * @param document
   *            the target document
   */
  public DomPrintUtil(Document document) {
    this.document = document;
  }

  private String getXMLString(String targetS) {
    return targetS.replaceAll(AMP, ESC_AMP).replaceAll(LT, ESC_LT)
        .replaceAll(GT, ESC_GT);
  }

  private String getAttributeString(Element element, Node attr) {
    if (null == attrFilter || attrFilter.acceptNode(element, attr)) {
      String value = getXMLString(attr.getNodeValue());
      String quat = QUAT;
      if (value.indexOf(QUAT) > 0) {
        quat = SINGLE_QUAT;
      }
      return " " + attr.getNodeName() + "=" + quat + value + quat;
    }
    return EMPTY_STR;
  }

  private boolean checkNewLine(Node target) {
    if (indent && target.hasChildNodes()) {
      short type = target.getFirstChild().getNodeType();
        return !(type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE);
    }
    return false;
  }

  /**
   * Returns XML text converted from the target DOM
   *
   * @return String format XML converted from the target DOM
   */
  public String toXMLString() {
    StringBuilder tmpSB = new StringBuilder(8192);

    TreeWalkerImpl treeWalker = new TreeWalkerImpl(document, whatToShow,
        nodeFilter, entityReferenceExpansion);

    String lt = escapeTagBracket ? ESC_LT : LT;
    String gt = escapeTagBracket ? ESC_GT : GT;
    String line_sep = indent ? LINE_SEP : EMPTY_STR;

    Node tmpN = treeWalker.nextNode();
    boolean prevIsText = false;

    String indentS = EMPTY_STR;
    while (tmpN != null) {
      short type = tmpN.getNodeType();
      switch (type) {
      case Node.ELEMENT_NODE:
        if (prevIsText) {
          tmpSB.append(line_sep);
        }
        tmpSB.append(indentS).append(lt).append(tmpN.getNodeName());
        NamedNodeMap attrs = tmpN.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
          Node attr = attrs.item(i);
          String value = attr.getNodeValue();
          if (null != value) {
            tmpSB.append(getAttributeString((Element) tmpN, attr));
          }
        }
        if (tmpN instanceof HTMLTitleElement && !tmpN.hasChildNodes()) {
          tmpSB.append(gt).append(((HTMLTitleElement) tmpN).getText());
          prevIsText = true;
        } else if (checkNewLine(tmpN)) {
          tmpSB.append(gt).append(line_sep);
          prevIsText = false;
        } else {
          tmpSB.append(gt);
          prevIsText = true;
        }
        break;
      case Node.TEXT_NODE:
        if (!prevIsText) {
          tmpSB.append(indentS);
        }
        tmpSB.append(getXMLString(tmpN.getNodeValue()));
        prevIsText = true;
        break;
      case Node.COMMENT_NODE:
        String comment;
        if (escapeTagBracket) {
          comment = getXMLString(tmpN.getNodeValue());
        } else {
          comment = tmpN.getNodeValue();
        }
        tmpSB.append(line_sep).append(indentS).append(lt)
                .append("!--").append(comment).append("--").append(gt).append(line_sep);
        prevIsText = false;
        break;
      case Node.CDATA_SECTION_NODE:
        tmpSB.append(line_sep).append(indentS).append(lt).append("!CDATA[")
                .append(tmpN.getNodeValue()).append("]]").append(line_sep);
        break;
      case Node.DOCUMENT_TYPE_NODE:
        if (tmpN instanceof DocumentType) {
          DocumentType docType = (DocumentType) tmpN;
          String pubId = docType.getPublicId();
          String sysId = docType.getSystemId();
          if (null != pubId && pubId.length() > 0) {
            if (null != sysId && sysId.length() > 0) {
              tmpSB.append(lt).append("!DOCTYPE ").append(docType.getName())
                      .append(" PUBLIC \"").append(pubId).append(" \"").append(sysId).append("\">").append(line_sep);
            } else {
              tmpSB.append(lt).append("!DOCTYPE ").append(docType.getName())
                      .append(" PUBLIC \"").append(pubId).append("\">").append(line_sep);
            }
          } else {
            tmpSB.append(lt).append("!DOCTYPE ").append(docType.getName())
                    .append(" SYSTEM \"").append(docType.getSystemId()).append("\">").append(line_sep);
          }
        } else {
          System.out
              .println("Document Type node does not implement DocumentType: "
                  + tmpN);
        }
        break;
      default:
        System.out.println(tmpN.getNodeType() + " : "
            + tmpN.getNodeName());
      }

      Node next = treeWalker.firstChild();
      if (null != next) {
        if (type == Node.ELEMENT_NODE && indent) {
          indentS = indentS + " ";
        }
        tmpN = next;
        continue;
      }

      if (tmpN.getNodeType() == Node.ELEMENT_NODE) {
        tmpSB.append(lt).append("/").append(tmpN.getNodeName()).append(gt).append(line_sep);
        prevIsText = false;
      }

      next = treeWalker.nextSibling();
      if (null != next) {
        tmpN = next;
        continue;
      }

      tmpN = null;
      next = treeWalker.parentNode();
      while (null != next) {
        if (next.getNodeType() == Node.ELEMENT_NODE) {
          if (indent) {
            if (indentS.length() > 0) {
              indentS = indentS.substring(1);
            } else {
              System.err.println("indent: " + next.getNodeName()
                  + " " + next);
            }
          }
          tmpSB.append(line_sep).append(indentS).append(lt).append("/")
                  .append(next.getNodeName()).append(gt).append(line_sep);
          prevIsText = false;
        }
        next = treeWalker.nextSibling();
        if (null != next) {
          tmpN = next;
          break;
        }
        next = treeWalker.parentNode();
      }
    }
    return tmpSB.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return toXMLString();
  }

  /**
   * Set whatToShow attribute to TreeWalker used in the utility.
   *
   * @param whatToShow
   *            the attribute determines which types of node are presented via
   *            the TreeWalker. The values are defined in the NodeFilter
   *            interface.
   * @see TreeWalkerImpl
   */
  public void setWhatToShow(int whatToShow) {
    this.whatToShow = whatToShow;
  }

  /**
   * Set NodeFilter to TreeWalker used in the utility.
   *
   * @param nodeFilter
   *            the filter used to screen nodes
   * @see TreeWalkerImpl
   */
  public void setNodeFilter(NodeFilter nodeFilter) {
    this.nodeFilter = nodeFilter;
  }

  /**
   * Set the entity reference expansion flag to TreeWalker used in the
   * utility.
   * @param entityReferenceExpansion
   *            the flag to determine whether the children of entity reference
   *            nodes are visible to TreeWalker.
   * @see TreeWalkerImpl
   */
  public void setEntityReferenceExpansion(boolean entityReferenceExpansion) {
    this.entityReferenceExpansion = entityReferenceExpansion;
  }

  /**
   * Set the number of space characters used for indent
   * @param indent the number of space characters used for indent
   */
  public void setIndent(boolean indent) {
    this.indent = indent;
  }

  /**
   * Determine to escape Tag bracket ('&lt;','&gt;') or not. Please set true if you
   * want to print out DOM into &lt;pre&gt; section of HTML.
   * @param escapeTagBracket
   *            if true, print Tag bracket as escaped format ({@literal '&lt;',
   *            '&gt;'})
   *
   */
  public void setEscapeTagBracket(boolean escapeTagBracket) {
    this.escapeTagBracket = escapeTagBracket;
  }

  /**
   * Set AttributeFilter to define the behavior for printing attributes of
   * each Element.
   * @param attrFilter the AttributeFilter to set
   */
  public void setAttrFilter(AttributeFilter attrFilter) {
    this.attrFilter = attrFilter;
  }

  /**
   * Method to Print out the target Document.
   * @param filePath the target file path.
   * @throws IOException throw if the File Output directory not exists.
   */
  public void writeToFile(String filePath) throws IOException {
    writeToFile(new File(filePath), UTF8);
  }

  /**
   * Method to Print out the target Document.
   * @param file the target File
   * @throws IOException throw if the File Output directory not exists.
   */
  public void writeToFile(File file) throws IOException {
    writeToFile(file, UTF8);
  }

  /**
   * Method to Print out the target Document in specified encoding
   * @param filePath the target file path
   * @param encode the target encoding
   * @throws IOException throw if the File Output directory not exists.
   */
  public void writeToFile(String filePath, String encode) throws IOException {
    writeToFile(new File(filePath), encode);
  }

  /**
   * Method to Print out the target Document in specified encoding
   * @param file the target file
   * @param encode the target encoding
   * @throws IOException throw if the File Output directory not exists.
   */
  public void writeToFile(File file, String encode) throws IOException {
      try (PrintWriter tmpPW = new PrintWriter(new OutputStreamWriter(
              new FileOutputStream(file), encode))) {
          tmpPW.println(toXMLString());
          tmpPW.flush();
      }
  }

}



class TreeWalkerImpl implements TreeWalker {

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
 class WhatToShowNodeFilter implements NodeFilter {

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
