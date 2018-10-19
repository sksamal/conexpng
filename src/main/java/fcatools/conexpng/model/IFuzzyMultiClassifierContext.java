package fcatools.conexpng.model;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.thoughtworks.xstream.XStream;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;
import fcatools.conexpng.gui.lattice.Edge;
import fcatools.conexpng.gui.lattice.LatticeGraph;
import fcatools.conexpng.gui.lattice.Node;

/**
 * A specialization of FormalContext<String,String> that can be used for multi-classification
 * Set of classes S1 = { A1, B1, C1, D1 } and S2 = { A2, B2, C2, D2 } 
 * An object may be classified as A1 in set S1 and B2 in set S2 and so on.
 * 
 */
public class IFuzzyMultiClassifierContext extends FuzzyMultiClassifierContext {

	// Incremental Fuzzy Concept lattice
	private Set<Concept<String, FullObject<String, String>>> conceptLattice ;
	private LatticeGraph latticeGraph = new LatticeGraph();
	private HashMap<Long,FullObject<String,String>> objMap;
	private HashMap<String,Long> attrIdMap = null;

	private Set<FullObject<String, String>> newObjects = new ListSet<FullObject<String, String>>();

	
	public boolean addObject(String object, String classifier, List<String> attributes, List<Double> values) throws IllegalObjectException {
		return this.addObject(object, classifier, attributes.toArray(new String[0]),values.toArray(new Double[0]));
	}

	public boolean addObject(String object, Set<String> classifiers, List<String> attributes, List<Double> values) throws IllegalObjectException {
		return this.addObject(object, classifiers, attributes.toArray(new String[0]),values.toArray(new Double[0]));
	}

	public void generateAttrIdMap() {
		attrIdMap = new HashMap<String,Long>();
		Long value = (long)1;
        for(int i=this.getAttributes().size()-1;i>=0;i--) {
        	attrIdMap.put(this.getAttributeAtIndex(i), value);
        	value = value *2;
 //           System.out.println(sortedAttributes[i] + " " + attrIdMap.get(sortedAttributes[i]));
        }
   
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

	public void toXml(String fileName) {
		
        XStream xstream = new XStream();
        PrintWriter pw = null;
        try {
                pw = new PrintWriter(new File(fileName));
        } catch (Exception e) {
                throw new RuntimeException(e);
        }
        	pw.print(xstream.toXML(this)+"\n");
        	pw.print(conceptLattice + "\n");
        	pw.println(newObjects + "\n");
        	pw.close();
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
	 
	public Node getMaximalConcept(Set<String> intent, Node generator) {
		boolean parentIsMaximal = true;
		Node maximalGenerator = generator;
		while(parentIsMaximal) {
			parentIsMaximal = false;
			List<Node> parents = maximalGenerator.getParentNodes();
			for(Node parent : parents) {
				if(parent.getAttributes().containsAll(intent)) {
					maximalGenerator = parent;
				    parentIsMaximal = true;
				    break;
			}
		}
		}
		return maximalGenerator;
	}
	
	public Node addIntent(Set<String> intent, Node generator) {
//		System.out.println("addIntent, intent=" + intent);
		Node maximalGenerator = getMaximalConcept(intent,generator);
//		System.out.println("maximalConcept");
//		printConcept(maximalGenerator);
				
		
		if(maximalGenerator.getAttributes().containsAll(intent) && intent.containsAll(maximalGenerator.getAttributes())) 
			return maximalGenerator;
		
		List<Node> maxGenParents = maximalGenerator.getParentNodes();
		List<Node> newParents = new ArrayList<Node>();
//		System.out.println("maxGenparents=" + maxGenParents.size());
//		System.out.println("newParents=" + newParents + " " + newParents.size());

//		int i=0;
//		for(Node parent: maxGenParents) {
//			i++;
//			System.out.print(i + ": ");
//			printConcept(parent);
//		}
		
		for(Node parent: maxGenParents) {
			Node candidate = parent;
			if(!intent.containsAll(parent.getAttributes())) {
				TreeSet<String> intersectSet = this.intersection(parent.getAttributes(),intent);
				candidate = addIntent(intersectSet, parent);
			}

			boolean addParent = true;

//			System.out.println("newParents=" + newParents + " " + newParents.size());
			for(Node newParent: newParents.toArray(new Node[0])) {
				if(newParent.getAttributes().containsAll(candidate.getAttributes())) {
					addParent = false;
				    break;
				}
				else if(candidate.getAttributes().containsAll(newParent.getAttributes())) {
					newParents.remove(newParent);
				}
				
			} // End For
			
			if(addParent) {
				newParents.add(candidate);
			}
		}   // End For
		
//		System.out.println("maximalGenerator:"); 
//		printConcept(maximalGenerator);
//		System.out.println("newParents=" + newParents + " " + newParents.size());
//		if(newParents.size() > 0) {
//			for(Node p : newParents)
//		printConcept(p);
//		}

		// Create new Node
		Node newNode = new Node(maximalGenerator.getObjects(), intent, maximalGenerator.getFullObjects(),maximalGenerator.getX(), maximalGenerator.getY());
		this.latticeGraph.getNodes().add(newNode);
		
		for(Node parent: newParents) {
			latticeGraph.getEdges().remove(new Edge(parent,maximalGenerator));
			parent.getChildNodes().remove(maximalGenerator);
			maximalGenerator.getParentNodes().remove(parent);
			parent.addChildNode(newNode);
			newNode.addParentNode(parent);
			latticeGraph.getEdges().add(new Edge(parent,newNode));			
		}							
		
			// set link and return
			newNode.addChildNode(maximalGenerator);
			maximalGenerator.addParentNode(newNode);
			latticeGraph.getEdges().add(new Edge(newNode,maximalGenerator));
			
//			System.out.println("Newnode=");
//			printConcept(newNode);
			return newNode;
	}

	public Set<Concept<String, FullObject<String, String>>> getConceptsUsingAddIntent() {
		if(attrIdMap == null)
			generateAttrIdMap();
		
		 if(this.conceptLattice ==null) {
				newObjects.clear();
				this.conceptLattice = super.getConcepts();
				latticeGraph.createLattice(conceptLattice);
				return this.conceptLattice;
		 }
		
		// nothing changed, just send whats existing, no new objects added
	   		if(this.conceptLattice !=null && newObjects.size()==0)
	   			return conceptLattice;
	   		
		 // Add Intent Algorithm
	   	    for(FullObject<String, String> newobj : newObjects) {
	   	    	
	 //  	    	System.out.println("ADDING object" + newobj);
	        	FullObject<String, String> newObj = this.getObject(newobj.getIdentifier());
	   	    	
	   	    	Long id = (long)0;
	   			for(String attribute:newObj.getDescription().getAttributes())
	   				id+= attrIdMap.get(attribute);

      			Node n = addIntent(newObj.getDescription().getAttributes(), latticeGraph.getBottomNode());
   				n.setId(id);
		
   				
	   			List<Node> nodes = new ArrayList<Node>();
	   			nodes.add(n);
				
   				
	   			while(nodes.size()>0) {
	   				n = nodes.remove(0);
   	//				System.out.println("newobj=" + newObj.getDescription().getAttributes());
	   				if((newObj.getDescription().getAttributes().containsAll(n.getAttributes())) || n.getAttributes().isEmpty()) {
	   					n.getObjects().add(newObj.getIdentifier());
	   					n.getFullObjects().add(newObj);
//	   					if(n.getParentNodes().size()>0) {
//	   						for(Node x : n.getParentNodes())
//	   							printConcept(x);
//	   						
//	   					}
	   				nodes.addAll(n.getParentNodes());
	   				}
	   	    }
	   			
	   	   }
	   	    this.conceptLattice = latticeGraph.getConcepts();
		 // Finally return the concepts
		 return conceptLattice;
	}

	public LatticeGraph getLatticeUsingAddIntent() {
		if(attrIdMap == null)
			generateAttrIdMap();
		
		 if(this.conceptLattice ==null) {
				newObjects.clear();
				this.conceptLattice = super.getConcepts();
				latticeGraph.createLattice(conceptLattice);
				return latticeGraph;
		 }
		
		// nothing changed, just send whats existing, no new objects added
	   		if(this.conceptLattice !=null && newObjects.size()==0)
	   			return latticeGraph;
	   		
	   		long tms1=0, tms2=0;
		 // Add Intent Algorithm
	   	    for(FullObject<String, String> newobj : newObjects) {
	   	    	
	 //  	    	System.out.println("ADDING object" + newobj);
	        	FullObject<String, String> newObj = this.getObject(newobj.getIdentifier());
	   	    	
	   	    	Long id = (long)0;
	   			for(String attribute:newObj.getDescription().getAttributes())
	   				id+= attrIdMap.get(attribute);

	   			long ms = System.currentTimeMillis();
      			Node n = addIntent(newObj.getDescription().getAttributes(), latticeGraph.getBottomNode());
   				n.setId(id);
   				tms1+= (System.currentTimeMillis() - ms);
   				
	   			List<Node> nodes = new ArrayList<Node>();
	   			nodes.add(n);
   			
	   			ms = System.currentTimeMillis();
	   			while(nodes.size()>0) {
	   				n = nodes.remove(0);
   	//				System.out.println("newobj=" + newObj.getDescription().getAttributes());
	   				if((newObj.getDescription().getAttributes().containsAll(n.getAttributes())) || n.getAttributes().isEmpty()) {
	   					n.getObjects().add(newObj.getIdentifier());
	   					n.getFullObjects().add(newObj);
//	   					if(n.getParentNodes().size()>0) {
//	   						for(Node x : n.getParentNodes())
//	   							printConcept(x);
//	   						
//	   					}
	   				nodes.addAll(n.getParentNodes());
	   				}
	   	    }
	   		tms2+= (System.currentTimeMillis() - ms);
	   	   		
	   	   }
	   	 System.out.println("AddIntent took " + tms1 + " ms, adding obj to concept took " + tms2 + " ms");
		 // Finally return the graph
		 return latticeGraph;
	}

   @Override
    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
	   
		if(attrIdMap == null)
			generateAttrIdMap();
	
		// compute all if existing lattice is null
	   if(this.conceptLattice ==null) {
		//   System.out.println("Lattice not present, regenerating from scratch");
		   newObjects.clear();
		   this.conceptLattice = super.getConcepts();
		   return this.conceptLattice;
	   }
   	
   		// nothing changed, just send whats existing, no new objects added
   		if(this.conceptLattice !=null && newObjects.size()==0)
   			return conceptLattice;
   		
	//    System.out.println(" added " + newObjects.size() + " new objects, using addIntent algorithm");
        
		// Add Intent algorithm
	    	// concept is (A,B)
	 		// A is set of objects which is extent
	 		// B is set of attributes which is intent
	 		
        for(FullObject<String, String> newobj : newObjects) {
        	
        	ListSet<Concept<String, FullObject<String, String>>> newConceptLattice = new ListSet<Concept<String, FullObject<String, String>>>();
        	int z = 0;
        	FullObject<String, String> newObj = this.getObject(newobj.getIdentifier());

            for(Concept<String, FullObject<String,String>> c : this.conceptLattice) {
            	z++;
      //      	System.out.println("Concept " + z + " [" + c.getExtent() + c.getIntent() + "]");
            	// Get the extent A of concept (A,B)
            	Set<FullObject<String,String>> extent = c.getExtent();   // A
       //     	System.out.println("A=" + c.getExtent());
       //     	System.out.println("B=" + c.getIntent());

            	Set<String> intent = c.getIntent();  // B
//            	
            	// newObj is (g,g')
      //      	System.out.println("New obj = [" + newObj.getIdentifier() + "," + newObj.getDescription().getAttributes() + "]");
            	// Case I - Add a new concept (A U g',B) if g' contains B 
            	if(newObj.getDescription().getAttributes().containsAll(intent)) { // g' contains B ?
            	      c.getExtent().add(newObj);  // add (A U g, B) to new context
            	      addConceptToLattice(newConceptLattice,c);
         //   	      System.out.println("Case I applies, \n\tConcept " + z + " +newObj added to new lattice");
         //   	      System.out.println(c);
            	}
            	else {
            		
            		//Case III - Add existing concept (A,B) to the new lattice 
            		//and another one if its the generator concept (not duplicate)
          //  		 System.out.println("Case III, \n\tadd Concept " + z + " anyway to the new lattice");
          //  		 System.out.println(c);

            		addConceptToLattice(newConceptLattice,c);  	  
            		
            		// compute D = intersection of B and g'
            		Set<String> newIntentAttributes = this.intersection(newObj.getDescription().getAttributes(),intent);
          //  		System.out.println("Case II check. \n\t D = B int g' = " + newIntentAttributes.toString());
            		// Case II - Add new concept (D',D) to new lattice
            		// but only if (A,B) is its unique generator
            		// Algorithm of Norris
            		
            		// Update D' as A U g
            		LatticeConcept newC = new LatticeConcept();
            		for(FullObject<String,String> f : c.getExtent())
            			newC.getExtent().add(f);
           // 		System.out.println(c.getExtent().size() + " " + newC.getExtent().size());
            		newC.getExtent().add(newObj);
         
            		// Update D as D
            		for(String attr: newIntentAttributes) {
            			newC.getIntent().add(attr);
            			newC.addToId(attrIdMap.get(attr));
            		}
            //		System.out.println("\tD = " + newC.getIntent());
            //		System.out.println("\tD' = " + newC.getExtent());
            
            		
            		// find all objects that have the new attributes D
            //		System.out.println("\tFinding other objects having attributes as D if they exist..");
            		boolean donotAdd = false;
            		int o = 0;
          	        for (FullObject<String, String> f : this.getObjects()) {
          	        	o++;
          	        	if(newC.getExtentIds().contains(f.getIdentifier()))
          	        		continue;
         // 	        	System.out.println("\t\tObject " + o + ": " + f.getDescription().getAttributes());
          	        	boolean subset = true;
          	        	// Does f contain all attributes D
          	        	for (String attr : newIntentAttributes) {
          	        		if (!f.getDescription().getAttributes().contains(attr)) 
          	        			{ subset = false; break; }
          	        	}
           // 	        if(subset)
           // 	        	System.out.println("\t\tObject " + o + " contains all newintentattributes");
            	        
           // 	        System.out.println("Does newC extent (D') contains object " + o + " " + f.getIdentifier() + " ?");
           // 	        System.out.println("\nD'=" + newC.getExtent());
           // 	        System.out.println("\nf=" + f);
          	        	// If f contains all new attributes, is it present in D'
            	        if(subset && !newC.getExtent().contains(f))  {
            	        	donotAdd = true; // (A,B) is not a generator of (D',D), hence no need to add, as it will be a duplicate
     //       	        	System.out.println("\t\tNo:(A,B) not generator of (D'D), hence skipping.");
            	            break;
            	        } 
          //  	        else
         //   	        	System.out.print("Yes");
            	    }
          	        
          	        // Add (D',D) to the new lattice
          	        if(!donotAdd) {
       //   	        	System.out.println("\t\tYes: Adding (D',D) since it is a generator");
                	    addConceptToLattice(newConceptLattice,newC);
          //      	    System.out.println(newC);
          	        }
            	}
            	
            } // end of traversing lattice objects
            	this.conceptLattice = newConceptLattice;
 //           	System.out.println(conceptLattice.size());
            } // end of newobjects list
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
	//        		System.out.println(clazzSet);
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
     
 	public static void printConcept(Node n) {
		// concept (A,B)
		// A is set of objects which is extent
		// B is set of attributes which is intent
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for(FullObject<String, String> o : n.getFullObjects()) 
				sb.append(o.getIdentifier() + ",");//  "[" + o.getDescription().getAttributes() + "]" + ",");
			sb.append("},{");
			for(String attr : n.getAttributes())
				sb.append(attr + ",");
			sb.append("}>");
			System.out.println(sb.toString());
	}
 	
 	public static void printConcepts(List<Node> nodes) {
 		int i=1;
 		for(Node n: nodes) {
 			printConcept(n);
 		}
 	}
     
}
