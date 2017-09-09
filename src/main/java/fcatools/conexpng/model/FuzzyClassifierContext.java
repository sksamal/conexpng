package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalAttributeException;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcaapi.utils.IndexedSet;
import de.tudresden.inf.tcs.fcalib.FullObject;

/**
 * A specialization of FormalContext<String,String> with the aim to remove the
 * verbose repetition of <String,String>. Plus, adds a couple of useful methods.
 * Due to the API of FormalContext<String,String> the here implemented methods
 * are extremely inefficient.
 */
public class FuzzyClassifierContext extends FuzzyFormalContext {

	// Extra wrapper sets and maps to hold objects and its classes
	protected HashMap<String, String> classMap;

	public boolean addObject(String object, String classifier, String attributes[], double[] values) throws IllegalObjectException {
		if(super.addObject(object, attributes,values))
			classMap.put(object, classifier);
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, String classifier, double value) throws IllegalObjectException {
		if(super.addObject(o,value))
			classMap.put(o.getIdentifier(), classifier);
		else return false;
		return true;
	}

	public boolean addObject(FullObject<String, String> o, String classifier) throws IllegalObjectException {
		if(super.addObject(o))
			classMap.put(o.getIdentifier(), classifier);
		else return false;
		return true;
	}

	@Override
	public boolean removeObject(String id) throws IllegalObjectException {
		if(removeObject(getObject(id)))
			classMap.remove(id);
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
	}

	public FuzzyClassifierContext(double threshold) {
		super(threshold);
		this.classMap = new HashMap<String,String>();
	}

	public FuzzyClassifierContext(int objectsCount, int attributesCount) {
		super(objectsCount, attributesCount);
		this.classMap = new HashMap<String,String>();
	}

	public FuzzyClassifierContext(int objectsCount, int attributesCount, double threshold) {
		super(objectsCount, attributesCount, threshold);
		this.classMap = new HashMap<String,String>();
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

}
