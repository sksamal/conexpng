package fcatools.conexpng.model;

public class FuzzyObject<T1, T2> implements java.lang.Comparable<FuzzyObject<T1, T2>>{
	T1 name;
	T2 value;
	public FuzzyObject(T1 name, T2 value) {
		super();
		this.name = name;
		this.value = value;
	}
	public T1 getName() {
		return name;
	}
	public void setName(T1 name) {
		this.name = name;
	}
	public T2 getValue() {
		return value;
	}
	public void setValue(T2 value) {
		this.value = value;
	}
	
	public boolean contains(T1 name) {
		return (this.name==name);
	}

	@Override
	public int compareTo(FuzzyObject<T1, T2> o) {
		
		if(o.getName().toString().compareTo(this.getName().toString())==0)
			return o.getValue().toString().compareTo(this.getValue().toString());	
		return o.getName().toString().compareTo(this.getName().toString());
	}
	@Override
	public String toString() {
		return "FuzzyObject(" + name + "," + value + ")";
	}
}
