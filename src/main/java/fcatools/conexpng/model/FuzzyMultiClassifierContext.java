package fcatools.conexpng.model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
public class FuzzyMultiClassifierContext extends FuzzyFormalContext {

	// A Set of hashMaps holding object and set of classes for training and test sets
	protected HashMap<String, Set<String>> trainingSetMap;
	protected HashMap<String, Set<String>> testSetMap;
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
			trainingSetMap.put(object,classSet);
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
			trainingSetMap.put(object, classifiers);
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
			trainingSetMap.put(o.getIdentifier(),classSet);
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
			trainingSetMap.put(o.getIdentifier(), classifiers);
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
			trainingSetMap.put(o.getIdentifier(),classSet);
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
		if(super.removeObject(getObject(id))) {
			Set<String> clazzSet = trainingSetMap.get(id);
			trainingSetMap.remove(id);
			
			Set<String> removeList = new TreeSet<String>();
			for(String clazz : clazzSet)
				removeList.add(clazz);
			
			for(Set<String> classSet : trainingSetMap.values()) {
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
			trainingSetMap.remove(removeSet);
			}
		}
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(FullObject<String, String> object) throws IllegalObjectException {
		return removeObject(object.getIdentifier());
	}

	public FuzzyMultiClassifierContext() {
		super();
		this.trainingSetMap = new HashMap<>();
		this.testSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	public FuzzyMultiClassifierContext(double threshold) {
		super(threshold);
		this.trainingSetMap = new HashMap<>();
		this.testSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	public FuzzyMultiClassifierContext(int objectsCount, int attributesCount) {
		super(objectsCount, attributesCount);
		this.trainingSetMap = new HashMap<>();
		this.testSetMap = new HashMap<>();
		this.classesSet = new ArrayList<>();
	}

	public FuzzyMultiClassifierContext(int objectsCount, int attributesCount, double threshold) {
		super(objectsCount, attributesCount, threshold);
		this.trainingSetMap = new HashMap<>();
		this.testSetMap = new HashMap<>();
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
		if(trainingSetMap.containsKey(o.getIdentifier()))
			trainingSetMap.remove(o.getIdentifier());
	
	}

	public void transpose() {
		// needs to be thought of 
		super.transpose();
	}

	
	public void renameObject(String oldObject, String newObject) {
		super.renameObject(oldObject, newObject);
		trainingSetMap.put(newObject, trainingSetMap.get(oldObject));
		trainingSetMap.remove(oldObject);
	}

   @Override
    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
	   		ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
	   		PrintWriter pw = null;
	   		try {
	   		pw = new PrintWriter("data/concepts.txt");
	   		}
	   		catch(FileNotFoundException f) {
	   			f.printStackTrace();
	   		}
	   		
	   		//fix for objs with no attributes
//	   		Set<FullObject<String, String>> tmpobjs = new ListSet<FullObject<String, String>>();
//	   	  	for(FullObject<String, String> o : this.getObjects()) 
//	   	      tmpobjs.add(o);
	   	  	//fix for concepts/objects with no attributes
        	LatticeConcept nullc = new LatticeConcept();
        	for(FullObject<String, String> o : this.getObjects()) 
  	   	      nullc.getExtent().add(o);
  	   	  
	   	  	
	    	for(Concept<String, FullObject<String, String>> c : super.getConcepts()) {
	    		
	    		
	    		pw.println(c);
	    		// A map that stores count of objects for various classes
	    		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
	        	FuzzyMultiClassedConcept fcc = new FuzzyMultiClassedConcept(c);
	        	for(FullObject<String, String> obj : fcc.getExtent()) {
	        		Set<String> clazzSet = trainingSetMap.get(obj.getIdentifier());
	        		if(clazzSet!=null) {
	        		for(String clazz: clazzSet) {
	        			if(countMap.containsKey(clazz))
	        				countMap.put(clazz, countMap.get(clazz)+1);
	        			else
	        				countMap.put(clazz,1);
	        		}
	        		}
	          		//fix for concepts/objs with null attrs 
	        		if(nullc.getExtent().contains(obj)) nullc.getExtent().remove(obj);
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
	    	pw.close();
	    	
	    	//fix for null concept
	   		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
       	 	FuzzyMultiClassedConcept fcc1 = new FuzzyMultiClassedConcept(nullc);
        	int i=0;
        	for(Set<String> clazzSet : classesSet) {
        		for(String clazz: clazzSet)
        			if(countMap.containsKey(clazz))
        				fcc1.addProb(i,countMap.get(clazz)*1.0/fcc1.getExtent().size());
        			else
        				fcc1.addProb(i,0.0);
        		i++;
        	}
        	fuzzyLattice.add(fcc1);
        	
	    	return fuzzyLattice;
	    }
   
   public HashMap<String, Set<String>> getTrainingSet() {
	return trainingSetMap;
}

public void setClassMap(HashMap<String, Set<String>> classMap) {
	this.trainingSetMap = classMap;
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
    		Set<String> clazzSet = trainingSetMap.get(obj.getIdentifier());
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
	return this.trainingSetMap.get(oid);
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
			for(FullObject<String,String> o : cObj.getExtent()) {				
				if(minConceptMap.get(o.getIdentifier()) == null)
					minConceptMap.put(o.getIdentifier(), cObj);
				else {
					if(cObj.getExtent().size() < minConceptMap.get(o.getIdentifier()).getExtent().size())
						minConceptMap.put(o.getIdentifier(),cObj);
					}
			}
			
//	 System.out.println(cObj);
}
//   System.out.println("Size:" + this.getObjects().size());
//   System.out.println("Size:" + minConceptMap.size());
   return minConceptMap;
}

public void partition(double testPercentage) {
	long totalSize = this.objects.size();
	
	// generate test indices
	long testSize = (int)(totalSize*(testPercentage));
	Set<Long> test = new TreeSet<Long>();
	System.out.println("TrainSize=" + (totalSize - testSize));
	System.out.println("TestSize=" + testSize + "(" + testPercentage + "%)");	

	while(test.size() < testSize) {
		long number =(long)(Math.random()*totalSize);
		test.add(number);
	}
	
	long index=0;
	for(String obj : this.trainingSetMap.keySet()) {
		if(test.contains(index)) {
			testSetMap.put(obj, trainingSetMap.get(obj));
		}
		index++;
	}
	
	for(String obj:this.testSetMap.keySet())
		trainingSetMap.remove(obj);

	
}

public void partition(double testPercentage, int seed) {
	long totalSize = this.objects.size();
	
	// generate test indices
	long testSize = (int)(totalSize*(testPercentage));
	Set<Long> test = new TreeSet<Long>();
	System.out.println("TrainSize=" + (totalSize - testSize));
	System.out.println("TestSize=" + testSize + "(" + testPercentage + "%)");
	Random rgen = new Random(seed);

	while(test.size() < testSize) {
		//long number =(long)(Math.random()*totalSize); 
		long number = (long)(rgen.nextDouble()*totalSize);
		test.add(number);
	}
	
	long index=0;
	for(String obj : this.trainingSetMap.keySet()) {
		if(test.contains(index)) {
			testSetMap.put(obj, trainingSetMap.get(obj));
		}
		index++;
	}
	
	for(String obj:this.testSetMap.keySet())
		trainingSetMap.remove(obj);

	
}

public void kpartition(int k) {
	
	long totalSize = this.objects.size();
	HashMap<String, Set<String>> kset[] = new HashMap[k];
	for(int i=0;i<k;i++)
		kset[i] = new HashMap<String,Set<String>>();
	
	long testSize = totalSize/k;
	System.out.println("TrainSize=" + (totalSize - testSize));
	System.out.println("TestSize=" + testSize);
	Set<String> keys = this.trainingSetMap.keySet();
	
	for(String key: keys) {
		int number =(int)(Math.random()*(k)); 
		kset[number].put(key,this.trainingSetMap.get(key));
	}
	
	for(int i=0;i<k;i++) {
		FuzzyMultiClassifierContext fmcc = new FuzzyMultiClassifierContext();
		
		for (int j=0;j<k ; j++) {
			try {
				
			for(String obj: kset[j].keySet()) {
				for(String attribute: this.getAttributesForObject(obj)) {
					Double value = this.composition.get(new OAPair<String,String>(obj,attribute));
					if(i!=j)
							fmcc.addObject(this.getObject(obj),trainingSetMap.get(obj), value);
					else
						{
					Set<String> nullSet = null;
					fmcc.addObject(this.getObject(obj),nullSet, value);
						}
				}
			  }
			}
			catch (IllegalObjectException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();

		 fmcc.classify();
		}	
	}
	}
}

public HashMap<String,Set<String>> getTestSet() {
  return this.testSetMap;
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
		 System.out.println(o + "\t" + predictedMap.get(o) + "\t" + trainingSetMap.get(o)
		  + "\t" + trainingSetMap.get(o).contains(predictedMap.get(o).toArray(new String[0])[0]));
		 if(trainingSetMap.get(o).contains(predictedMap.get(o).toArray(new String[0])[0]))
			 count++;
	 }
	 System.out.println("Objects classified correctly:" + count);
	 System.out.println("Total objects:"+ this.getObjectCount());
	 System.out.println("% Success:" + count*100.0/this.getObjectCount());
	 return predictedMap;
		 
}

public void setTrainingSet(HashMap<String, Set<String>> classSetMap) {
	this.trainingSetMap = classSetMap;
}
}
