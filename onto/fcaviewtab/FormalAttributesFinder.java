package jp.ac.hokudai.med.guoqian.fcaviewtab;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class FormalAttributesFinder
{
  private KnowledgeBase kb;
  private String clsName;
  private Collection formalObjects;
  private Collection allTemplateSlots;
  
  public FormalAttributesFinder(KnowledgeBase kb, String clsName)
  {
    this.kb = kb;
    this.clsName = clsName;
    this.formalObjects = getFormalObjects();
    this.allTemplateSlots = getAllTemplateSlots();
  }
  
  private Vector getSlotsWithBooleanType()
  {
    Vector results = new Vector();
    Iterator it = this.allTemplateSlots.iterator();
    while (it.hasNext())
    {
      Slot slot = (Slot)it.next();
      if (slot.getValueType() == ValueType.BOOLEAN) {
        results.add(slot.getBrowserText());
      }
    }
    return results;
  }
  
  private Vector getSlotsWithMultipleInstanceType()
  {
    Vector results = new Vector();
    Iterator it = this.allTemplateSlots.iterator();
    while (it.hasNext())
    {
      Slot slot = (Slot)it.next();
      if ((slot.getValueType() == ValueType.INSTANCE) && (slot.getAllowsMultipleValues())) {
        results.add(slot.getBrowserText());
      }
    }
    return results;
  }
  
  public String[] getPossibleFormatAttributesForBoolean()
  {
    Vector slots = getSlotsWithBooleanType();
    String[] results = new String[slots.size()];
    slots.copyInto(results);
    return results;
  }
  
  public String[] getPossibleFormalAttributesForMultiple()
  {
    Vector slots = getSlotsWithMultipleInstanceType();
    String[] results = new String[slots.size()];
    slots.copyInto(results);
    return results;
  }
  
  public Collection getFormalObjects()
  {
    Cls cls = this.kb.getCls(this.clsName);
    Collection instances = cls.getDirectInstances();
    return instances;
  }
  
  private Collection getAllTemplateSlots()
  {
    Cls cls = this.kb.getCls(this.clsName);
    Collection templateSlots = cls.getTemplateSlots();
    return templateSlots;
  }
}
