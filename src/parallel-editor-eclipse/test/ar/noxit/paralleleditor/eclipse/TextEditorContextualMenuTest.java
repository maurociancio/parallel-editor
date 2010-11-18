package ar.noxit.paralleleditor.eclipse;

import org.eclipse.jface.action.IContributionItem;
import org.testng.Assert;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;
import ar.noxit.paralleleditor.eclipse.menu.TextEditorContextualMenu;

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
			return new ShareManager();
		}
	}
}
