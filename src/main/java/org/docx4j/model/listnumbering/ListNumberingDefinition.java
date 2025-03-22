package org.docx4j.model.listnumbering;
import org.docx4j.XmlUtils;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.Numbering;
import org.docx4j.wml.Numbering.Num.LvlOverride.StartOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents:
 * 
	  <w:num w:numId="1">
	    <w:abstractNumId w:val="0"/>
	  </w:num>
	  
	  or 
	  
	  <w:num w:numId="2">
	    <w:abstractNumId w:val="0"/>
	    <w:lvlOverride w:ilvl="0">
	      <w:startOverride w:val="10"/>
	    </w:lvlOverride>
	  </w:num>
	  	    
	    (layered on top of the JAXB object representing same)
    
 */
public class ListNumberingDefinition {
  private Numbering.Num numNode;

  public Numbering.Num getNumNode() {
    return numNode;
  }

  protected static Logger log = LoggerFactory.getLogger(ListNumberingDefinition.class);

  /**
     * @param numNode
     * @param abstractListDefinitions
     */
  public ListNumberingDefinition(Numbering.Num numNode, HashMap<String, AbstractListNumberingDefinition> abstractListDefinitions) {
    this.numNode = numNode;
    this.listNumberId = numNode.getNumId().toString();
    if (log.isDebugEnabled()) {
      log.debug("Constructing model for numId=" + listNumberId);
      log.debug(XmlUtils.marshaltoString(numNode));
    }
    Numbering.Num.AbstractNumId abstractNumNode = numNode.getAbstractNumId();
    if (abstractNumNode == null) {
      log.warn("No abstractNumId on w:numId=" + listNumberId);
    } else {
      log.debug("concrete " + listNumberId + " points to abstract list " + abstractNumNode.getVal().toString());
      this.abstractListDefinition = abstractListDefinitions.get(abstractNumNode.getVal().toString());
      if (abstractListDefinition == null) {
        log.warn("No abstractListDefinition for w:numId=" + listNumberId);
        return;
      }
      if (log.isDebugEnabled()) {
        log.debug(XmlUtils.marshaltoString(abstractListDefinition.getAbstractNumNode()));
      }
      if (this.abstractListDefinition.getLevelCount() == 0 && this.abstractListDefinition.hasLinkedStyle()) {
      }
      this.levels = new HashMap<String, ListLevel>(this.abstractListDefinition.getLevelCount());
      Iterator listLevelIterator = this.abstractListDefinition.getListLevels().entrySet().iterator();
      while (listLevelIterator.hasNext()) {
        Map.Entry pairs = (Map.Entry) listLevelIterator.next();
        this.levels.put((String) pairs.getKey(), new ListLevel((ListLevel) pairs.getValue()));
      }
      List<Numbering.Num.LvlOverride> levelOverrideNodes = numNode.getLvlOverride();
      if (levelOverrideNodes != null) {
        for (Numbering.Num.LvlOverride overrideNode : levelOverrideNodes) {
          if (log.isDebugEnabled()) {
            log.debug("found LvlOverride " + XmlUtils.marshaltoString(overrideNode, true));
          }
          if (overrideNode.getIlvl() == null) {
            if (log.isWarnEnabled()) {
              log.warn("Missing @w:ilvl! " + XmlUtils.marshaltoString(overrideNode, true));
            }
          } else {
            String overrideLevelId = overrideNode.getIlvl().toString();
            log.debug(".. " + overrideLevelId);
            if (!overrideLevelId.equals("")) {
              StartOverride startOverride = overrideNode.getStartOverride();
              if (startOverride != null && startOverride.getVal() != null) {
                if (this.levels.get(overrideLevelId) == null) {
                  throw new RuntimeException(overrideLevelId + " level missing for abstractListDefinition " + abstractListDefinition.getID());
                }
                this.levels.get(overrideLevelId).setStartValue(startOverride.getVal().subtract(BigInteger.ONE));
                log.debug("level " + overrideLevelId + "starts at " + startOverride.getVal());
              }
            }
            Lvl lvl = overrideNode.getLvl();
            if (lvl != null && this.levels.get(overrideLevelId) != null) {
              this.levels.get(overrideLevelId).SetOverrides(lvl);
            }
          }
        }
      }
    }
  }

  private AbstractListNumberingDefinition abstractListDefinition;

  public AbstractListNumberingDefinition getAbstractListDefinition() {
    return abstractListDefinition;
  }

  private HashMap<String, ListLevel> levels;

  public ListLevel getLevel(String ilvl) {
    return levels.get(ilvl);
  }

  public void IncrementCounter(String level) {
    int otherLevelInt;
    String otherLevelStr;
    if (!this.levels.get(level).getCounter().isEncounteredAlready()) {
      otherLevelInt = Integer.parseInt(level) - 1;
      otherLevelStr = Integer.toString(otherLevelInt);
      while (this.levels.containsKey(otherLevelStr) && !this.levels.get(otherLevelStr).getCounter().isEncounteredAlready()) {
        log.debug("Increment lower level " + otherLevelStr);
        this.levels.get(otherLevelStr).IncrementCounter();
        otherLevelInt--;
        otherLevelStr = Integer.toString(otherLevelInt);
      }
    }
    log.debug("Increment level " + level);
    this.levels.get(level).IncrementCounter();
    otherLevelInt = Integer.parseInt(level) + 1;
    otherLevelStr = Integer.toString(otherLevelInt);
    while (this.levels.containsKey(otherLevelStr)) {
      log.debug("Reset level " + otherLevelInt);
      this.levels.get(otherLevelStr).ResetCounter();
      otherLevelInt++;
      otherLevelStr = Integer.toString(otherLevelInt);
    }
  }

  private String listNumberId;

  public String getListNumberId() {
    return this.listNumberId;
  }

  /**
     * returns a String containing the current state of the counters, up to the indicated level
     * 
     * @param level
     * @return
     */
  public String GetCurrentNumberString(String level) {
    ListLevel controllingLvl = this.levels.get(level);
    boolean isLegal = level.equals("1") && controllingLvl.getJaxbAbstractLvl().getIsLgl() != null && controllingLvl.getJaxbAbstractLvl().getIsLgl().isVal();
    String formatString = controllingLvl.getLevelText();
    log.debug("levelText: " + formatString);
    StringBuilder result = new StringBuilder();
    String temp = "";
    for (int i = 0; i < formatString.length(); i++) {
      temp = formatString.substring(i, i + 1);
      if (temp.equals("%")) {
        if (i < formatString.length() - 1) {
          String formatStringLevel = formatString.substring(i + 1, i + 2);
          int levelId = Integer.parseInt(formatStringLevel) - 1;
          ListLevel lvl = this.levels.get(Integer.toString(levelId));
          if (levelId == 0 && isLegal) {
            result.append(lvl.getCurrentValueUnformatted());
          } else {
            result.append(lvl.getCurrentValueFormatted());
          }
          i++;
        }
      } else {
        result.append(temp);
      }
    }
    return result.toString();
  }

  public String GetFont(String level) {
    return this.levels.get(level).getFont();
  }

  public boolean IsBullet(String level) {
    return this.levels.get(level).IsBullet();
  }

  public boolean LevelExists(String level) {
    if (this.levels == null) {
      log.info("No levels present in abstractNumId");
      if (getAbstractListDefinition() == null) {
        log.info("[missing]");
      } else {
        log.info(XmlUtils.marshaltoString(getAbstractListDefinition()));
      }
      log.debug("referenced from ");
      log.debug(XmlUtils.marshaltoString(numNode));
      return false;
    }
    return this.levels.containsKey(level);
  }
}