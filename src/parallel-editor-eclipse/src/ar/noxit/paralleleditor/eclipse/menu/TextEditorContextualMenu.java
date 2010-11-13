package ar.noxit.paralleleditor.eclipse.menu;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.ShareDocumentIntent;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;
import ar.noxit.paralleleditor.eclipse.infrastructure.texteditor.TextEditorProvider;
import ar.noxit.paralleleditor.eclipse.menu.actions.ConnectToServerAction;
import ar.noxit.paralleleditor.eclipse.menu.actions.ShareDocumentAction;

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
		ShareManager shareManager = new ShareManager();
		return shareManager;
	}
}