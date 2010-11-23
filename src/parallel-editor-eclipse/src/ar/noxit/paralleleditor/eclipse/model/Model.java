package ar.noxit.paralleleditor.eclipse.model;

import java.util.ArrayList;
import java.util.List;

public class Model<T> implements IModel<T> {

	private T obj;
	private final List<IModelListener> listeners = new ArrayList<IModel.IModelListener>();

	public static <T> Model<T> of(T obj) {
		return new Model<T>(obj);
	}

	public Model() {
	}

	public Model(T obj) {
		super();
		this.obj = obj;
	}

	@Override
	public T get() {
		return obj;
	}

	@Override
	public void set(T object) {
		this.obj = object;
		notifyListeners();
	}

	@Override
	public void addNewListener(IModelListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	protected void notifyListeners() {
		for (IModelListener l : listeners) {
			l.onUpdate();
		}
	}

	@Override
	public void clearListeners() {
		listeners.clear();
	}

	@Override
	public void modelChanged() {
		notifyListeners();
	}
}
