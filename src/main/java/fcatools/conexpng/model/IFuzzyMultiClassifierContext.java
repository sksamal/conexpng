package fcatools.conexpng.model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;

/**
 * A specialization of FormalContext<String,String> that can be used for multi-classification
 * Set of classes S1 = { A1, B1, C1, D1 } and S2 = { A2, B2, C2, D2 } 
 * An object may be classified as A1 in set S1 and B2 in set S2 and so on.
 * 
 */
public class IFuzzyMultiClassifierContext extends FuzzyMultiClassifierContext {

	// Incremental Fuzzy Concept lattice
	private Set<Concept<String, FullObject<String, String>>> conceptLattice ;
	private Set<FullObject<String, String>> newObjects = new ListSet<FullObject<String, String>>();

	
	public boolean addObject(String object, String classifier, List<String> attributes, List<Double> values) throws IllegalObjectException {
		return this.addObject(object, classifier, attributes.toArray(new String[0]),values.toArray(new Double[0]));
	}

	public boolean addObject(String object, Set<String> classifiers, List<String> attributes, List<Double> values) throws IllegalObjectException {
		return this.addObject(object, classifiers, attributes.toArray(new String[0]),values.toArray(new Double[0]));
	}

	public boolean addObject(String object, String classifier, String attributes[], Double[] values) throws IllegalObjectException {
		newObjects.add(new FullObject<String,String>(object));
		return super.addObject(object,classifier,attributes,values);
	}
	
	public boolean addObject(String object, Set<String> classifiers, String attributes[], Double[] values) throws IllegalObjectException {
		newObjects.add(new FullObject<String,String>(object));
		return super.addObject(object,classifiers,attributes,values);
	}

	public boolean addObject(FullObject<String, String> o, String classifier, double value) throws IllegalObjectException {
		newObjects.add(o);
		return super.addObject(o, classifier, value);
	}

	public boolean addObject(FullObject<String, String> o, Set<String> classifiers, double value) throws IllegalObjectException {
		newObjects.add(o);
		return super.addObject(o, classifiers, value);
	}
	
	public boolean addObject(FullObject<String, String> o, String classifier) throws IllegalObjectException {
		newObjects.add(o);
		return super.addObject(o,classifier);
	}
	
	@Override
	public boolean removeObject(String id) throws IllegalObjectException {
		if(newObjects.contains(id))
			newObjects.remove(id);
		return super.removeObject(id);
	}

	@Override
	public boolean removeObject(FullObject<String, String> object) throws IllegalObjectException {
		if(newObjects.contains(object.getIdentifier()))
			newObjects.remove(object.getIdentifier());
		return removeObject(object.getIdentifier());
	}

	public IFuzzyMultiClassifierContext() {
		super();
	}

	public IFuzzyMultiClassifierContext(double threshold) {
		super(threshold);
	}

	public IFuzzyMultiClassifierContext(int objectsCount, int attributesCount) {
		super(objectsCount, attributesCount);
	}

	public IFuzzyMultiClassifierContext(int objectsCount, int attributesCount, double threshold) {
		super(objectsCount, attributesCount, threshold);
	}

	public IFuzzyMultiClassifierContext(FuzzyMultiClassifierContext fc) {

		// copy new structures directly
		this.classesSet = fc.classesSet;
	  	this.trainingSetMap = fc.trainingSetMap;
	  	this.composition = fc.composition;
	  	this.allObjectsOfAttribute = fc.allObjectsOfAttribute;
	  	this.objectsOfAttribute = fc.objectsOfAttribute;
	  	
	  	this.setThreshold(fc.getThreshold());

		// raw copy attributes
		for(String attr : fc.getAttributes())
	   		this.getAttributes().add(attr);
	    	
	   	// raw copy objects
	  	for(FullObject<String,String> o : fc.getObjects())
	  		this.getObjects().add(o);
	  	
 }
	 

   @Override
    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
	   
		// compute all if existing lattice is null
	   if(conceptLattice ==null) {
		   newObjects.clear();
		   return super.getConcepts();
	   }
   	
   		// nothing changed, just send whats existing, no new objects added
   		if(conceptLattice !=null && newObjects.size()==0)
   			return conceptLattice;
   		
        ListSet<Concept<String, FullObject<String, String>>> newConceptLattice = new ListSet<Concept<String, FullObject<String, String>>>();

		// Add Intent algorithm
        for(FullObject<String, String> newObj : newObjects) {
            for(Concept<String, FullObject<String,String>> c : this.conceptLattice) {
            	
            	// Get the extent of concept
            	Set<FullObject<String,String>> extent = c.getExtent();
            	Set<String> extentAttributes = new ListSet<String>();
            	for(FullObject<String,String> obj : extent)
            		extentAttributes.addAll(obj.getDescription().getAttributes());
            	
            	// Case I - Add a new concept (A,B) U g 
            	if(newObj.getDescription().getAttributes().containsAll(extentAttributes)) {
            	      c.getIntent().add(newObj.getIdentifier());
            	      addConceptToLattice(newConceptLattice,c);
            	}
            	else {
            		
            		//Case III - Add existing concept and another one if its the generator concept (not duplicate)
            		addConceptToLattice(newConceptLattice,c);    
            		Set<String> newExtentAttributes = this.intersection(newObj.getDescription().getAttributes(),extentAttributes);
            		
            		Concept<String, FullObject<String, String>> newC = new LatticeConcept();
            		newC.getIntent().addAll(c.getIntent());
            		newC.getIntent().add(newObj.getIdentifier());
            		
            		boolean donotAdd = false;
          	        for (FullObject<String, String> f : this.getObjects()) {
            	        if (f.getDescription().getAttributes().contains(newExtentAttributes)) {
            	               newC.getExtent().add(f);
            	             }
            	        if(!newC.getIntent().contains(f.getIdentifier()))  
            	        	donotAdd = true; // Not an generator, no need to add, it will be a duplicate
            	    }
          	        
          	        if(!donotAdd)
              	      addConceptToLattice(newConceptLattice,newC);
            				
            }
            }
            	this.conceptLattice = newConceptLattice;
            }
            	newObjects.clear();
                return conceptLattice;
	   
   }

     // sub-method to add concept and calculate probabilities
     public void addConceptToLattice(ListSet<Concept<String, FullObject<String, String>>> newConceptLattice ,Concept<String, FullObject<String, String>> c ) {
	   
	    		// A map that stores count of objects for various classes
	    		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
	        	FuzzyMultiClassedConcept fcc = new FuzzyMultiClassedConcept(c);
	        	for(FullObject<String, String> obj : fcc.getExtent()) {
	        		Set<String> clazzSet = trainingSetMap.get(obj.getIdentifier());
	        		System.out.println(clazzSet);
	        		for(String clazz: clazzSet) {
	        			if(countMap.containsKey(clazz))
	        				countMap.put(clazz, countMap.get(clazz)+1);
	        			else
	        				countMap.put(clazz,1);
	        		}
	        	}
	   
	        	// Calculate probabilities
	        	int i=0;
	        	for(Set<String> clazzSet : classesSet) {
	        		for(String clazz: clazzSet)
	        			if(countMap.containsKey(clazz))
	        				fcc.addProb(i,countMap.get(clazz)*1.0/fcc.getExtent().size());
	        			else
	        				fcc.addProb(i,0.0);
	        		i++;
	        	}
	        	newConceptLattice.add(fcc);	
	        }
     
       
     
}
