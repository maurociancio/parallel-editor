package ar.noxit.paralleleditor.eclipse.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

abstract public class EditorOpener {

	public static IEditorPart openNewEditor(String title, String content) {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();

		if (page != null) {
			try {
				return IDE.openEditor(page, new StringEditorInput(title, content), IDE.getEditorDescriptor(title)
						.getId(), true);
			} catch (PartInitException e1) {
				throw new RuntimeException(e1);
			}
		}
		return null;
	}

	public static IEditorPart openFileFromWorkspace(String title, String content) {

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = null;

		final String projectName = getProjectNameFromPath(title);
		final String fileRelativePath = getFileRelativePath(title);

		// Obtengo el proyecto que especifica el titulo del archivo remoto
		IProject project = workspaceRoot.getProject(projectName);

		// si el proyecto existe busco el archivo y si existe lo abro
		if (project.exists()) {
			file = project.getFile(fileRelativePath);
			if (file.exists())
				return openEditorFromLocalFileWithSyncCheck(file, content, window);
		}

		// el proyecto no existe o no se encontro el archivo
		// obtengo demas proyectos existentes en el workspace
		List<IProject> projects = Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());

		// obtengo archivos que matchean el titulo del archivo remoto requerido
		Collection<IFile> matchingFiles = new ArrayList<IFile>();
		Iterator<IProject> projectsIterator = projects.iterator();
		while (projectsIterator.hasNext()) {
			IProject currentProject = projectsIterator.next();
			if (currentProject != project) {
				IFile projectFile = currentProject.getFile(new Path(fileRelativePath));
				if (projectFile.exists())
					matchingFiles.add(projectFile);
			}
		}

		// if (searchOthers) {
		if (matchingFiles.size() > 0) {
			FileSelectorDialog fileChooser = new FileSelectorDialog(window.getShell(), matchingFiles);
			fileChooser.open();
			IFile selectedFile = fileChooser.getSelectedFile();
			IEditorPart editor = (selectedFile != null) ? openEditorFromLocalFileWithSyncCheck(selectedFile, content,
					window) : null;
			if (editor != null)
				return editor;
		}

		return openNewEditor(file.getProjectRelativePath().lastSegment(), content);
	}

	private static IEditorPart openEditorFromLocalFile(IFile file, final IWorkbenchWindow window) {
		try {
			final IWorkbenchPage page = window.getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			return page.openEditor(new FileEditorInput(file), desc.getId());
		} catch (PartInitException e) {
			// TODO log here
			return null;
		}
	}

	private static IEditorPart openEditorFromLocalFileWithSyncCheck(IFile file,
			String remoteContent,
			IWorkbenchWindow window) {

		IEditorPart editor = openEditorFromLocalFile(file, window);
		// TODO text editor puede ser null y provocar NPE
		ITextEditor textEditor = (editor instanceof ITextEditor) ? (ITextEditor) editor : null;
		String localContent = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();

		if (localContent.equals(remoteContent))
			return editor;
		else {
			MessageDialog overwriteDialog = new MessageDialog(window.getShell(), "Synchronization error", null,
					"Remote and local file contents are different, update local copy with remote content?", 3,
					new String[] { "Open in new editor", "Overwrite contents" }, 0);
			boolean overwrite = overwriteDialog.open() != 0;

			if (overwrite) {
				textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).set(remoteContent);
				return textEditor;
			} else {
				textEditor.close(false);
				return openNewEditor(file.getProjectRelativePath().lastSegment(), remoteContent);
			}
		}
	}

	private static String getProjectNameFromPath(String title) {
		String[] parts = title.split("/");
		if (parts.length > 1)
			return parts[1];
		else
			return "";
	}

	private static String getFileRelativePath(String title) {
		String[] parts = title.split("/", 3);
		if (parts.length == 3)
			return parts[2];
		else
			return "";
	}

	private EditorOpener() {
	}

}
