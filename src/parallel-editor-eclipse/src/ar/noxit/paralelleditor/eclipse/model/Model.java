package ar.noxit.paralelleditor.eclipse.model;

public class Model<T> implements IModel<T> {

	private T obj;

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
	}
}
