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
package ar.noxit.paralleleditor.eclipse;

import org.easymock.EasyMock;
import org.eclipse.jface.action.IContributionItem;
import org.testng.Assert;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.eclipse.menu.TextEditorContextualMenu;
import ar.noxit.paralleleditor.eclipse.share.ILocalKernelListener;
import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.share.ShareManager;

@Test
public class TextEditorContextualMenuTest {

	@Test
	public void testGetMenu() {
		SpyTextEditorContextualMenu menu = new SpyTextEditorContextualMenu();
		IContributionItem[] contributionItems = menu.getContributionItems();

		Assert.assertEquals(3, contributionItems.length);
	}

	private class SpyTextEditorContextualMenu extends TextEditorContextualMenu {

		@Override
		public IContributionItem[] getContributionItems() {
			return super.getContributionItems();
		}

		@Override
		protected ShareManager getShareManager() {
			ILocalKernelListener mock = EasyMock.createMock(ILocalKernelListener.class);
			EasyMock.replay(mock);
			IChatCallback callback = EasyMock.createMock(IChatCallback.class);
			EasyMock.replay(callback);
			return new ShareManager(mock, callback);
		}
	}
}
