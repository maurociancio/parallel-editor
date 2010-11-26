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
package ar.noxit.paralleleditor.eclipse.menu;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.share.IDocument;

public class Document implements IDocument {

	private final LocationKind locationKind;
	private final IPath fullPath;
	private final ITextEditor textEditor;

	public Document(IPath fullPath, LocationKind locationKind, ITextEditor textEditor) {
		Assert.isNotNull(fullPath);
		Assert.isNotNull(locationKind);
		Assert.isNotNull(textEditor);

		this.fullPath = fullPath;
		this.locationKind = locationKind;
		this.textEditor = textEditor;
	}

	@Override
	public IPath getFullPath() {
		return fullPath;
	}

	@Override
	public LocationKind getLocationKind() {
		return locationKind;
	}

	@Override
	public ITextEditor getTextEditor() {
		return textEditor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullPath == null) ? 0 : fullPath.hashCode());
		result = prime * result + ((locationKind == null) ? 0 : locationKind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (fullPath == null) {
			if (other.fullPath != null)
				return false;
		} else if (!fullPath.equals(other.fullPath))
			return false;
		if (locationKind == null) {
			if (other.locationKind != null)
				return false;
		} else if (!locationKind.equals(other.locationKind))
			return false;
		return true;
	}
}