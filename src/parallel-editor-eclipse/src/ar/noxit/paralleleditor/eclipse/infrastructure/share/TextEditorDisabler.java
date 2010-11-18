package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

public class TextEditorDisabler implements ITextEditorDisabler {

	private final StyledText textEditor;

	public TextEditorDisabler(ITextEditor textEditor) {
		Assert.isNotNull(textEditor);
		this.textEditor = (StyledText) textEditor.getAdapter(Control.class);
	}

	@Override
	public void enableInput() {
		setEnable(true);
	}

	@Override
	public void disableInput() {
		setEnable(false);
	}

	private void setEnable(final boolean editable) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				textEditor.setEditable(editable);
			}
		});
	}
}
