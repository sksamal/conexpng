package fcatools.conexpng.gui.workers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.StatusMessage;
import fcatools.conexpng.gui.lattice.LatticeGraphComputer;
import fcatools.conexpng.gui.lattice.LatticeSettings;
import fcatools.conexpng.gui.lattice.LatticeView;
import fcatools.conexpng.model.FormalContext;
import fcatools.conexpng.model.FuzzyClassedConcept;
import fcatools.conexpng.model.FuzzyClassifierContext;
import fcatools.conexpng.model.FuzzyMultiClassedConcept;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;
import fcatools.conexpng.model.LatticeConcept;

/**
 * Worker to calculate concepts and lattice.
 * 
 * @author Torsten Casselt
 */
public class ConceptWorker extends AbstractWorker {

    private Conf state;
    private FormalContext context;
    private boolean lattice;
    private LatticeView view;
    private ListSet<Concept<String, FullObject<String, String>>> conceptLattice;

    /**
     * Creates the concept worker.
     * 
     * @param view
     *            needed to fetch information to work with from
     * @param lattice
     *            true if the lattice shall be computed, false if only the
     *            concepts
     * @param progressBarId
     */
    public ConceptWorker(LatticeView view, boolean lattice, Long progressBarId) {
        super(progressBarId);
        this.view = view;
        this.state = view.getState();
        this.statusBar = state.getStatusBar();
        this.statusBar.setIndeterminate(progressBarId, false);
        this.context = state.context;
        this.lattice = lattice;
    }

    @Override
    protected Void doInBackground() throws Exception {

        conceptLattice = new ListSet<Concept<String, FullObject<String, String>>>();
        HashMap<String, Set<String>> extentPerAttr = new HashMap<String, Set<String>>();
        int progress = 0;

        state.startCalculation(StatusMessage.CALCULATINGCONCEPTS);
        setProgressBarMessage(StatusMessage.CALCULATINGCONCEPTS.toString());
        init();

        /*
         * Step 1: Initialize a list of concept extents. To begin with, write
         * for each attribute m # M the attribute extent {m}$ to this list (if
         * not already present).
         */
        for (String s : context.getAttributes()) {
            if (!context.getDontConsideredAttr().contains(s)) {
                TreeSet<String> set = new TreeSet<String>();
                for (FullObject<String, String> f : context.getObjects()) {
                    if (f.getDescription().getAttributes().contains(s) && (!context.getDontConsideredObj().contains(f))) {
                        set.add(f.getIdentifier());
                    }
                }
                extentPerAttr.put(s, set);
            }
        }
        // checks if the worker is cancelled
        if (isCancelled()) {
            return null;
        }
        /*
         * Step 2: For any two sets in this list, compute their intersection. If
         * the result is a set that is not yet in the list, then extend the list
         * by this set. With the extended list, continue to build all pairwise
         * intersections.
         */
        HashMap<String, Set<String>> temp = new HashMap<String, Set<String>>();
        for (String s : extentPerAttr.keySet()) {
            for (String t : extentPerAttr.keySet()) {
                if (!s.equals(t)) {
                    Set<String> result = context.intersection(extentPerAttr.get(s), extentPerAttr.get(t));
                    if (!extentPerAttr.values().contains(result)) {
                        if (!temp.containsValue(result)) {
                            temp.put(s + ", " + t, result);
                        }
                    }
                }
            }
            setProgress((int) (((float) progress++ / extentPerAttr.keySet().size()) * 99));
        }
        extentPerAttr.putAll(temp);
        // checks if the worker is cancelled
        if (isCancelled()) {
            return null;
        }
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
                    Set<String> result = context.intersection(extentPerAttr.get(s), extentPerAttr.get(t));
                    if (!extentPerAttr.values().contains(result)) {
                        notcontained = true;
                        break;
                    }
                }
            }
        }
        if (!notcontained) {
            TreeSet<String> set = new TreeSet<String>();
            for (FullObject<String, String> f : context.getObjects()) {
                set.add(f.getIdentifier());
            }
            if (!extentPerAttr.values().contains(set))
                extentPerAttr.put("", set);
        }
        // checks if the worker is cancelled
        if (isCancelled()) {
            return null;
        }
        /*
         * Step 4: For every concept extent A in the list compute the
         * corresponding intent A' to obtain a list of all formal concepts
         * (A,A') of (G,M, I).
         */

        HashSet<Set<String>> extents = new HashSet<Set<String>>();
        for (Set<String> e : extentPerAttr.values()) {
            if (!extents.contains(e))
                extents.add(e);
        }
        
        String[] sortedAttributes = context.getAttributes().toArray(new String[0]);
        Arrays.sort(sortedAttributes);
        HashMap<String,Long> attrIdMap = new HashMap<String,Long>();
        Long value = (long)1;
        for(int i=sortedAttributes.length-1;i>=0;i--) {
        	attrIdMap.put(sortedAttributes[i], value);
        	value = value *2;
        }

        for (Set<String> e : extents) {
            TreeSet<String> intents = new TreeSet<String>();
            int count = 0;
            Concept<String, FullObject<String, String>> c = new LatticeConcept();
            if (e.isEmpty()) {
                intents.addAll(context.getAttributes());
            } else
                for (FullObject<String, String> i : context.getObjects()) {
                    if (!context.getDontConsideredObj().contains(i)) {
                        if (e.contains(i.getIdentifier().toString())) {
                            TreeSet<String> prev = context.sort(i.getDescription().getAttributes());
                            if (count > 0) {
                                intents = context.intersection(prev, intents);
                            } else {
                                intents = prev;
                            }
                            count++;
                            c.getExtent().add(i);
                        }
                    }
                }
            // checks if the worker is cancelled
            if (isCancelled()) {
                return null;
            }
            for (String s : intents) {
                if (!context.getDontConsideredAttr().contains(s)) {
                    c.getIntent().add(s);
                    ((LatticeConcept)c).addToId(attrIdMap.get(s));
                }
            }
            conceptLattice.add(c);
        }
   		
      if(this.state.context instanceof FuzzyMultiClassifierContext) {
   		ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
        System.out.println("FuzzyMultiClassifierContext");
 //  		System.out.println("Classes=" + ((FuzzyMultiClassifierContext)this.state.context).getClasses());
        
       	FuzzyMultiClassifierContext fmcc = (FuzzyMultiClassifierContext)(this.state.context);
        int count[] = new int[fmcc.getClassesCount()];
        
        for(Concept<String, FullObject<String, String>> c : conceptLattice) {
    		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
        	FuzzyMultiClassedConcept fcc = new FuzzyMultiClassedConcept(c);
        	for(int i=0;i<count.length;i++)
        		count[i]=0;

        	for(FullObject<String, String> obj : fcc.getExtent()) {
        		Set<String> clazzSet = fmcc.getTrainingSet().get(obj.getIdentifier());
        		int i=0;
            	for(String clazz: clazzSet) {
        			if(clazz != null) {
        				count[i]++;
        			if(countMap.containsKey(clazz))
        				countMap.put(clazz, countMap.get(clazz)+1);
        			else
        				countMap.put(clazz,1);
        			}
        		}
            	i++;
        	}
   
        	int i=0;
        	for(Set<String> clazzSet : fmcc.getClasses()) {
        		for(String clazz: clazzSet) {
        			if(countMap.containsKey(clazz)) {
        				fcc.addProb(i,countMap.get(clazz)*1.0/count[i]); //fcc.getExtent().size());
  //      			System.out.println(fcc.getExtent().size() + " " + count + " " + countMap.get(clazz));
        			
        		}
        		else {
        			fcc.addProb(i,0.0);
 //       			System.out.println(fcc.getExtent().size() + " 0.0");
        		}
        		}
        		i++;
        	}
        	//System.out.println(fcc.getExtent() +" " +  fcc.getProbsList());
        //	System.out.println(fcc.getClass());
        	fuzzyLattice.add(fcc);	
        	
        }
    	conceptLattice = fuzzyLattice;
      }
   		else if(this.state.context instanceof FuzzyClassifierContext) {
       		ListSet<Concept<String, FullObject<String, String>>> fuzzyLattice = new ListSet<Concept<String, FullObject<String, String>>>();
            System.out.println("FuzzyClassifierContext");
//       		System.out.println("Classes=" + ((FuzzyClassifierContext)this.state.context).getClasses());
            
       		for(Concept<String, FullObject<String, String>> c : conceptLattice) {
        		HashMap<String,Integer> countMap = new HashMap<String,Integer> ();
            	FuzzyClassedConcept fcc = new FuzzyClassedConcept(c);
            	int count=0;
            	for(FullObject<String, String> obj : fcc.getExtent()) {
            		String clazz = ((FuzzyClassifierContext)this.state.context).getClassMap().get(obj.getIdentifier());
            		if(clazz != null) {
            		count++;
            		if(countMap.containsKey(clazz))
            			countMap.put(clazz, countMap.get(clazz)+1);
            		else
            			countMap.put(clazz,1);
            		}
            	}
       
            	for(String clazz : ((FuzzyClassifierContext)this.state.context).getClasses()) {
            		if(countMap.containsKey(clazz)) {
            			fcc.addProb(countMap.get(clazz)*1.0/count); //fcc.getExtent().size());
      //      			System.out.println(fcc.getExtent().size() + " " + count + " " + countMap.get(clazz));
            			
            		}
            		else {
            			fcc.addProb(0.0);
     //       			System.out.println(fcc.getExtent().size() + " 0.0");
            		}

            	}
 //           	System.out.println(fcc.getExtent() +" " +  fcc.getProb());
            	
            	fuzzyLattice.add(fcc);	
            }
    	conceptLattice = fuzzyLattice;
    }     
        printConcepts(conceptLattice);
    	return null;
    }

    public static void printConcepts(ListSet<Concept<String, FullObject<String, String>>> cLattice) {
    	
    	int i=1;
    	for(Concept<String, FullObject<String, String>> c : cLattice) {
		// concept (A,B)
		// A is set of objects which is extent
		// B is set of attributes which is intent
			StringBuffer sb = new StringBuffer();
			sb.append(i + ": <{");
			for(FullObject<String, String> o : c.getExtent()) 
				sb.append(o.getIdentifier() + ",");//  "[" + o.getDescription().getAttributes() + "]" + ",");
			sb.append("},{");
			for(String attr : c.getIntent())
				sb.append(attr + ",");
			sb.append("}>");
			System.out.println(sb + "[" + ((LatticeConcept)c).getId() + "]");
			i++;
    	}
	}
    
    /*
     * executed in EDT so no computations here.
     */
    @Override
    protected void done() {
    	
        state.endCalculation(StatusMessage.CALCULATINGCONCEPTS);
        // if not cancelled finish the operation
        if (!isCancelled()) {
            state.concepts = conceptLattice;
 //           System.out.println(state.concepts.toString());
            if (lattice) {
                state.startCalculation(StatusMessage.CALCULATINGLATTICE);
                setProgress(99);
                setProgressBarMessage(StatusMessage.CALCULATINGLATTICE.toString());
            }
            System.out.println("calling from done");
            state.lattice = LatticeGraphComputer.computeLatticeGraph(state.concepts, view.getLatticeGraphViewBounds());
            view.updateLatticeGraph();
            if (lattice) {
                ((LatticeSettings) view.getSettings()).update(state);
                setProgress(100);
                state.endCalculation(StatusMessage.CALCULATINGLATTICE);
            }
        }
        super.done();
    }
}
