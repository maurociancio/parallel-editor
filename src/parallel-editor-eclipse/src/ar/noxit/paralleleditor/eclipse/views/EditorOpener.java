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
				return openEditorFromLocalFile(file, window);
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

		boolean searchOthers = MessageDialog.openQuestion(window.getShell(), "Unexistant project",
				"The specified project for the file does not exist. Search for file in the rest of active projects?");

		if (searchOthers) {
			if (matchingFiles.size() > 0)
				return promptFileToOpen(window, matchingFiles);
		}

		boolean openInNewEditor = MessageDialog.openQuestion(window.getShell(), "Unexistant File",
				"The file is not present in the local workspace. Open as a new file?");

		if (openInNewEditor)
			return openNewEditor(title, content);
		else
			return null;
	}

	private static IEditorPart promptFileToOpen(final IWorkbenchWindow window, Collection<IFile> matchingFiles) {
		FileSelectorDialog fileChooser = new FileSelectorDialog(window.getShell(), matchingFiles);
		fileChooser.open();
		return openEditorFromLocalFile(fileChooser.getSelectedFile(), window);
	}

	private static IEditorPart openEditorFromLocalFile(IFile file, final IWorkbenchWindow window) {

		final IWorkbenchPage page = window.getActivePage();

		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		try {
			return page.openEditor(new FileEditorInput(file), desc.getId());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
