package ar.noxit.paralleleditor.eclipse.menu;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

import ar.noxit.paralleleditor.eclipse.infrastructure.texteditor.TextEditorProvider;
import ar.noxit.paralleleditor.eclipse.menu.actions.ConnectToServerAction;
import ar.noxit.paralleleditor.eclipse.menu.actions.ShareDocumentAction;

public class MenuContributor extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		MenuManager menuPE = new MenuManager("Share thru PE");

		menuPE.add(new ActionContributionItem(new ShareDocumentAction(new TextEditorProvider())));
		menuPE.add(new ActionContributionItem(new ConnectToServerAction()));

		return new IContributionItem[] { new Separator(), menuPE, new Separator() };
	}
}
