package ar.noxit.paralleleditor.eclipse;

import org.easymock.EasyMock;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.AbstractShareDocumentIntent;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.ITextFileManager;

@Test
public class ShareDocumentIntentTest {

	@Test
	public void testShareDocument() throws Exception {
		Path path = new Path("/");

		final ITextFileManager fileBuffer = EasyMock.createMock(ITextFileManager.class);
		fileBuffer.connect(EasyMock.eq(path), EasyMock.eq(LocationKind.IFILE), (IProgressMonitor) EasyMock.anyObject());
		EasyMock.replay(fileBuffer);

		AbstractShareDocumentIntent shareDocumentIntent = new AbstractShareDocumentIntent() {
			@Override
			protected ITextFileManager getTextFileBufferManager() {
				return fileBuffer;
			}
		};

		shareDocumentIntent.shareDocument(path, LocationKind.IFILE);
		EasyMock.verify(fileBuffer);
	}
}
