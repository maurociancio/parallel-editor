package ar.noxit.paralleleditor.eclipse.views.share.callbacks;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;

import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.eclipse.locator.IModel;
import ar.noxit.paralleleditor.eclipse.share.ISession.IUserListCallback;
import ar.noxit.paralleleditor.eclipse.views.share.DocumentElement;

public class UserListCallback implements IUserListCallback {

	private IModel<java.util.List<DocumentElement>> elements;

	public UserListCallback(IModel<java.util.List<DocumentElement>> elements) {
		this.elements = elements;
	}

	@Override
	public void onUserListResponse(final Map<String, List<String>> usernames) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				elements.set(convertFromScala(usernames));
			}
		});
	}

	protected ArrayList<DocumentElement> convertFromScala(final Map<String, List<String>> usernames) {
		ArrayList<DocumentElement> users = new ArrayList<DocumentElement>();

		Iterator<Tuple2<String, List<String>>> userIt = usernames.iterator();
		while (userIt.hasNext()) {
			Tuple2<String, List<String>> user = userIt.next();

			List<String> docs = user._2;
			ArrayList<String> newDocs = new ArrayList<String>();

			@SuppressWarnings("unchecked")
			Iterator<String> it2 = docs.iterator();
			while (it2.hasNext()) {
				newDocs.add(it2.next());
			}

			users.add(new DocumentElement(user._1, newDocs));
		}

		return users;
	}
}