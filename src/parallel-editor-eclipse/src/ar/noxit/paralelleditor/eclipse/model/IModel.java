package ar.noxit.paralelleditor.eclipse.model;

public interface IModel<T> {

	T get();
	
	void set(T object);
}
