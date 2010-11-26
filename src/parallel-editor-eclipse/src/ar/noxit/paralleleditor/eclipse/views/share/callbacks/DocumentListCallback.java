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
