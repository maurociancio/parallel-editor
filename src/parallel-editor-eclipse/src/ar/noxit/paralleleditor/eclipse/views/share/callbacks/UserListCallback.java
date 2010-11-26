/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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