package org.docx4j;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.docx4j.dml.CTHyperlink;
import org.docx4j.dml.CTNonVisualDrawingProps;
import org.docx4j.dml.diagram.CTDataModel;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.EndnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.utils.CompoundTraversalUtilVisitorCallback;
import org.docx4j.utils.SingleTraversalUtilVisitorCallback;
import org.docx4j.utils.TraversalUtilVisitor;
import org.docx4j.wml.CTObject;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.Pict;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traverse a list of JAXB objects (eg document.xml's document/body 
 * children), and do something to them.
 * 
 * This is similar to what one could do via XSLT,
 * but avoids marshalling/unmarshalling.  The downside is that
 * not everything will necessarily get traversed here
 * since visitChildren is not (yet) comprehensive.
 * 
 * This utility might be redundant if we used
 * http://code.google.com/p/jaxb-visitor/ or 
 * https://github.com/ops4j/org.ops4j.xvisitor
 * at XJC time. Haven't tried them though.
 * 
 * See also org.docx4j.utils.SingleTraversalUtilVisitorCallback
 * and CompoundTraversalUtilVisitorCallback
 * 
 * @author jharrop, alberto
 *
 */
public class TraversalUtil {
  private static Logger log = LoggerFactory.getLogger(TraversalUtil.class);

  public interface Callback {
    void walkJAXBElements(Object parent);

    List<Object> getChildren(Object o);

    /**
		 * Visits a node in pre order (before its children have been visited).
		 * 
		 * A node is visited only if all its parents have been traversed (
		 * {@link #shouldTraverse(Object)}).</p>
		 * 
		 * <p>
		 * Implementations can have side effects.
		 * </p>
		 */
    List<Object> apply(Object o);

    /**
		 * Decide whether this node's children should be traversed.
		 * 
		 * @return whether the children of this node should be visited
		 */
    boolean shouldTraverse(Object o);
  }

  public static abstract class CallbackImpl implements Callback {
    public void walkJAXBElements(Object parent) {
      List children = getChildren(parent);
      if (children != null) {
        Object o2;
        for (Object o : children) {
          if (o instanceof javax.xml.bind.JAXBElement) {
            o2 = ((JAXBElement) o).getValue();
            if (o2 instanceof Child) {
              if (parent instanceof List) {
                if (log.isDebugEnabled()) {
                  if (((Child) o2).getParent() == null) {
                    log.debug("Unknown parent for " + o2.getClass().getName());
                  } else {
                    log.debug("Parent of " + o2.getClass().getName() + " is currently " + ((Child) o2).getParent().getClass().getName());
                  }
                }
              } else {
                if (parent == ((Child) o2).getParent()) {
                } else {
                  if (((Child) o2).getParent() == null) {
                    if (log.isWarnEnabled()) {
                      log.warn("Unknown parent for " + o2.getClass().getName());
                    }
                    ((Child) o2).setParent(parent);
                  } else {
                    if (log.isWarnEnabled()) {
                      log.warn("Parent of " + o2.getClass().getName() + " is currently " + ((Child) o2).getParent().getClass().getName());
                    }
                  }
                  if (log.isInfoEnabled()) {
                    log.info("setting to  " + parent.getClass().getName());
                  }
                }
              }
            }
          } else {
            o2 = o;
            if (log.isDebugEnabled()) {
              if (o2 instanceof Child) {
                if (((Child) o2).getParent() == null) {
                  log.debug("Unknown parent for " + o2.getClass().getName());
                } else {
                  if (parent != ((Child) o2).getParent()) {
                    log.info("Parent of " + o2.getClass().getName() + " is currently " + ((Child) o2).getParent().getClass().getName());
                  }
                }
              } else {
                log.info(o2.getClass().getName() + " not an instanceof Child!");
              }
            }
          }
          this.apply(o2);
          if (this.shouldTraverse(o2)) {
            walkJAXBElements(o2);
          }
        }
      }
    }

    public List<Object> getChildren(Object o) {
      return TraversalUtil.getChildrenImpl(o);
    }

    /**
		 * Visits a node in pre order (before its children have been visited).

		 * 
		 * A node is visited only if all its parents have been traversed (
		 * {@link #shouldTraverse(Object)}).</p>
		 * 
		 * <p>
		 * Implementations can have side effects.
		 * </p>
		 */
    public abstract List<Object> apply(Object o);

    /**
		 * Decide whether this node's children should be traversed.
		 * 
		 * @return whether the children of this node should be visited
		 */
    public boolean shouldTraverse(Object o) {
      return true;
    }
  }

  Callback cb;

  /**
	 * Traverse the object using your callback.  Invoking this constructor starts the 
	 * traverse, by invoking the callback's walkJAXBElements method.
	 * 
	 * @param parent
	 * @param cb
	 */
  public TraversalUtil(Object parent, Callback cb) {
    this.cb = cb;
    cb.walkJAXBElements(parent);
  }

  static void visitChildrenImpl(Object o) {
  }

  private static List<Object> handleGraphicData(org.docx4j.dml.GraphicData graphicData) {
    List<Object> tmpArtificialList = new ArrayList<Object>();
    if (graphicData.getPic() != null) {
      CTNonVisualDrawingProps picNonVisual = graphicData.getPic().getNvPicPr().getCNvPr();
      if (picNonVisual != null) {
        handleCTNonVisualDrawingProps(picNonVisual, tmpArtificialList);
      }
    }
    if (graphicData.getPic() != null && graphicData.getPic().getBlipFill() != null && graphicData.getPic().getBlipFill().getBlip() != null) {
      log.debug("found CTBlip");
      List<Object> artificialList = new ArrayList<Object>();
      if (!tmpArtificialList.isEmpty()) {
        artificialList.addAll(tmpArtificialList);
      }
      artificialList.add(graphicData.getPic().getBlipFill().getBlip());
      return artificialList;
    } else {
      return graphicData.getAny();
    }
  }

  /**
	 * There can be hyperlinks references in CTNonVisualDrawingProps.
	 * @param drawingProps
	 * @param artificialList
	 */
  private static void handleCTNonVisualDrawingProps(CTNonVisualDrawingProps drawingProps, List<Object> artificialList) {
    if (drawingProps != null) {
      CTHyperlink docPrHyperLink = drawingProps.getHlinkClick();
      if (docPrHyperLink != null) {
        artificialList.add(docPrHyperLink);
      }
    }
  }

  /**
	 * Get the children of some docx content object
	 * (as opposed to pptx, xlsx content).
	 * 
	 * @param o
	 * @return
	 */
  public static List<Object> getChildrenImpl(Object o) {
    if (o == null) {
      log.warn("null passed to getChildrenImpl");
      return null;
    }
    log.debug("getting children of " + o.getClass().getName());
    if (o instanceof org.docx4j.wml.Text) {
      return null;
    }
    if (o instanceof List) {
      return (List<Object>) o;
    } else {
      if (o instanceof org.docx4j.wml.Document) {
        List<Object> artificialList = new ArrayList<Object>();
        artificialList.add(((org.docx4j.wml.Document) o).getBody());
        return artificialList;
      } else {
        if (o instanceof org.docx4j.wml.ContentAccessor) {
          return ((org.docx4j.wml.ContentAccessor) o).getContent();
        } else {
          if (o instanceof org.docx4j.wml.SdtElement) {
            if (((org.docx4j.wml.SdtElement) o).getSdtContent() != null) {
              return ((org.docx4j.wml.SdtElement) o).getSdtContent().getContent();
            } else {
              log.warn("SdtElement is missing content element");
              return null;
            }
          } else {
            if (o instanceof org.docx4j.dml.wordprocessingDrawing.Anchor) {
              org.docx4j.dml.wordprocessingDrawing.Anchor anchor = (org.docx4j.dml.wordprocessingDrawing.Anchor) o;
              List<Object> artificialList = new ArrayList<Object>();
              CTNonVisualDrawingProps drawingProps = anchor.getDocPr();
              if (drawingProps != null) {
                handleCTNonVisualDrawingProps(drawingProps, artificialList);
              }
              if (anchor.getGraphic() != null) {
                log.debug("found a:graphic");
                org.docx4j.dml.Graphic graphic = anchor.getGraphic();
                if (graphic.getGraphicData() != null) {
                  artificialList.addAll(handleGraphicData(graphic.getGraphicData()));
                }
              }
              if (!artificialList.isEmpty()) {
                return artificialList;
              }
            } else {
              if (o instanceof org.docx4j.dml.wordprocessingDrawing.Inline) {
                org.docx4j.dml.wordprocessingDrawing.Inline inline = (org.docx4j.dml.wordprocessingDrawing.Inline) o;
                List<Object> artificialList = new ArrayList<Object>();
                CTNonVisualDrawingProps drawingProps = inline.getDocPr();
                if (drawingProps != null) {
                  handleCTNonVisualDrawingProps(drawingProps, artificialList);
                }
                if (inline.getGraphic() != null) {
                  log.debug("found a:graphic");
                  org.docx4j.dml.Graphic graphic = inline.getGraphic();
                  if (graphic.getGraphicData() != null) {
                    artificialList.addAll(handleGraphicData(graphic.getGraphicData()));
                  }
                  return artificialList;
                }
                if (!artificialList.isEmpty()) {
                  return artificialList;
                }
              } else {
                if (o instanceof Pict) {
                  return ((Pict) o).getAnyAndAny();
                } else {
                  if (o instanceof org.docx4j.dml.picture.Pic) {
                    org.docx4j.dml.picture.Pic dmlPic = ((org.docx4j.dml.picture.Pic) o);
                    if (dmlPic.getBlipFill() != null && dmlPic.getBlipFill().getBlip() != null) {
                      log.debug("found DML Blip");
                      List<Object> artificialList = new ArrayList<Object>();
                      artificialList.add(dmlPic.getBlipFill().getBlip());
                      return artificialList;
                    } else {
                      return null;
                    }
                  } else {
                    if (o instanceof org.docx4j.dml.CTGvmlPicture) {
                      org.docx4j.dml.CTGvmlPicture dmlPic = ((org.docx4j.dml.CTGvmlPicture) o);
                      if (dmlPic.getBlipFill() != null && dmlPic.getBlipFill().getBlip() != null) {
                        log.debug("found DML Blip");
                        List<Object> artificialList = new ArrayList<Object>();
                        artificialList.add(dmlPic.getBlipFill().getBlip());
                        return artificialList;
                      } else {
                        return null;
                      }
                    } else {
                      if (o instanceof org.docx4j.vml.CTShape) {
                        List<Object> artificialList = new ArrayList<Object>();
                        for (JAXBElement<?> j : ((org.docx4j.vml.CTShape) o).getPathOrFormulasOrHandles()) {
                          artificialList.add(j);
                        }
                        return artificialList;
                      } else {
                        if (o instanceof CTDataModel) {
                          CTDataModel dataModel = (CTDataModel) o;
                          List<Object> artificialList = new ArrayList<Object>();
                          artificialList.addAll(dataModel.getPtLst().getPt());
                          artificialList.addAll(dataModel.getCxnLst().getCxn());
                          return artificialList;
                        } else {
                          if (o instanceof org.docx4j.dml.diagram2008.CTDrawing) {
                            return ((org.docx4j.dml.diagram2008.CTDrawing) o).getSpTree().getSpOrGrpSp();
                          } else {
                            if (o instanceof org.docx4j.vml.CTTextbox) {
                              org.docx4j.vml.CTTextbox textBox = ((org.docx4j.vml.CTTextbox) o);
                              if (textBox.getTxbxContent() == null) {
                                return null;
                              } else {
                                return textBox.getTxbxContent().getEGBlockLevelElts();
                              }
                            } else {
                              if (o instanceof CTObject) {
                                CTObject ctObject = (CTObject) o;
                                List<Object> artificialList = new ArrayList<Object>();
                                artificialList.addAll(ctObject.getAnyAndAny());
                                if (ctObject.getControl() != null) {
                                  artificialList.add(ctObject.getControl());
                                }
                                return artificialList;
                              } else {
                                if (o instanceof org.docx4j.dml.CTGvmlGroupShape) {
                                  return ((org.docx4j.dml.CTGvmlGroupShape) o).getTxSpOrSpOrCxnSp();
                                } else {
                                  if (o instanceof org.docx4j.dml.CTGvmlShape) {
                                    org.docx4j.dml.CTGvmlShape sp = (org.docx4j.dml.CTGvmlShape) o;
                                    if (sp != null && sp.getTxSp() != null && sp.getTxSp().getTxBody() != null) {
                                      List<Object> artificialList = new ArrayList<Object>();
                                      artificialList.addAll(sp.getTxSp().getTxBody().getP());
                                      return artificialList;
                                    }
                                    return null;
                                  } else {
                                    if (o instanceof FldChar) {
                                      FldChar fldChar = ((FldChar) o);
                                      List<Object> artificialList = new ArrayList<Object>();
                                      artificialList.add(fldChar.getFldCharType());
                                      if (fldChar.getFfData() != null) {
                                        artificialList.add(fldChar.getFfData());
                                      }
                                      if (fldChar.getFldData() != null) {
                                        artificialList.add(fldChar.getFldData());
                                      }
                                      if (fldChar.getNumberingChange() != null) {
                                        artificialList.add(fldChar.getNumberingChange());
                                      }
                                      return artificialList;
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    log.debug(".. looking for method which returns list ");
    try {
      Method[] methods = o.getClass().getDeclaredMethods();
      for (int i = 0; i < methods.length; i++) {
        Method m = methods[i];
        if (m.getReturnType().getName().equals("java.util.List")) {
          return (List<Object>) m.invoke(o);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    log.debug(".. no list member");
    return null;
  }

  public static void replaceChildren(Object o, List<Object> newChildren) {
    if (o instanceof org.docx4j.wml.ContentAccessor) {
      ((org.docx4j.wml.ContentAccessor) o).getContent().clear();
      ((org.docx4j.wml.ContentAccessor) o).getContent().addAll(newChildren);
    } else {
      if (o instanceof org.docx4j.wml.SdtElement) {
        ((org.docx4j.wml.SdtElement) o).getSdtContent().getContent().clear();
        ((org.docx4j.wml.SdtElement) o).getSdtContent().getContent().addAll(newChildren);
      } else {
        if (o instanceof org.docx4j.wml.CTTxbxContent) {
          ((org.docx4j.wml.CTTxbxContent) o).getEGBlockLevelElts().clear();
          ((org.docx4j.wml.CTTxbxContent) o).getEGBlockLevelElts().addAll(newChildren);
        } else {
          log.info("Don\'t know how to replaceChildren in " + o.getClass().getName());
          if (o instanceof org.w3c.dom.Node) {
            log.warn(" IGNORED " + ((org.w3c.dom.Node) o).getNodeName());
          } else {
          }
        }
      }
    }
  }

  /** 
	 * Use this if there is only a single object type (eg just P's)
	 * you are interested in doing something with.  
	 * 
	 * This method allows you to traverse just the main document
	 * part, or also headers/footers, footnotes/endnotes, and comments
	 * as well. 
	 * 
	 * @param wmlPackage
	 * @param bodyOnly
	 * @param visitor
	 */
  public static void visit(WordprocessingMLPackage wmlPackage, boolean bodyOnly, TraversalUtilVisitor visitor) {
    if (visitor != null) {
      visit(wmlPackage, bodyOnly, new SingleTraversalUtilVisitorCallback(visitor));
    }
  }

  /** 
	 * Use this if there is only a single object type (eg just P's)
	 * you are interested in doing something with.
	 * 
	 * This method is for traversing an arbitrary WML object (eg a table), as opposed to
	 * eg the main document part, or a header.
	 * 
	 * @param parent
	 * @param visitor
	 */
  public static void visit(Object parent, TraversalUtilVisitor visitor) {
    if (visitor != null) {
      visit(parent, new SingleTraversalUtilVisitorCallback(visitor));
    }
  }

  /** 
	 * Use this if there is more than one object type (eg Tables and Paragraphs)
	 * you are interested in doing something with during the traversal.
	 * 
	 * This method allows you to traverse just the main document
	 * part, or also headers/footers, footnotes/endnotes, and comments
	 * as well. 
	 * 
	 * @param wmlPackage
	 * @param bodyOnly
	 * @param visitorList
	 */
  public static void visit(WordprocessingMLPackage wmlPackage, boolean bodyOnly, List<TraversalUtilVisitor> visitorList) {
    CompoundTraversalUtilVisitorCallback callback = null;
    if ((visitorList != null) && (!visitorList.isEmpty())) {
      if (visitorList.size() > 1) {
        visit(wmlPackage, bodyOnly, new CompoundTraversalUtilVisitorCallback(visitorList));
      } else {
        visit(wmlPackage, bodyOnly, visitorList.get(0));
      }
    }
  }

  /** 
	 * Use this if there is more than one object type (eg Tables and Paragraphs)
	 * you are interested in doing something with during the traversal.
	 * 
	 * This method is for traversing an arbitrary WML object (eg a table), as opposed to
	 * eg the main document part, or a header.
	 * 
	 * @param parent
	 * @param visitorList
	 */
  public static void visit(Object parent, List<TraversalUtilVisitor> visitorList) {
    CompoundTraversalUtilVisitorCallback callback = null;
    if ((visitorList != null) && (!visitorList.isEmpty())) {
      if (visitorList.size() > 1) {
        visit(parent, new CompoundTraversalUtilVisitorCallback(visitorList));
      } else {
        visit(parent, visitorList.get(0));
      }
    }
  }

  public static void visit(Object parent, Callback callback) {
    if ((parent != null) && (callback != null)) {
      callback.walkJAXBElements(parent);
    }
  }

  public static void visit(WordprocessingMLPackage wmlPackage, boolean bodyOnly, Callback callback) {
    MainDocumentPart mainDocument = null;
    RelationshipsPart relPart = null;
    List<Relationship> relList = null;
    List<Object> elementList = null;
    if ((wmlPackage != null) && (callback != null)) {
      mainDocument = wmlPackage.getMainDocumentPart();
      callback.walkJAXBElements(mainDocument.getJaxbElement().getBody());
      if (!bodyOnly) {
        relPart = mainDocument.getRelationshipsPart();
        relList = relPart.getRelationships().getRelationship();
        for (Relationship rs : relList) {
          elementList = null;
          if (Namespaces.HEADER.equals(rs.getType())) {
            elementList = ((HeaderPart) relPart.getPart(rs)).getJaxbElement().getContent();
          } else {
            if (Namespaces.FOOTER.equals(rs.getType())) {
              elementList = ((FooterPart) relPart.getPart(rs)).getJaxbElement().getContent();
            } else {
              if (Namespaces.ENDNOTES.equals(rs.getType())) {
                elementList = new ArrayList();
                elementList.addAll(((EndnotesPart) relPart.getPart(rs)).getJaxbElement().getEndnote());
              } else {
                if (Namespaces.FOOTNOTES.equals(rs.getType())) {
                  elementList = new ArrayList();
                  elementList.addAll(((FootnotesPart) relPart.getPart(rs)).getJaxbElement().getFootnote());
                } else {
                  if (Namespaces.COMMENTS.equals(rs.getType())) {
                    elementList = new ArrayList();
                    for (Comment comment : ((CommentsPart) relPart.getPart(rs)).getJaxbElement().getComment()) {
                      elementList.addAll(comment.getEGBlockLevelElts());
                    }
                  }
                }
              }
            }
          }
          if ((elementList != null) && (!elementList.isEmpty())) {
            log.debug("Processing target: " + rs.getTarget() + ", type: " + rs.getType());
            callback.walkJAXBElements(elementList);
          }
        }
      }
    }
  }
}