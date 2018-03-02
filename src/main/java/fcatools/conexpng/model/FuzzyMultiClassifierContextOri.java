package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
public class FuzzyMultiClassifierContextOri extends FuzzyFormalContext {

	// A Set of hashMaps holding object and set of classes
	protected HashMap<String, Set<String>> classSetMap;
	// A List of Set of classes 
	ArrayList<Set<String>> classesSet;
	
	public boolean addObject(String object, String classifier, List<String> attributes, List<Double> values) throws IllegalObjectException {
		return this.addObject(object, classifier, attributes.toArray(new String[0]),values.toArray(new Double[0]));
	}

	public boolean addObject(String object, Set<String> classifiers, List<String> attributes, List<Double> values) throws IllegalObjectException {
		return this.addObject(object, classifiers, attributes.toArray(new String[0]),values.toArray(new Double[0]));
	}

	public boolean addObject(String object, String classifier, String attributes[], Double[] values) throws IllegalObjectException {
		if(super.addObject(object, attributes,values)) {
			Set<String> classSet = new TreeSet<String>();
			classSet.add(classifier);
			classSetMap.put(object,classSet);
			if(classifier!=null)  {
				for(Set<String> classes : classesSet) {
					if(classes.contains(classifier))
						return true;
				}
				
				if(classesSet.isEmpty()|| classesSet.get(0) == null) {
					classesSet.add(new TreeSet<String>());
				}
				Set<String>	classes = classesSet.get(0);
				classes.add(classifier);
			}
		}
		else return false;
		return true;
	}
	
	public boolean addObject(String object, Set<String> classifiers, String attributes[], Double[] values) throws IllegalObjectException {
		if(super.addObject(object, attributes,values)) {
			classSetMap.put(object, classifiers);
			int i = 0;
			for(String classifier: classifiers) {
				if(classesSet.size()<=i)
					classesSet.add(new TreeSet<String>());
				
				if(classifier!=null)  {
					for(Set<String> classes : classesSet) {
						if(classes.contains(classifier))
							return true;
					}
					classesSet.get(i).add(classifier);
				}
			i++;
		  }
		}
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, String classifier, double value) throws IllegalObjectException {
		if(super.addObject(o,value)) {
			Set<String> classSet = new TreeSet<String>();
			classSet.add(classifier);
			classSetMap.put(o.getIdentifier(),classSet);
			if(classifier!=null)  {
				for(Set<String> classes : classesSet) {
					if(classes.contains(classifier))
						return true;
				}
				
				if(classesSet.isEmpty()|| classesSet.get(0) == null) 
					classesSet.add(new TreeSet<String>());
				classesSet.get(0).add(classifier);
			}
		}
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, Set<String> classifiers, double value) throws IllegalObjectException {
		if(super.addObject(o,value)) {
			classSetMap.put(o.getIdentifier(), classifiers);
			int i = 0;
			for(String classifier: classifiers) {
				if(classesSet.size()<=i)
					classesSet.add(new TreeSet<String>());
				
				if(classifier!=null)  {
					for(Set<String> classes : classesSet) {
						if(classes.contains(classifier))
							return true;
					}
					classesSet.get(i).add(classifier);
				}
			i++;
		  }
		}
		else return false;
		return true;
	}

	
	public boolean addObject(FullObject<String, String> o, String classifier) throws IllegalObjectException {
		if(super.addObject(o)) {
			Set<String> classSet = new HashSet<String>();
			classSet.add(classifier);
			classSetMap.put(o.getIdentifier(),classSet);
			if(classifier!=null)  {
				for(Set<String> classes : classesSet) {
					if(classes.contains(classifier))
						return true;
				}
				if(classesSet.isEmpty()|| classesSet.get(0) == null) 
					classesSet.add(new TreeSet<String>());
				classesSet.get(0).add(classifier);
			}
		}
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(String id) throws IllegalObjectException {
		if(removeObject(getObject(id))) {
			Set<String> clazzSet = classSetMap.get(id);
			classSetMap.remove(id);
			
			Set<String> removeList = new TreeSet<String>();
			for(String clazz : clazzSet)
				removeList.add(clazz);
			
			for(Set<String> classSet : classSetMap.values()) {
				for(String clazz: clazzSet) {
					if(classSet.contains(clazz))
						removeList.remove(clazz);
				}
				
				List<Set<String>> removeSet = new ArrayList<Set<String>>();
				for(String clazz: removeList) {
					for(Set<String> classes: classesSet) {
						if(classes.contains(clazz))
							classes.remove(clazz);
						if(classes.isEmpty())
							removeSet.add(classes);
				}
			}
			classSetMap.remove(removeSet);
			}
		}
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(FullObject<String, String> object) throws IllegalObjectException {
		return removeObject(object.getIdentifier());
	}

	public FuzzyMultiClassifierContextOri() {
		super();
		this.classSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	public FuzzyMultiClassifierContextOri(double threshold) {
		super(threshold);
		this.classSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	public FuzzyMultiClassifierContextOri(int objectsCount, int attributesCount) {
		super(objectsCount, attributesCount);
		this.classSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	public FuzzyMultiClassifierContextOri(int objectsCount, int attributesCount, double threshold) {
		super(objectsCount, attributesCount, threshold);
		this.classSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	@Override
	public String toString() {
		// For now
		return "FuzzyMultiClassifierContext " + super.toString() + classesSet.toString();
	}

	// Remove object from hashMap only
	public void removeObjectOnly(FullObject<String, String> o) {

		super.removeObjectOnly(o);
		if(classSetMap.containsKey(o.getIdentifier()))
			classSetMap.remove(o.getIdentifier());
	
	}

	public void transpose() {
		// needs to be thought of 
		super.transpose();
	}

	
	public void renameObject(String oldObject, String newObject) {
		super.renameObject(oldObject, newObject);
		classSetMap.put(newObject, classSetMap.get(oldObject));
		classSetMap.remove(oldObject);
	}

   @Override
    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
	   		ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
	    	for(Concept<String, FullObject<String, String>> c : super.getConcepts()) {
	    		
	    		// A map that stores count of objects for various classes
	    		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
	        	FuzzyMultiClassedConcept fcc = new FuzzyMultiClassedConcept(c);
	        	for(FullObject<String, String> obj : fcc.getExtent()) {
	        		Set<String> clazzSet = classSetMap.get(obj.getIdentifier());
	        		if(clazzSet!=null) {
	        		for(String clazz: clazzSet) {
	        			if(countMap.containsKey(clazz))
	        				countMap.put(clazz, countMap.get(clazz)+1);
	        			else
	        				countMap.put(clazz,1);
	        		}
	        		}
	        	}
	   
	        	int i=0;
	        	for(Set<String> clazzSet : classesSet) {
	        		for(String clazz: clazzSet)
	        			if(countMap.containsKey(clazz))
	        				fcc.addProb(i,countMap.get(clazz)*1.0/fcc.getExtent().size());
	        			else
	        				fcc.addProb(i,0.0);
	 //       		System.out.println(fcc.getProbsList());
		       	    
	        		i++;
	        	}
	        	fuzzyLattice.add(fcc);	
	        }
	    	return fuzzyLattice;
	    }
   
   public HashMap<String, Set<String>> getClassMap() {
	return classSetMap;
}

public void setClassMap(HashMap<String, Set<String>> classMap) {
	this.classSetMap = classMap;
}

public ArrayList<Set<String>> getClasses() {
	return classesSet;
}

public void setClasses(ArrayList<Set<String>> classes) {
	this.classesSet = classes;
}

public String getClassAsString(int i, int index) {
	return (String) this.classesSet.get(i).toArray()[index];
}

public List<String> getClassesAsString(int index) {
	List<String> classList = new ArrayList<String>();
	for(int i=0;i<classesSet.size();i++)
		classList.add(getClassAsString(i, index));
	return classList;
}

public String getClassesAsString(List<List<Integer>> indices) {
	String clazz = "";
	for(int i=0;i<indices.size();i++)
		clazz= clazz+ getClassAsString(i,indices.get(i)) + ",";
	return clazz.substring(0,clazz.length()-1);
}

public String getClassAsString(int i, List<Integer> indices) {
	String clazz = "";
	for(int index: indices)
		clazz= clazz+ this.classesSet.get(i).toArray()[index] + ",";
	return clazz.substring(0,clazz.length()-1);
}

@Override
   public Set<Concept<String, FullObject<String, String>>> getConceptsWithoutConsideredElements() {
	ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
   	for(Concept<String, FullObject<String, String>> c : super.getConcepts()) {
		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
     	FuzzyMultiClassedConcept fcc = new FuzzyMultiClassedConcept(c);
    	for(FullObject<String, String> obj : fcc.getExtent()) {
    		Set<String> clazzSet = classSetMap.get(obj.getIdentifier());
    		for(String clazz: clazzSet) {
    			if(countMap.containsKey(clazz))
    				countMap.put(clazz, countMap.get(clazz)+1);
    			else
    				countMap.put(clazz,1);
    		}
    	}

   	   	int i=0;
   	 	for(Set<String> clazzSet : classesSet) {
   	    	for(String clazz: clazzSet) 
    			if(countMap.containsKey(clazz))
    				fcc.addProb(i,countMap.get(clazz)*1.0/fcc.getExtent().size());
    			else
    				fcc.addProb(i,0.0);
    		i++;
    	}
    	fuzzyLattice.add(fcc);	
    }
	return fuzzyLattice;
   }

public Set<String> getClass(String oid) {
	return this.classSetMap.get(oid);
}
  public ArrayList<Set<String>> getClassesSet() {
	  return classesSet;
  }
  
  public String getClassesAsString() {
	  StringBuffer sb = new StringBuffer();
	  for(Set<String> classSet : classesSet)
		  sb.append("{" + classSet + "}\n");
	  return sb.toString();
  }

public int getClassesCount() {
	return this.classesSet.size();
}

public List<String> getClassAsString(List<Integer> classList) {
	List<String> classes = new ArrayList<String>();
	for(int i=0;i<classesSet.size();i++)
		classes.add(classesSet.get(i).toArray(new String[0])[classList.get(i)]);
	return classes;
}
public List<List<String>> getProbClassAsString(Concept<String, FullObject<String, String>> c) {
	List<List<String>> csList = new ArrayList<List<String>>();
	FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept)c;
	int i=0;
	for(List<Integer> ccList : fcc.getProbClass()) {
		List<String> cscList = new ArrayList<String>();
		for(Integer ic : ccList) {
			cscList.add(classesSet.get(i).toArray(new String[0])[ic]);
		i++;
		}
		csList.add(cscList);
	}
	return csList;
}

public Set<Concept<String, FullObject<String, String>>> getConceptsOfObject(String oString) {
	 
    ListSet<Concept<String, FullObject<String, String>>> subConcepts = new ListSet<Concept<String, FullObject<String, String>>>();

	 for(Concept<String,FullObject<String, String>> cObj : this.getConcepts()) {
		 for(FullObject<String, String> o: cObj.getExtent())
			 if(o.getIdentifier().equals(oString))
				 subConcepts.add(cObj);	 
	 }
	 return subConcepts;
}

public Set<Concept<String, FullObject<String, String>>> getConceptsOfObject(FullObject<String,String> o) {
	 
    ListSet<Concept<String, FullObject<String, String>>> subConcepts = new ListSet<Concept<String, FullObject<String, String>>>();

	 for(Concept<String,FullObject<String, String>> cObj : this.getConcepts()) {
		 if(cObj.getExtent().contains(o))
			subConcepts.add(cObj);	 
	 }
	 return subConcepts;
}

public HashMap<String,Concept<String,FullObject<String, String>>> getMinimalConceptMap() {
	 HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = new HashMap<String,Concept<String,FullObject<String, String>>>();
	 
	 for(Concept<String,FullObject<String, String>> cObj : this.getConcepts()) {
			for(FullObject<String,String> o : cObj.getExtent())
				if(minConceptMap.get(o.getIdentifier()) == null)
					minConceptMap.put(o.getIdentifier(), cObj);
				else {
					if(cObj.getExtent().size() < minConceptMap.get(o.getIdentifier()).getExtent().size())
						minConceptMap.put(o.getIdentifier(),cObj);
					}
}
   return minConceptMap;
}
public HashMap<String,Set<String>> classify() {
	
	 HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = getMinimalConceptMap();
		 
	 HashMap<String,Set<String>> predictedMap = new HashMap<String,Set<String>>();
	 for(String ostr : minConceptMap.keySet()) {
		 Set<String> cSet = new TreeSet<String>();
		 
		 Concept<String,FullObject<String, String>> cObj = minConceptMap.get(ostr);
	//	 	 System.out.println("O= "+ostr + " Obj= " + cObj);
		 
		 for(List<String> sList : getProbClassAsString(cObj))
			 for(String sElement: sList)
				 cSet.add(sElement);
		 predictedMap.put(ostr, cSet);
	 }
	
	 int count=0;
	 System.out.println("Object\tPredicted\tActual\tSuccess");
	 for(String o : predictedMap.keySet()) {
		 System.out.println(o + "\t" + predictedMap.get(o) + "\t" + classSetMap.get(o)
		  + "\t" + classSetMap.get(o).contains(predictedMap.get(o).toArray(new String[0])[0]));
		 if(classSetMap.get(o).contains(predictedMap.get(o).toArray(new String[0])[0]))
			 count++;
	 }
	 System.out.println("Objects classified correctly:" + count);
	 System.out.println("Total objects:"+ this.getObjectCount());
	 System.out.println("% Success:" + count*100.0/this.getObjectCount());
	 return predictedMap;
		 
}

public HashMap<String, Set<String>> getClassSetMap() {
	return classSetMap;
}

public void setClassSetMap(HashMap<String, Set<String>> classSetMap) {
	this.classSetMap = classSetMap;
}
}
