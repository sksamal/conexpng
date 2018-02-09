package fcatools.conexpng.model;

import java.util.Set;
import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcaapi.utils.IndexedSet;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;

/**
 * A specialization of FormalContext<String,String> with the aim to remove the
 * verbose repetition of <String,String>. Plus, adds a couple of useful methods.
 * Due to the API of FormalContext<String,String> the here implemented methods
 * are extremely inefficient.
 */
public class IncrementalFormalContext extends FormalContext {
 
    
	// Incremental Concept lattice
	private Set<Concept<String, FullObject<String, String>>> conceptLattice ;
	private Set<FullObject<String, String>> newObjects = new ListSet<FullObject<String, String>>();
    
	public IncrementalFormalContext() {
        super();
    }

    public IncrementalFormalContext(int objectsCount, int attributesCount) {
        super(objectsCount,attributesCount);
    }
    
    public IncrementalFormalContext(FormalContext fc) {
 
    	for(String attr : fc.getAttributes())
    		this.addAttribute(attr);
    	
    	try {
    	for(FullObject<String,String> o : fc.getObjects())
    		this.addObject(o);
    	}
    	catch (IllegalObjectException e) {
    		e.printStackTrace();
    	}
    }
    
    @Override
    public boolean addObject(FullObject<String, String> arg0) throws IllegalObjectException {
        if (super.addObject(arg0)) {
            for (String attribute : arg0.getDescription().getAttributes()) {
                objectsOfAttribute.get(attribute).add(arg0.getIdentifier());
            }
            newObjects.add(arg0);
            return true;
        }
        return false;
    }

    @Override
    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
    	
    	// compute all
    	if(conceptLattice ==null) {
    		newObjects.clear();
    		return super.getConcepts();
    	}
    	
    	// nothing changed, just send whts existing
    	if(conceptLattice !=null && newObjects.size()==0)
    		return conceptLattice;
    	
    	
        ListSet<Concept<String, FullObject<String, String>>> newConceptLattice = new ListSet<Concept<String, FullObject<String, String>>>();
 
        for(FullObject<String, String> newObj : newObjects) {
        for(Concept<String, FullObject<String,String>> c : this.conceptLattice) {
        	
        	// Get the extent of concept
        	Set<FullObject<String,String>> extent = c.getExtent();
        	Set<String> extentAttributes = new ListSet<String>();
        	for(FullObject<String,String> obj : extent)
        		extentAttributes.addAll(obj.getDescription().getAttributes());
        	
        	// Case I
        	if(newObj.getDescription().getAttributes().containsAll(extentAttributes)) {
        	      c.getIntent().add(newObj.getIdentifier());
        	      newConceptLattice.add(c);
        	}
        	else {
        		newConceptLattice.add(c);    //Case 3
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
      	        	newConceptLattice.add(newC);
        				
        }
        }
        	this.conceptLattice = newConceptLattice;
        }
        	newObjects.clear();
            return conceptLattice;
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
        newObjects.add(object);
        objects = newObjects;
    }

}
