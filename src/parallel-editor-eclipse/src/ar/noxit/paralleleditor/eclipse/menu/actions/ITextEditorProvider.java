package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.ui.texteditor.ITextEditor;

public interface ITextEditorProvider {

	/**
	 * Returns the current text editor, null if none
	 * 
	 * @return
	 */
	ITextEditor getCurrentTextEditor();
}
