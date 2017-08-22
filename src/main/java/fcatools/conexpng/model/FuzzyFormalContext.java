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
public class FuzzyFormalContext extends de.tudresden.inf.tcs.fcalib.FormalContext<String, FuzzyObject<String,Double>> {

    protected HashMap<String, SortedSet<FuzzyObject<String,Double>>> objectsOfAttribute = new HashMap<>();
    private ArrayList<String> dontConsideredAttr = new ArrayList<>();
    private ArrayList<FullObject<String, FuzzyObject<String,Double>>> dontConsideredObj = new ArrayList<>();

    @Override
    public boolean addAttribute(String attribute) throws IllegalAttributeException {
        if (super.addAttribute(attribute)) {
            objectsOfAttribute.put(attribute, new TreeSet<FuzzyObject<String,Double>>());
            return true;
        } else
            return false;
    }

    @Override
    public boolean addAttributeToObject(String attribute, FuzzyObject<String,Double> id) throws IllegalAttributeException,
            IllegalObjectException {
        if (super.addAttributeToObject(attribute,id)) {
            SortedSet<FuzzyObject<String,Double>> objects = objectsOfAttribute.get(attribute);
            if (objects != null)
                objects.add(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean addObject(FullObject<String, FuzzyObject<String,Double>> arg0) throws IllegalObjectException {
        if (super.addObject(arg0)) {
            for (String attribute : arg0.getDescription().getAttributes()) {
                objectsOfAttribute.get(attribute).add(arg0.getIdentifier());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAttributeFromObject(String attribute, FuzzyObject<String,Double> id) throws IllegalAttributeException,
            IllegalObjectException {
        if (super.removeAttributeFromObject(attribute, id)) {
            SortedSet<FuzzyObject<String,Double>> objects = objectsOfAttribute.get(attribute);
            if (objects != null)
                objects.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeObject(FuzzyObject<String,Double> id) throws IllegalObjectException {
        return removeObject(getObject(id));
    }

    @Override
    public boolean removeObject(FullObject<String, FuzzyObject<String,Double>> object) throws IllegalObjectException {
        if (super.removeObject(object)) {
            for (String attribute : object.getDescription().getAttributes()) {
                objectsOfAttribute.get(attribute).remove(object.getIdentifier());
            }
        }
        return false;
    }

    public FuzzyFormalContext() {
        super();
        objectsOfAttribute = new HashMap<>();
    }

    public FuzzyFormalContext(int objectsCount, int attributesCount) {
        super();
        objectsOfAttribute = new HashMap<>();
        for (int i = 0; i < attributesCount; i++) {
            addAttribute("attr" + i);
        }
        for (int i = 0; i < objectsCount; i++) {
            try {
            	FuzzyObject<String,Double> fattr = new FuzzyObject<String,Double>("obj" + i,0.0);
                addObject(new FullObject<String, FuzzyObject<String,Double>>(fattr));
            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Set<Concept<String, FullObject<String, FuzzyObject<String,Double>>>> getConcepts() {
        ListSet<Concept<String, FullObject<String, FuzzyObject<String,Double>>>> conceptLattice = new ListSet<Concept<String, FullObject<String, FuzzyObject<String,Double>>>>();

        HashMap<String, Set<FuzzyObject<String,Double>>> extentPerAttr = new HashMap<String, Set<FuzzyObject<String,Double>>>();
        /*
         * Step 1: Initialize a list of concept extents. To begin with, write
         * for each attribute m # M the attribute extent {m}$ to this list (if
         * not already present).
         */
        for (String s : this.getAttributes()) {
            TreeSet<FuzzyObject<String,Double>> set = new TreeSet<FuzzyObject<String,Double>>();
            for (FullObject<String, FuzzyObject<String,Double>> f : this.getObjects()) {
                if (f.getDescription().getAttributes().contains(s)) {
                    set.add(f.getIdentifier());
                }
            }
            extentPerAttr.put(s, set);
        }

        /*
         * Step 2: For any two sets in this list, compute their intersection. If
         * the result is a set that is not yet in the list, then extend the list
         * by this set. With the extended list, continue to build all pairwise
         * intersections.
         */
        HashMap<String, Set<FuzzyObject<String,Double>>> temp = new HashMap<String, Set<FuzzyObject<String,Double>>>();
        for (String s : extentPerAttr.keySet()) {
            for (String t : extentPerAttr.keySet()) {
                if (!s.equals(t)) {
                    Set<FuzzyObject<String,Double>> result = this.intersection(extentPerAttr.get(s), extentPerAttr.get(t));
                    if (!extentPerAttr.values().contains(result)) {
                        if (!temp.containsValue(result)) {
                            temp.put(s + ", " + t, result);
                        }
                    }
                }
            }
        }
        extentPerAttr.putAll(temp);

        /*
         * Step 3: If for any two sets of the list their intersection is also in
         * the list, then extend the list by the set G (provided it is not yet
         * contained in the list). The list then contains all concept extents
         * (and nothing else).
         */
        boolean notcontained = false;
        for (String s : extentPerAttr.keySet()) {
            if (notcontained)
                break;
            for (String t : extentPerAttr.keySet()) {
                if (!s.equals(t)) {
                    Set<FuzzyObject<String,Double>> result = this.intersection(extentPerAttr.get(s), extentPerAttr.get(t));
                    if (!extentPerAttr.values().contains(result)) {
                        notcontained = true;
                        break;
                    }
                }
            }
        }
        if (!notcontained) {
            TreeSet<FuzzyObject<String,Double>> set = new TreeSet<FuzzyObject<String,Double>>();
            for (FullObject<String, FuzzyObject<String,Double>> f : this.getObjects()) {
                set.add(f.getIdentifier());
            }
            if (!extentPerAttr.values().contains(set))
                extentPerAttr.put("", set);
        }

        /*
         * Step 4: For every concept extent A in the list compute the
         * corresponding intent A' to obtain a list of all formal concepts
         * (A,A') of (G,M, I).
         */
        HashSet<Set<FuzzyObject<String,Double>>> extents = new HashSet<Set<FuzzyObject<String,Double>>>();
        for (Set<FuzzyObject<String,Double>> e : extentPerAttr.values()) {
            if (!extents.contains(e))
                extents.add(e);
        }
        for (Set<FuzzyObject<String,Double>> e : extents) {
            TreeSet<String> intents = new TreeSet<String>();
            int count = 0;
            Concept<String, FullObject<String, FuzzyObject<String,Double>>> c = new FuzzyLatticeConcept();
            if (e.isEmpty()) {
                intents.addAll(getAttributes());
            } else
                for (FullObject<String, FuzzyObject<String,Double>> i : this.getObjects()) {
                    if (e.contains(i.getIdentifier().toString())) {
                        TreeSet<String> prev = sort(i.getDescription().getAttributes());
                        if (count > 0) {
                            intents = intersectionAttr(prev, intents);
                        } else {
                            intents = prev;
                        }
                        count++;
                        c.getExtent().add(i);
                    }
                }
            // concepts.put(e, intents);
            for (String s : intents) {
                c.getIntent().add(s);
            }
            conceptLattice.add(c);
        }
        return conceptLattice;
    }

    public Set<Concept<String, FullObject<String, FuzzyObject<String,Double>>>> getConceptsWithoutConsideredElements() {
        ListSet<Concept<String, FullObject<String, FuzzyObject<String,Double>>>> conceptLattice = new ListSet<Concept<String, FullObject<String, FuzzyObject<String,Double>>>>();

        HashMap<String, Set<FuzzyObject<String,Double>>> extentPerAttr = new HashMap<String, Set<FuzzyObject<String,Double>>>();
        /*
         * Step 1: Initialize a list of concept extents. To begin with, write
         * for each attribute m # M the attribute extent {m}$ to this list (if
         * not already present).
         */
        for (String s : this.getAttributes()) {
            if (!dontConsideredAttr.contains(s)) {
                TreeSet<FuzzyObject<String,Double>> set = new TreeSet<FuzzyObject<String,Double>>();
                for (FullObject<String, FuzzyObject<String,Double>> f : this.getObjects()) {
                    if (f.getDescription().getAttributes().contains(s) && (!dontConsideredObj.contains(f))) {
                        set.add(f.getIdentifier());
                    }
                }
                extentPerAttr.put(s, set);
            }
        }

        /*
         * Step 2: For any two sets in this list, compute their intersection. If
         * the result is a set that is not yet in the list, then extend the list
         * by this set. With the extended list, continue to build all pairwise
         * intersections.
         */
        HashMap<String, Set<FuzzyObject<String,Double>>> temp = new HashMap<String, Set<FuzzyObject<String,Double>>>();
        for (String s : extentPerAttr.keySet()) {
            for (String t : extentPerAttr.keySet()) {
                if (!s.equals(t)) {
                    Set<FuzzyObject<String,Double>> result = this.intersection(extentPerAttr.get(s), extentPerAttr.get(t));
                    if (!extentPerAttr.values().contains(result)) {
                        if (!temp.containsValue(result)) {
                            temp.put(s + ", " + t, result);
                        }
                    }
                }
            }
        }
        extentPerAttr.putAll(temp);

        /*
         * Step 3: If for any two sets of the list their intersection is also in
         * the list, then extend the list by the set G (provided it is not yet
         * contained in the list). The list then contains all concept extents
         * (and nothing else).
         */
        boolean notcontained = false;
        for (String s : extentPerAttr.keySet()) {
            if (notcontained)
                break;
            for (String t : extentPerAttr.keySet()) {
                if (!s.equals(t)) {
                    Set<FuzzyObject<String,Double>> result = this.intersection(extentPerAttr.get(s), extentPerAttr.get(t));
                    if (!extentPerAttr.values().contains(result)) {
                        notcontained = true;
                        break;
                    }
                }
            }
        }
        if (!notcontained) {
            TreeSet<FuzzyObject<String,Double>> set = new TreeSet<FuzzyObject<String,Double>>();
            for (FullObject<String, FuzzyObject<String,Double>> f : this.getObjects()) {
                set.add(f.getIdentifier());
            }
            if (!extentPerAttr.values().contains(set))
                extentPerAttr.put("", set);
        }

        /*
         * Step 4: For every concept extent A in the list compute the
         * corresponding intent A' to obtain a list of all formal concepts
         * (A,A') of (G,M, I).
         */

        HashSet<Set<FuzzyObject<String,Double>>> extents = new HashSet<Set<FuzzyObject<String,Double>>>();
        for (Set<FuzzyObject<String,Double>> e : extentPerAttr.values()) {
            if (!extents.contains(e))
                extents.add(e);
        }
        for (Set<FuzzyObject<String,Double>> e : extents) {
            TreeSet<String> intents = new TreeSet<String>();
            int count = 0;
            Concept<String, FullObject<String, FuzzyObject<String,Double>>> c = new FuzzyLatticeConcept();
            if (e.isEmpty()) {
                intents.addAll(getAttributes());
            } else
                for (FullObject<String, FuzzyObject<String,Double>> i : this.getObjects()) {
                    if (!dontConsideredObj.contains(i)) {
                        if (e.contains(i.getIdentifier().toString())) {
                            TreeSet<String> prev = sort(i.getDescription().getAttributes());
                            if (count > 0) {
                                intents = intersectionAttr(prev, intents);
                            } else {
                                intents = prev;
                            }
                            count++;
                            c.getExtent().add(i);
                        }
                    }
                }
            // concepts.put(e, intents);
            for (String s : intents) {
                if (!dontConsideredAttr.contains(s))
                    c.getIntent().add(s);
            }
            conceptLattice.add(c);
        }
        return conceptLattice;
    }

    public int supportCount(Set<String> attributes) {
        if (attributes.isEmpty())
            return objects.size();
        int mincount = Integer.MAX_VALUE;
        String attributeWithMincount = "";
        // search for the attribute with the fewest objects
        for (String string : attributes) {
            if (objectsOfAttribute.get(string).size() < mincount) {
                mincount = objectsOfAttribute.get(string).size();
                attributeWithMincount = string;
            }
        }
        int count = 0;
        boolean notfound;
        // search the other attributes only in these objects
        for (FuzzyObject<String,Double> obj : objectsOfAttribute.get(attributeWithMincount)) {
            notfound = false;
            for (String att : attributes) {
                if (!objectHasAttribute(getObject(obj), att)) {
                    notfound = true;
                    break;
                }
            }
            if (!notfound)
                count++;
        }
        return count;

    }

    @Override
    public Set<FCAImplication<String>> getStemBase() {
        // de.tudresden.inf.tcs.fcalib.ImplicationSet<String> doesn't return the
        // implications, so we need this result-variable, maybe we should modify
        // ImplicationSet
        IndexedSet<FCAImplication<String>> result = new ListSet<>();

        ImplicationSet<String> impl = new ImplicationSet<>(this);

        // Next-Closure

        Set<String> A = new ListSet<>();

        while (!A.equals(getAttributes())) {
            A = impl.nextClosure(A);
            if (A == null)
                return Collections.emptySet();
            if (!A.equals(doublePrime(A))) {
                Implication<String> im = new Implication<>(A, doublePrime(A));
                impl.add(im);
                result.add(im);
            }
        }
        // remove redundant items in the conclusion
        for (FCAImplication<String> fcaImplication : result) {
            fcaImplication.getConclusion().removeAll(fcaImplication.getPremise());
        }

        return result;
    }

    @Override
    public Set<FCAImplication<String>> getDuquenneGuiguesBase() {
        return getStemBase();
    }

    /**
     * Returns objects of this context.
     * 
     * @return objects of this context
     */
    public IndexedSet<FullObject<String, FuzzyObject<String,Double>>> getObjects() {
        return objects;
    }

    /**
     * Removes given object only without removing attributes.
     * 
     * @param o
     *            object to remove
     */
    public void removeObjectOnly(FullObject<String, String> o) {
        objects.remove(o);
    }

    public void transpose() {
        IndexedSet<FullObject<String, FuzzyObject<String,Double>>> newObjects = new ListSet<>();
        IndexedSet<String> newAttributes = new ListSet<>();
        for (String attribute : getAttributes()) {
            IndexedSet<FuzzyObject<String,Double>> allObjectsForAttribute = new ListSet<>();
            for (FullObject<String, FuzzyObject<String,Double>> object : objects) {
                if (objectHasAttribute(object, attribute)) {
                	FuzzyObject<String,Double> fuzzyObj = new FuzzyObject<String,Double>(attribute,object.getIdentifier().getValue());
                    allObjectsForAttribute.add(fuzzyObj);
                }
            }
        	FullObject<String, FuzzyObject<String,Double>> newObject = new FullObject<>(attribute,allObjectsForAttribute);
            newObjects.add(newObject);
        }
        for (FullObject<String, FuzzyObject<String,Double>> object : objects) {
            newAttributes.add(object.getIdentifier().getName());
        }

        objects = newObjects;
        // Why can I access objects directly but not attributes? (I'm
        // questioning the API-decision)
        getAttributes().clear();
        objectsOfAttribute.clear();
        for (String attribute : newAttributes) {
            getAttributes().add(attribute);
            objectsOfAttribute.put(attribute, new TreeSet<FuzzyObject<String,Double>>());
        }
        for (FullObject<String, FuzzyObject<String,Double>> object : objects) {
            for (String attribute : object.getDescription().getAttributes()) {
                objectsOfAttribute.get(attribute).add(object.getIdentifier());
            }
        }
    }

    public void toggleAttributeForObject(String attribute, FuzzyObject<String,Double> objectID) {
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
            	FuzzyObject<String,Double> objectID = getObjectAtIndex(i).getIdentifier();
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
                FullObject<String, FuzzyObject<String,Double>> object = getObjectAtIndex(i);
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
                FullObject<String, FuzzyObject<String,Double>> object = getObjectAtIndex(i);
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
        IndexedSet<FullObject<String, FuzzyObject<String,Double>>> filteredObjects = new ListSet<>();
        for (FullObject<String, FuzzyObject<String,Double>> object : objects) {
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
        for (FullObject<String, FuzzyObject<String,Double>> object : filteredObjects) {
            try {
                addAttributeToObject(newName, object.getIdentifier());
            } catch (IllegalObjectException e) {
                e.printStackTrace();
            }
        }
    }

    public void renameObject(String oldName, String newName) {
        IndexedSet<FullObject<String, FuzzyObject<String,Double>>> newObjects = new ListSet<>();
        // IndexedSet<String> filteredAttributes = new ListSet<>();
        for (FullObject<String, FuzzyObject<String,Double>> object : objects) {
            if (object.getIdentifier().equals(oldName)) {
            	
                newObjects.add(new FullObject<String, FuzzyObject<String,Double>>(newName, getAttributesForObject(oldName)));
            } else {
                newObjects.add(object);
            }
        }
        objects = newObjects;
        for (SortedSet<String> objects : objectsOfAttribute.values()) {
            if (objects.contains(oldName)) {
                objects.remove(oldName);
                objects.add(newName);
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
            if (object.getIdentifier().equals(name))
                return true;
        }
        return false;
    }

    public Set<String> getAttributesForObject(FuzzyObject<String,Double> objectID) {
        Set<String> attributes = new HashSet<>();
        FullObject<String, FuzzyObject<String,Double>> object = getObject(objectID);
        for (String attribute : getAttributes()) {
            if (objectHasAttribute(object, attribute)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public void removeAttribute(String attribute) {
        IndexedSet<String> newAttributes = new ListSet<>();
        for (FullObject<String, FuzzyObject<String,Double>> object : objects) {
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

    public TreeSet<FuzzyObject<String,Double>> intersection(Set<FuzzyObject<String,Double>> firstSet, Set<FuzzyObject<String,Double>> secondSet) {
        TreeSet<FuzzyObject<String,Double>> result = new TreeSet<FuzzyObject<String,Double>>();
        for (FuzzyObject<String,Double> s : firstSet) {
            for (FuzzyObject<String,Double> t : secondSet) {
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
