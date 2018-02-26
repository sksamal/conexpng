package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	// A list of hashMaps for each set of classes
	protected HashMap<String, String> classMap;
	protected ArrayList<String> classes;
	
	public boolean addObject(String object, String classifier, String attributes[], double[] values) throws IllegalObjectException {
		if(super.addObject(object, attributes,values)) {
			HashMap<String, String> classMap = new HashMap<String, String>();
			classMap.put(object, classifier);
			if(classifier!=null && !classes.contains(classifier)) 
				classes.add(classifier);
		}
		else return false;
		return true;
	}
	
	public boolean addObject(String object, ArrayList<String> classifiers, String attributes[], double[] values) throws IllegalObjectException {
		if(super.addObject(object, attributes,values)) {
			for(String classifier: classifiers) {
				classMap.put(object, classifier);
				if(classifier!=null && !classes.contains(classifier))  
					classes.add(classifier);
		  }
		}
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, String classifier, double value) throws IllegalObjectException {
		if(super.addObject(o,value)) {
			classMap.put(o.getIdentifier(), classifier);
			
			if(classifier!=null && !classes.contains(classifier)){
				classes.add(classifier);
				}
			}
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, ArrayList<String> classifiers, double value) throws IllegalObjectException {
		if(super.addObject(o,value)) {
			for(String classifier: classifiers) {
				classMap.put(o.getIdentifier(), classifier);
			
			if(classifier!=null && !classes.contains(classifier)){
				classes.add(classifier);
			}
			}
		}
		else return false;
		return true;
	}

	
	public boolean addObject(FullObject<String, String> o, String classifier) throws IllegalObjectException {
		if(super.addObject(o)) {
			HashMap<String, String> classMap = new HashMap<String, String>();
			classMap.put(o.getIdentifier(), classifier);
			if(classifier!=null && !classes.contains(classifier)) {
				classes.add(classifier);
			}
			}
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(String id) throws IllegalObjectException {
		
		List<String> removeList = new ArrayList<String>();
		if(removeObject(getObject(id))) {
			String clazz = classMap.remove(id);
			if(!classMap.values().contains(clazz)) {
					removeList.add(clazz);
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
		this.classMap = new HashMap<>();
		this.classes = new ArrayList<>();
	}

	public FuzzyClassifierContext(double threshold) {
		super(threshold);
		this.classMap = new HashMap<>();
		this.classes = new ArrayList<>();
	}

	public FuzzyClassifierContext(int objectsCount, int attributesCount) {
		super(objectsCount, attributesCount);
		this.classMap = new HashMap<>();
		this.classes = new ArrayList<>();
	}

	public FuzzyClassifierContext(int objectsCount, int attributesCount, double threshold) {
		super(objectsCount, attributesCount, threshold);
		this.classMap = new HashMap<>();
		this.classes = new ArrayList<>();
	}

	@Override
	public String toString() {
		// For now
		return "FuzzyClassifierContext " + super.toString() + classes.toString();
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
   
   public HashMap<String, String> getClassMap() {
	return classMap;
}

public void setClassMap(HashMap<String, String> classMap) {
	this.classMap = classMap;
}

public List<String> getClasses() {
	return classes;
}

public void setClasses(ArrayList<String> classes) {
	this.classes = classes;
}

public String getClassAsString(int index) {
	return (String) this.classes.toArray()[index];
}

public String getClass(String oid) {
	return this.classMap.get(oid);
}
public String getClassAsString(List<Integer> indices) {
	String clazz = "";
	for(int index: indices)
		clazz= clazz+ this.classes.toArray()[index] + ",";
	return clazz.substring(0,clazz.length()-1);
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
