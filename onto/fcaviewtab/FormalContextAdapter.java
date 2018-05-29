package fcaviewtab;

import conexp.core.ConceptsCollection;
import conexp.core.Context;
import conexp.core.ContextEntity;
import conexp.core.FCAEngineRegistry;
import conexp.core.Lattice;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FormalContextAdapter
  extends FormalContextInterface
{
  private Context context;
  private Vector entities;
  private Map formalObjects;
  private Map formalAttributes;
  
  public FormalContextAdapter()
  {
    this.context = FCAEngineRegistry.makeContext(1, 1);
    
    this.entities = new Vector();
    this.formalObjects = new HashMap();
    this.formalAttributes = new HashMap();
  }
  
  public void addFormalObject(Instance object)
  {
    int o = this.formalObjects.size();
    if (!this.formalObjects.containsKey(object))
    {
      this.formalObjects.put(object, new Integer(o + 1));
      String objname = object.getBrowserText();
      ContextEntity entity = ContextEntity.createContextObject(objname);
      this.entities.add(entity);
      this.context.addObject(entity);
    }
  }
  
  public void addFormalAttribute(Instance attribute)
  {
    int a = this.formalAttributes.size();
    if (!this.formalAttributes.containsKey(attribute))
    {
      this.formalAttributes.put(attribute, new Integer(a + 1));
      String attname = attribute.getBrowserText();
      ContextEntity entity = ContextEntity.createContextAttribute(attname);
      this.entities.add(entity);
      this.context.addAttribute(entity);
    }
  }
  
  public void addFormalAttribute(Slot attribute)
  {
    int a = this.formalAttributes.size();
    if (!this.formalAttributes.containsKey(attribute))
    {
      this.formalAttributes.put(attribute, new Integer(a + 1));
      String attname = attribute.getBrowserText();
      ContextEntity entity = ContextEntity.createContextAttribute(attname);
      this.entities.add(entity);
      this.context.addAttribute(entity);
    }
  }
  
  private void addObjectIfNotExisted(Instance object)
  {
    int o = this.formalObjects.size();
    if (!this.formalObjects.containsKey(object)) {
      this.formalObjects.put(object, new Integer(o + 1));
    }
  }
  
  private void addAttributeIfNotExisted(Slot attribute)
  {
    int a = this.formalAttributes.size();
    if (!this.formalAttributes.containsKey(attribute)) {
      this.formalAttributes.put(attribute, new Integer(a + 1));
    }
  }
  
  public void setRelation(Instance object, Instance attribute)
  {
    Integer o = (Integer)this.formalObjects.get(object);
    Integer a = (Integer)this.formalAttributes.get(attribute);
    this.context.setRelationAt(o.intValue(), a.intValue(), true);
  }
  
  public void setRelation(Instance object, Slot attribute)
  {
    Integer o = (Integer)this.formalObjects.get(object);
    Integer a = (Integer)this.formalAttributes.get(attribute);
    this.context.setRelationAt(o.intValue(), a.intValue(), true);
  }
  
  public Lattice getLattice()
  {
    return FCAEngineRegistry.buildLattice(this.context);
  }
  
  public ConceptsCollection getConceptsCollection()
  {
    return FCAEngineRegistry.buildConceptSet(this.context);
  }
  
  public Context getContext()
  {
    this.context.removeAttribute(0);
    this.context.removeObject(0);
    return this.context;
  }
}
