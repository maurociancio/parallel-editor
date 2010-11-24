package ar.noxit.paralleleditor.eclipse.views.share.callbacks;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;

import scala.collection.Iterator;
import scala.collection.immutable.List;
import ar.noxit.paralleleditor.eclipse.locator.IModel;
import ar.noxit.paralleleditor.eclipse.share.ISession.IDocumentListCallback;

public class DocumentListCallback implements IDocumentListCallback {

	private IModel<java.util.List<String>> docsModel;

	public DocumentListCallback(IModel<java.util.List<String>> docsModel) {
		this.docsModel = docsModel;
	}

	@Override
	public void onDocumentListResponse(final List<String> docs) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				docsModel.set(convertFromScala(docs));
			}

			protected java.util.List<String> convertFromScala(final List<String> docs) {
				java.util.List<String> result = new ArrayList<String>();

				@SuppressWarnings("unchecked")
				Iterator<String> it = docs.iterator();
				while (it.hasNext()) {
					result.add(it.next());
				}
				return result;
			}
		});
	}
}
