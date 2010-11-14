package ar.noxit.paralleleditor.eclipse;

import org.easymock.EasyMock;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.AbstractShareDocumentIntent;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IShareManager;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.ITextFileManager;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;
import ar.noxit.paralleleditor.eclipse.menu.actions.Document;

@Test
public class ShareDocumentIntentTest {

	private int count;

	@BeforeMethod
	public void before() {
		count = 0;
	}

	@Test
	public void testShareDocument() throws Exception {
		Path path = new Path("/");

		final ITextFileManager fileBuffer = EasyMock.createMock(ITextFileManager.class);
		fileBuffer.connect(EasyMock.eq(path), EasyMock.eq(LocationKind.IFILE), (IProgressMonitor) EasyMock.anyObject());
		EasyMock.replay(fileBuffer);

		IShareManager shareManager = EasyMock.createMock(IShareManager.class);
		shareManager.createShare("/", "initial");
		EasyMock.replay(shareManager);

		AbstractShareDocumentIntent shareDocumentIntent = new AbstractShareDocumentIntent(shareManager) {
			@Override
			protected ITextFileManager getTextFileBufferManager() {
				return fileBuffer;
			}

			@Override
			protected String getContentFor(IDocument document) {
				return "initial";
			}
		};

		shareDocumentIntent.shareDocument(new Document(path, LocationKind.IFILE));
		EasyMock.verify(fileBuffer, shareManager);
	}

	@Test
	public void testShareDocumentWithException() throws Exception {
		Path path = new Path("/");

		final ITextFileManager fileBuffer = EasyMock.createMock(ITextFileManager.class);
		fileBuffer.connect(EasyMock.eq(path), EasyMock.eq(LocationKind.IFILE), (IProgressMonitor) EasyMock.anyObject());
		EasyMock.expectLastCall().andThrow(new CoreException(new Status(0, "data", "data")));
		EasyMock.replay(fileBuffer);

		IShareManager shareManager = EasyMock.createMock(IShareManager.class);
		EasyMock.replay(shareManager);

		AbstractShareDocumentIntent shareDocumentIntent = new AbstractShareDocumentIntent(shareManager) {
			@Override
			protected ITextFileManager getTextFileBufferManager() {
				return fileBuffer;
			}

			@Override
			protected void onException(CoreException e) {
				count = count + 1;
			}

			@Override
			protected String getContentFor(IDocument document) {
				throw new UnsupportedOperationException();
			}
		};

		shareDocumentIntent.shareDocument(new Document(path, LocationKind.IFILE));
		EasyMock.verify(fileBuffer, shareManager);
		Assert.assertEquals(1, count);
	}
}
