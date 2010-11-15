package ar.noxit.paralelleditor.eclipse.model;

public interface IModel<T> {

	public static interface IModelListener {
		void onUpdate();
	}

	T get();

	void set(T object);

	void addNewListener(IModelListener listener);
}
