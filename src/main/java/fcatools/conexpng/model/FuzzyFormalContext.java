package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalAttributeException;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcaapi.utils.IndexedSet;
import de.tudresden.inf.tcs.fcalib.FullObject;

/**
 * A specialization of FormalContext<String,String> with the aim to remove the
 * verbose repetition of <String,String>. Plus, adds a couple of useful methods.
 * Due to the API of FormalContext<String,String> the here implemented methods
 * are extremely inefficient.
 */
public class FuzzyFormalContext extends FormalContext {

	// Extra wrapper sets and maps to hold objects and allobjectsofAttribute
    protected HashMap<String, SortedSet<String>> allObjectsOfAttribute = new HashMap<>();
    protected HashMap<OAPair<String,String>,Double> composition = new HashMap<>();
    private double threshold = 0.5;

    @Override
    public boolean addAttribute(String attribute) throws IllegalAttributeException {
        if (super.addAttribute(attribute)) {
            allObjectsOfAttribute.put(attribute, new TreeSet<String>());
            return true;
        } else
            return false;
    }

 
    public boolean addAttributeToObject(String attribute, String id, double value) throws IllegalAttributeException,
            IllegalObjectException {
    	if(!allObjectsOfAttribute.containsKey(attribute))
    		allObjectsOfAttribute.put(attribute, new TreeSet<String>());
    	allObjectsOfAttribute.get(attribute).add(id);
    	if(value>=threshold) {
    		if (!super.addAttributeToObject(attribute,id)) return false;
        	composition.put(new OAPair<String,String>(attribute,id), value);
    	}
    	return true;
    }
    
    public boolean addAttributeToObject(String attribute, String id) throws IllegalAttributeException,
    IllegalObjectException {
    	return addAttributeToObject(attribute,id,threshold);
}


    public boolean addObject(FullObject<String, String> o, double[] values) throws IllegalObjectException {
   	    int i=0;
   	    for (String attribute : o.getDescription().getAttributes()) {
                composition.put(new OAPair<String,String>(attribute,o.getIdentifier()), values[i]);
                if(values[i]>=threshold) super.addObject(o);
               	if(!allObjectsOfAttribute.containsKey(attribute))
            		allObjectsOfAttribute.put(attribute, new TreeSet<String>());
            	allObjectsOfAttribute.get(attribute).add(o.getIdentifier());
                i++;
            }
    	    return true;
    }

    public boolean addObject(FullObject<String, String> o, double value) throws IllegalObjectException {
	    for (String attribute : o.getDescription().getAttributes()) {
        	allObjectsOfAttribute.get(attribute).add(o.getIdentifier());
            composition.put(new OAPair<String,String>(attribute,o.getIdentifier()), value);
    		if(value>=threshold) return super.addAttributeToObject(attribute,o.getIdentifier());
        }
	    return true;
}
    public boolean addObject(FullObject<String, String> o) throws IllegalObjectException {
    	return addObject(o,threshold);
    }
    
    public boolean removeAttributeFromObject(String attribute, String id) throws IllegalAttributeException,
            IllegalObjectException {
    	super.removeAttributeFromObject(attribute, id);
        SortedSet<String> objects = allObjectsOfAttribute.get(attribute);
        if (objects != null) {
            objects.remove(id);
            composition.remove(new OAPair<String,String>(attribute,id));
        	return true;
        }
        return false;
    }

    @Override
    public boolean removeObject(String id) throws IllegalObjectException {
        return removeObject(getObject(id));
    }

    @Override
    public boolean removeObject(FullObject<String, String> object) throws IllegalObjectException {
    	
    	// remove from all objects
    	boolean removed = getObjects().remove(object);
		if (!removed) {
			throw new IllegalObjectException("Object" + object.getIdentifier() + "not successfully removed");
		}
		
        if (super.removeObject(object)) {
            for (String attribute : object.getDescription().getAttributes()) {
                allObjectsOfAttribute.get(attribute).remove(object.getIdentifier());
                if(objectsOfAttribute.get(attribute).contains(object.getIdentifier()))
                	super.removeObject(object);
                composition.remove(new OAPair<String,String>(attribute,object.getIdentifier()));
            }
        }
        return false;
    }

    public FuzzyFormalContext() {
        this(0.5);
      }

    public FuzzyFormalContext(double threshold) {
        super();
        allObjectsOfAttribute = new HashMap<>(); 
        this.threshold = threshold;
    }
    
    public FuzzyFormalContext(int objectsCount, int attributesCount) {
      this(objectsCount,attributesCount,0.5);
    	
    }
    public FuzzyFormalContext(int objectsCount, int attributesCount, double threshold) {
        super(objectsCount, attributesCount);
        allObjectsOfAttribute = new HashMap<>(); 
        this.threshold = threshold;
        for (int i = 0; i < attributesCount; i++) {
            for (int j = 0; j < objectsCount; j++) {
            	composition.put(new OAPair<String,String>("attr" + i, "obj" + j), 0.0);
        }
        }
    }

    @Override
	public String toString() {
		return "FuzzyFormalContext("+threshold+"):" + objectsOfAttribute.keySet().toString() + "\n"+
				this.objects.toString();
	}

    /* Return all objects*/
    public IndexedSet<FullObject<String, String>> getObjects() {
        return objects;
    }

    /**
     * Removes given object only without removing attributes.
     * 
     * @param o
     *            object to remove
     */
    public void removeObjectOnly(FullObject<String, String> o) {
    	
    	if(objects.contains(o))
        objects.remove(o);
    }

    public void transpose() {
  
        HashMap<OAPair<String,String>,Double> newComposition = new HashMap<>();
        for(OAPair<String,String> key : composition.keySet()) {
        	newComposition.put(new OAPair<String,String>(key.getObject(),key.getAttribute()),composition.get(key));
        }
        composition = newComposition;
        HashMap<String, SortedSet<String>> newAllObjectsOfAttribute = new HashMap<>();
        allObjectsOfAttribute.clear();
   
        for(String attribute : allObjectsOfAttribute.keySet())
        	for(String obj: allObjectsOfAttribute.get(attribute)) {
        		if(!newAllObjectsOfAttribute.containsKey(obj))
        			newAllObjectsOfAttribute.put(obj,new TreeSet<String>());
                newAllObjectsOfAttribute.get(obj).add(attribute);
        	}
        allObjectsOfAttribute = newAllObjectsOfAttribute;
        super.transpose();

    }

    public void toggleAttributeForObject(String attribute, String objectID) {
    	OAPair<String,String> oapair = new OAPair<String,String>(attribute,objectID);
        if (objectHasAttribute(getObject(objectID), attribute)) {
            try {
                removeAttributeFromObject(attribute, objectID);
                this.composition.put(oapair,threshold/2);
            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.addAttributeToObject(attribute, objectID);
                this.composition.put(new OAPair<String,String>(attribute,objectID),threshold);

                if(!allObjectsOfAttribute.containsKey(attribute))
        			allObjectsOfAttribute.put(attribute,new TreeSet<String>());
                if(!allObjectsOfAttribute.get(attribute).contains(objectID))
                	allObjectsOfAttribute.get(attribute).add(objectID);

            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        }
    }

    public void renameAttribute(String oldName, String newName) {
    	super.renameAttribute(oldName, newName);
    	if (allObjectsOfAttribute.containsKey(oldName)) {

    	  for(String object: allObjectsOfAttribute.get(oldName)) {
    		this.composition.put(new OAPair<>(newName,object), this.composition.get(new OAPair<>(oldName,object)));
    		this.composition.remove(new OAPair<>(oldName,object));

    	  }
    		
    		allObjectsOfAttribute.put(newName,allObjectsOfAttribute.get(oldName));
    		allObjectsOfAttribute.remove(oldName);

    	}
    }

    public void renameObject(String oldObject, String newObject) {
    	super.renameObject(oldObject, newObject);
    	
  	  for(String attribute: allObjectsOfAttribute.keySet()) {
  	   	if(allObjectsOfAttribute.get(attribute).contains(oldObject)) {
  	   		this.composition.put(new OAPair<>(attribute,newObject), this.composition.get(new OAPair<>(attribute,oldObject)));
  	   		this.composition.remove(new OAPair<>(attribute,oldObject));
  	   	}

  	   	for (SortedSet<String> objects : allObjectsOfAttribute.values()) {
            if (objects.contains(oldObject)) {
                objects.remove(oldObject);
                objects.add(newObject);
            }
        }
 
  	  }

    }
    
  
    public void removeAttribute(String attribute) {
    	super.removeAttribute(attribute);
    	if(allObjectsOfAttribute.containsKey(attribute)) {
    	for(String obj: allObjectsOfAttribute.get(attribute))
    		this.composition.remove(new OAPair<>(attribute,obj));
    	allObjectsOfAttribute.remove(attribute);
    	}
    }

    // Should not be used outside the context editor
    public void removeAttributeInternal(String attribute) {
    	super.removeAttributeInternal(attribute);
    	if(allObjectsOfAttribute.containsKey(attribute)) {
    	for(String obj: allObjectsOfAttribute.get(attribute))
    		this.composition.remove(new OAPair<>(attribute,obj));
    	allObjectsOfAttribute.remove(attribute);
    	}
    }
    
    public TreeSet<String> intersection(Set<String> firstSet, Set<String> secondSet) {
        TreeSet<String> result = new TreeSet<String>();
        for (String s : firstSet) {
            for (String t : secondSet) {
                if (s.equals(t)) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    public TreeSet<String> sort(Set<String> sortable) {
        TreeSet<String> result = new TreeSet<String>();
        for (String s : sortable) {
            result.add(s);
        }
        return result;
    }

    /**
     * Set Attribute which don't be consider by lattice computation.
     * 
     * @param attr
     */
    public void dontConsiderAttribute(String attr) {
        this.getDontConsideredAttr().add(attr);
    }

    /**
     * Set Attribute which has to be reconsider by lattice computation.
     * 
     * @param attr
     */
    public void considerAttribute(String attr) {
        this.getDontConsideredAttr().remove(attr);
    }

    /**
     * Set Object which don't be consider by lattice computation.
     * 
     * @param obj
     */
    public void dontConsiderObject(FullObject<String, String> obj) {
        this.getDontConsideredObj().add(obj);
    }

    /**
     * Set Object which has to be reconsider by lattice computation.
     * 
     * @param obj
     */
    public void considerObject(FullObject<String, String> obj) {
        this.getDontConsideredObj().remove(obj);
    }

    public void clearConsidered() {
    	this.getDontConsideredAttr().clear();
        getDontConsideredObj().clear();
    }

    public ArrayList<String> getDontConsideredAttr() {
        return this.getDontConsideredAttr();
    }

    public ArrayList<FullObject<String, String>> getDontConsideredObj() {
        return getDontConsideredObj();
    }

}
