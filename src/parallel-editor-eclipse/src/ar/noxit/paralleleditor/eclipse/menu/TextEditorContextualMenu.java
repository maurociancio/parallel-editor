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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

import ar.noxit.paralleleditor.eclipse.Activator;
import ar.noxit.paralleleditor.eclipse.editor.TextEditorProvider;
import ar.noxit.paralleleditor.eclipse.share.ShareManager;
import ar.noxit.paralleleditor.eclipse.share.sync.ShareDocumentIntent;

public class TextEditorContextualMenu extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		MenuManager menuPE = new MenuManager("Share thru PE");

		menuPE.add(new ActionContributionItem(getShareDocumentAction()));
		menuPE.add(new ActionContributionItem(getConnectToServerAction()));

		return new IContributionItem[] { new Separator(), menuPE, new Separator() };
	}

	protected IAction getConnectToServerAction() {
		return new ConnectToServerAction();
	}

	protected IAction getShareDocumentAction() {
		ShareManager shareManager = getShareManager();
		return new ShareDocumentAction(new TextEditorProvider(), new ShareDocumentIntent(shareManager));
	}

	protected ShareManager getShareManager() {
		return Activator.shareManager;
	}
}
