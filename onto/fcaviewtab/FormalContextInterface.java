package fcaviewtab;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

public abstract class FormalContextInterface
{
  public abstract void addFormalObject(Instance paramInstance);
  
  public abstract void addFormalAttribute(Slot paramSlot);
  
  public abstract void setRelation(Instance paramInstance, Slot paramSlot);
}
