package ar.noxit.paralelleditor.eclipse.views;

public interface IModel<T> {

	T get();
	
	void set(T object);
}
