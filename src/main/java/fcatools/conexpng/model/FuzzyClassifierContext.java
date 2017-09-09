package fcatools.conexpng.model;

import java.util.HashMap;
import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;

/**
 * A specialization of FormalContext<String,String> with the aim to remove the
 * verbose repetition of <String,String>. Plus, adds a couple of useful methods.
 * Due to the API of FormalContext<String,String> the here implemented methods
 * are extremely inefficient.
 */
public class FuzzyClassifierContext extends FuzzyFormalContext {

	// Extra wrapper sets and maps to hold objects and its classes
	protected HashMap<String, String> classMap;
	Set<String> classes;

	public boolean addObject(String object, String classifier, String attributes[], double[] values) throws IllegalObjectException {
		if(super.addObject(object, attributes,values)) {
			classMap.put(object, classifier);
			classes.add(classifier);
		}
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, String classifier, double value) throws IllegalObjectException {
		if(super.addObject(o,value)) {
			classMap.put(o.getIdentifier(), classifier);
			classes.add(classifier);
			}
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, String classifier) throws IllegalObjectException {
		if(super.addObject(o)) {
			classMap.put(o.getIdentifier(), classifier);
			classes.add(classifier);
			}
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(String id) throws IllegalObjectException {
		if(removeObject(getObject(id))) {
			String clazz = classMap.get(id);
			classMap.remove(id);
			if(!classMap.values().contains(clazz)) {
				classes.remove(clazz);
			}
		}
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(FullObject<String, String> object) throws IllegalObjectException {
		return removeObject(object.getIdentifier());
	}

	public FuzzyClassifierContext() {
		super();
		this.classMap = new HashMap<String,String>();
		this.classes = new ListSet<String>();
	}

	public FuzzyClassifierContext(double threshold) {
		super(threshold);
		this.classMap = new HashMap<String,String>();
		this.classes = new ListSet<String>();
	}

	public FuzzyClassifierContext(int objectsCount, int attributesCount) {
		super(objectsCount, attributesCount);
		this.classMap = new HashMap<String,String>();
		this.classes = new ListSet<String>();
	}

	public FuzzyClassifierContext(int objectsCount, int attributesCount, double threshold) {
		super(objectsCount, attributesCount, threshold);
		this.classMap = new HashMap<String,String>();
		this.classes = new ListSet<String>();
	}

	@Override
	public String toString() {
		// For now
		return super.toString();
	}

	public void removeObjectOnly(FullObject<String, String> o) {

		super.removeObjectOnly(o);
		if(classMap.containsKey(o.getIdentifier()))
			classMap.remove(o.getIdentifier());
	
		String clazz = classMap.get(o.getIdentifier());
		if(!classMap.values().contains(clazz)) {
			classes.remove(clazz);
		}
	}

	public void transpose() {
		// needs to be thought of 
		super.transpose();
	}

	public void renameObject(String oldObject, String newObject) {
		super.renameObject(oldObject, newObject);
		classMap.put(newObject, classMap.get(oldObject));
		classMap.remove(oldObject);
	}

   @Override
    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
	   		ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
	    	for(Concept<String, FullObject<String, String>> c : super.getConcepts()) {
	    		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
	        	FuzzyClassedConcept fcc = new FuzzyClassedConcept(c);
	        	for(FullObject<String, String> obj : fcc.getExtent()) {
	        		String clazz = classMap.get(obj.getIdentifier());
	        		if(countMap.containsKey(clazz))
	        			countMap.put(clazz, countMap.get(clazz)+1);
	        		else
	        			countMap.put(clazz,1);
	        	}
	   
	        	for(String clazz : classes) {
	        		if(countMap.containsKey(clazz))
	        			fcc.addProb(countMap.get(clazz)*1.0/fcc.getExtent().size());
	        		else
	        			fcc.addProb(0.0);

	        	}
	        	fuzzyLattice.add(fcc);	
	        }
	    	return fuzzyLattice;
	    }
   
   @Override
   public Set<Concept<String, FullObject<String, String>>> getConceptsWithoutConsideredElements() {
	ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
   	for(Concept<String, FullObject<String, String>> c : super.getConcepts()) {
		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
    	FuzzyClassedConcept fcc = new FuzzyClassedConcept(c);
    	for(FullObject<String, String> obj : fcc.getExtent()) {
    		String clazz = classMap.get(obj.getIdentifier());
    		if(countMap.containsKey(clazz))
    			countMap.put(clazz, countMap.get(clazz)+1);
    		else
    			countMap.put(clazz,1);
    	}

    	for(String clazz : classes) 
    		fcc.addProb(countMap.get(clazz)*1.0/fcc.getExtent().size());
    	fuzzyLattice.add(fcc);	
    }
	return fuzzyLattice;
   }   
}
