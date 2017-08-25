package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.FCAImplication;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalAttributeException;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcaapi.utils.IndexedSet;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.Implication;
import de.tudresden.inf.tcs.fcalib.ImplicationSet;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;

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

 
    public boolean addAttributeToObject(String attribute, String id, double value) throws IllegalAttributeException,
            IllegalObjectException {
    	allObjectsOfAttribute.put(attribute, new TreeSet<String>());
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


    public boolean addObject(FullObject<String, String> o, double value) throws IllegalObjectException {
    	if(value>=threshold) super.addObject(o);
    	
        if (super.super.addObject(o)) {
            for (String attribute : o.getDescription().getAttributes()) {
            	allObjectsOfAttribute.get(attribute).add(o.getIdentifier());
                if(value>=threshold) objectsOfAttribute.get(attribute).add(o.getIdentifier());
                composition.put(new OAPair<String,String>(attribute,o.getIdentifier()), value);
            }
            return true;
        }
        return false;
    }

    public boolean addObject(FullObject<String, String> arg0) throws IllegalObjectException {
    	return addObject(arg0,threshold);
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
    	boolean removed = getAllObjects().remove(object);
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
    public IndexedSet<FullObject<String, String>> getAllObjects() {
        return allObjects;
    }

    /**
     * Removes given object only without removing attributes.
     * 
     * @param o
     *            object to remove
     */
    public void removeObjectOnly(FullObject<String, String> o) {
    	if(allObjects.contains(o))
            allObjects.remove(o);
    	
    	if(objects.contains(o))
        objects.remove(o);
    }

    public void transpose() {
  
        HashMap<OAPair<String,String>,Double> newComposition = new HashMap<>();
        
        for(OAPair<String,String> key : composition.keySet()) {
        	newComposition.put(new OAPair<String,String>(key.getObject(),key.getAttribute()),composition.get(key));
        }
        composition = newComposition;
        /* Do we need to create and add newObjects */

        IndexedSet<FullObject<String, String>> newObjects = new ListSet<>();
        IndexedSet<String> newAttributes = new ListSet<>();
        for (String attribute : getAttributes()) {
            IndexedSet<String> objectsForAttribute = new ListSet<>();
            for (FullObject<String, String> object : objects) {
                if (objectHasAttribute(object, attribute))
                    objectsForAttribute.add(object.getIdentifier());
            }
            newObjects.add(new FullObject<>(attribute, objectsForAttribute));
        }
        for (FullObject<String, String> object : objects) {
            newAttributes.add(object.getIdentifier());
        }
 
        // All objs
        IndexedSet<FullObject<String, String>> newAllObjects = new ListSet<>();
        for (String attribute : getAttributes()) {
            IndexedSet<String> objectsForAttribute = new ListSet<>();
            for (FullObject<String, String> obj : allObjects) {
                if (obj.getDescription().containsAttribute(attribute)
)                   objectsForAttribute.add(obj.getIdentifier());
            }
            newAllObjects.add(new FullObject<>(attribute, objectsForAttribute));
        }
        for (FullObject<String, String> object : allObjects) {
            newAttributes.add(object.getIdentifier());
        }

        objects = newObjects;
        allObjects = newAllObjects;

        getAttributes().clear();
        objectsOfAttribute.clear();
        for (String attribute : newAttributes) {
            getAttributes().add(attribute);
            objectsOfAttribute.put(attribute, new TreeSet<String>());
        }
        for (FullObject<String, String> object : objects) {
            for (String attribute : object.getDescription().getAttributes()) {
                objectsOfAttribute.get(attribute).add(object.getIdentifier());
            }
        }
    }

    public void toggleAttributeForObject(String attribute, String objectID) {
        if (objectHasAttribute(getObject(objectID), attribute)) {
            try {
                removeAttributeFromObject(attribute, objectID);
            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.addAttributeToObject(attribute, objectID);
            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        }
    }

    public void invert(int objectStartIndex, int objectEndIndex, int attributeStartIndex, int attributeEndIndex) {
        for (int i = objectStartIndex; i < objectEndIndex; i++) {
            for (int j = attributeStartIndex; j < attributeEndIndex; j++) {
            	String objectID = getObjectAtIndex(i).getIdentifier();
                String attribute = getAttributeAtIndex(j);
                toggleAttributeForObject(attribute, objectID);
            }
        }
    }

    public void invert() {
        invert(0, getObjectCount() - 1, 0, getAttributeCount() - 1);
    }

    public void clear(int objectStartIndex, int objectEndIndex, int attributeStartIndex, int attributeEndIndex) {
        for (int i = objectStartIndex; i < objectEndIndex; i++) {
            for (int j = attributeStartIndex; j < attributeEndIndex; j++) {
                FullObject<String, String> object = getObjectAtIndex(i);
                String attribute = getAttributeAtIndex(j);
                if (objectHasAttribute(object, attribute)) {
                    try {
                        removeAttributeFromObject(attribute, object.getIdentifier());
                    } catch (IllegalObjectException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void clear() {
        clear(0, getObjectCount() - 1, 0, getAttributeCount() - 1);
    }

    public void fill(int objectStartIndex, int objectEndIndex, int attributeStartIndex, int attributeEndIndex) {
        for (int i = objectStartIndex; i < objectEndIndex; i++) {
            for (int j = attributeStartIndex; j < attributeEndIndex; j++) {
                FullObject<String, String> object = getObjectAtIndex(i);
                String attribute = getAttributeAtIndex(j);
                if (!objectHasAttribute(object, attribute)) {
                    try {
                        addAttributeToObject(attribute, object.getIdentifier());
                    } catch (IllegalObjectException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void fill() {
        fill(0, getObjectCount() - 1, 0, getAttributeCount() - 1);
    }

    public void renameAttribute(String oldName, String newName) {
        IndexedSet<String> newAttributes = new ListSet<>();
        IndexedSet<FullObject<String, String>> filteredObjects = new ListSet<>();
        for (FullObject<String, String> object : objects) {
            if (objectHasAttribute(object, oldName)) {
                filteredObjects.add(object);
                try {
                    removeAttributeFromObject(oldName, object.getIdentifier());
                } catch (IllegalObjectException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String attribute : getAttributes()) {
            if (attribute.equals(oldName)) {
                newAttributes.add(newName);
            } else {
                newAttributes.add(attribute);
            }
        }
        getAttributes().clear();
        for (String attribute : newAttributes) {
            getAttributes().add(attribute);
        }
        for (FullObject<String, String> object : filteredObjects) {
            try {
                addAttributeToObject(newName, object.getIdentifier());
            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        }
    }

    public void renameObject(String oldObject, String newObject) {
        IndexedSet<FullObject<String, String>> newObjects = new ListSet<>();
        // IndexedSet<String> filteredAttributes = new ListSet<>();
        for (FullObject<String, String> object : objects) {
            if (object.getIdentifier().equals(oldObject)) {
                newObjects.add(new FullObject<String, String>(newObject, getAttributesForObject(oldObject)));
            } else {
                newObjects.add(object);
            }
        }
        objects = newObjects;
        for (SortedSet<String> objects : objectsOfAttribute.values()) {
            if (objects.contains(oldObject)) {
                objects.remove(oldObject);
                objects.add(newObject);
            }
        }
    }
    
    public void renameObject(String oldName, String newName) {
        IndexedSet<FullObject<String, String>> newObjects = new ListSet<>();
        // IndexedSet<String> filteredAttributes = new ListSet<>();
        for (FullObject<String, String> object : objects) {
            if (object.getIdentifier().getName().equals(oldName)) {
            	String newObject = new String(newName,object.getIdentifier().getValue());
            	newObjects.add(new FullObject<String, String>(newObject, getAttributesForObject(object.getIdentifier())));
            } else {
                newObjects.add(object);
            }
        }
        objects = newObjects;
        for (SortedSet<String> objects : objectsOfAttribute.values()) {
        	for(String object : objects) {
        		if (object.contains(oldName)) {
            	String newObject = new String(newName,object.getValue());
            	objects.remove(object);
                objects.add(newObject);
            }
        }
        }
    }

    public boolean existsAttributeAlready(String name) {
        for (String attribute : getAttributes()) {
            if (attribute.equals(name))
                return true;
        }
        return false;
    }

    public boolean existsObjectAlready(String name) {
        for (FullObject<String, String> object : objects) {
            if (object.getIdentifier().getName().equals(name))
                return true;
        }
        return false;
    }

    public boolean existsObjectAlready(String name) {
        for (FullObject<String, String> object : objects) {
            if (object.getIdentifier().equals(name))
                return true;
        }
        return false;
    }

    public Set<String> getAttributesForObject(String objectID) {
        Set<String> attributes = new HashSet<>();
        FullObject<String, String> object = getObject(objectID);
        for (String attribute : getAttributes()) {
            if (objectHasAttribute(object, attribute)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public void removeAttribute(String attribute) {
        IndexedSet<String> newAttributes = new ListSet<>();
        for (FullObject<String, String> object : objects) {
            if (objectHasAttribute(object, attribute)) {
                try {
                    removeAttributeFromObject(attribute, object.getIdentifier());
                } catch (IllegalObjectException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String attr : getAttributes()) {
            if (attr.equals(attribute)) {
            } else {
                newAttributes.add(attr);
            }
        }
        getAttributes().clear();
        for (String attr : newAttributes) {
            getAttributes().add(attr);
        }
    }

    // Should not be used outside the context editor
    public void removeAttributeInternal(String attribute) {
        IndexedSet<String> newAttributes = new ListSet<>();
        for (String attr : getAttributes()) {
            if (attr.equals(attribute)) {
            } else {
                newAttributes.add(attr);
            }
        }
        getAttributes().clear();
        for (String attr : newAttributes) {
            getAttributes().add(attr);
        }
    }
    
    public void addObjectAt(FullObject<String, String> object, int i) {
        IndexedSet<FullObject<String, String>> newObjects = new ListSet<>();
        for (int j = 0; j < getObjectCount(); j++) {
            if (j == i)
                newObjects.add(object);
            newObjects.add(getObjectAtIndex(j));
        }
        if (i == getObjectCount())
            newObjects.add(object);
        objects = newObjects;
    }

    public void addAttributeAt(String attribute, int i) {
        IndexedSet<String> newAttributes = new ListSet<>();
        for (int j = 0; j < getAttributeCount(); j++) {
            if (j == i)
                newAttributes.add(attribute);
            newAttributes.add(getAttributeAtIndex(j));
        }
        if (i == getAttributeCount())
            newAttributes.add(attribute);
        getAttributes().clear();
        for (String attr : newAttributes) {
            getAttributes().add(attr);
        }
        objectsOfAttribute.put(attribute, new TreeSet<String>());
    }

    public TreeSet<String> intersection(Set<String> firstSet, Set<String> secondSet) {
        TreeSet<String> result = new TreeSet<String>();
        for (String s : firstSet) {
            for (String t : secondSet) {
                if (s.getName() == t.getName() && s.getValue() == t.getValue()) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    public TreeSet<String> intersectionAttr(Set<String> firstSet, Set<String> secondSet) {
        TreeSet<String> result = new TreeSet<String>();
        for (String s : firstSet) {
            for (String t : secondSet) {
                if (s == t) {
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
        this.dontConsideredAttr.add(attr);
    }

    /**
     * Set Attribute which has to be reconsider by lattice computation.
     * 
     * @param attr
     */
    public void considerAttribute(String attr) {
        this.dontConsideredAttr.remove(attr);
    }

    /**
     * Set Object which don't be consider by lattice computation.
     * 
     * @param obj
     */
    public void dontConsiderObject(FullObject<String, String> obj) {
        this.dontConsideredObj.add(obj);
    }

    /**
     * Set Object which has to be reconsider by lattice computation.
     * 
     * @param obj
     */
    public void considerObject(FullObject<String, String> obj) {
        this.dontConsideredObj.remove(obj);
    }

    public void clearConsidered() {
        dontConsideredAttr.clear();
        dontConsideredObj.clear();
    }

    public ArrayList<String> getDontConsideredAttr() {
        return dontConsideredAttr;
    }

    public ArrayList<FullObject<String, String>> getDontConsideredObj() {
        return dontConsideredObj;
    }

}
