package ar.noxit.paralelleditor.eclipse.model;

public interface IModel<T> {

	public static interface IModelListener {
		void onUpdate();
	}

	T get();

	void set(final T object);

	void addNewListener(IModelListener listener);
}
