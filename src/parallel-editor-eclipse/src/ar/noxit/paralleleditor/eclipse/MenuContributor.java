package ar.noxit.paralleleditor.eclipse;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

public class MenuContributor extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		MenuManager menuPE = new MenuManager("Share thru PE");
		menuPE.add(new ActionContributionItem(new ShareDocumentAction()));
		menuPE.add(new ActionContributionItem(new ConnectToServerAction()));

		return new IContributionItem[] { new Separator(), menuPE, new Separator() };
	}

	private static final class ShareDocumentAction extends Action {

		@Override
		public void run() {
		}

		@Override
		public String getText() {
			return "Share this Doc";
		}
	}

	private static final class ConnectToServerAction extends Action {

		@Override
		public void run() {
		}

		@Override
		public String getText() {
			return "Connection to Collaboration Server";
		}
	}
}
