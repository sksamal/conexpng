package fcaviewtab;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import java.util.Collection;
import java.util.Iterator;

public class FormalContextSetter
{
  private FormalContextAdapter adapter;
  private KnowledgeBase kb;
  
  public FormalContextSetter(KnowledgeBase kb, Collection objects, String[] attributes, int typeIndex)
  {
    this.kb = kb;
    this.adapter = new FormalContextAdapter();
    if (typeIndex == 0) {
      setContextForBoolean(objects, attributes);
    } else if (typeIndex == 1) {
      setContextForMultiple(objects, attributes);
    }
  }
  
  private void setContextForBoolean(Collection objects, String[] attributes)
  {
    Iterator it = objects.iterator();
    while (it.hasNext())
    {
      Instance instance = (Instance)it.next();
      this.adapter.addFormalObject(instance);
      for (int i = 0; i < attributes.length; i++)
      {
        Slot slot = this.kb.getSlot(attributes[i]);
        this.adapter.addFormalAttribute(slot);
        Collection values = instance.getDirectOwnSlotValues(slot);
        if (!values.isEmpty())
        {
          Boolean b = (Boolean)instance.getDirectOwnSlotValue(slot);
          if (b.booleanValue()) {
            this.adapter.setRelation(instance, slot);
          }
        }
      }
    }
  }
  
  private void setContextForMultiple(Collection objects, String[] attributes)
  {
    Iterator it = objects.iterator();
    while (it.hasNext())
    {
      Instance instance = (Instance)it.next();
      this.adapter.addFormalObject(instance);
      for (int i = 0; i < attributes.length; i++)
      {
        Slot slot = this.kb.getSlot(attributes[i]);
        Collection values = instance.getDirectOwnSlotValues(slot);
        if (!values.isEmpty())
        {
          Iterator itv = values.iterator();
          while (itv.hasNext())
          {
            Instance slotIns = (Instance)itv.next();
            this.adapter.addFormalAttribute(slotIns);
            this.adapter.setRelation(instance, slotIns);
          }
        }
      }
    }
  }
  
  public FormalContextAdapter getContextAdapter()
  {
    return this.adapter;
  }
}
