package fcatools.conexpng.model;

public class FuzzyObject<T1, T2> {
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
}
