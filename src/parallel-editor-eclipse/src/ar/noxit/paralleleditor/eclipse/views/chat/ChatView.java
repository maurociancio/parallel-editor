package ar.noxit.paralleleditor.eclipse.views.chat;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralleleditor.eclipse.Activator;
import ar.noxit.paralleleditor.eclipse.locator.IModel;
import ar.noxit.paralleleditor.eclipse.locator.IModel.IModelListener;
import ar.noxit.paralleleditor.eclipse.locator.Model;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.ISession;
import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.share.ShareManager;
import ar.noxit.paralleleditor.eclipse.views.share.provider.ConnectionInfoLabelProvider;

public class ChatView extends ViewPart {

	private final IModel<List<ConnectionInfo>> hosts = Activator.hostsModel;
	private final ShareManager shareManager = Activator.shareManager;
	private final IModel<String> chatMessage = Model.of("");
	private final IModel<ConnectionInfo> selectedConnection = new Model<ConnectionInfo>();

	private IModelListener hostListener;

	private Text history;
	private ComboViewer server;
	private Button send;
	private Text message;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));

		{
			this.history = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
			GridData historyLayout = new GridData();
			historyLayout.grabExcessHorizontalSpace = true;
			historyLayout.grabExcessVerticalSpace = true;
			historyLayout.horizontalAlignment = GridData.FILL;
			historyLayout.verticalAlignment = GridData.FILL;
			this.history.setLayoutData(historyLayout);

			final Composite target = new Composite(parent, SWT.NONE);
			target.setLayout(new GridLayout(3, false));
			GridData targetLayout = new GridData();
			targetLayout.grabExcessHorizontalSpace = true;
			targetLayout.horizontalAlignment = GridData.FILL;
			target.setLayoutData(targetLayout);
			{
				this.message = new Text(target, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
				message.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						chatMessage.set(message.getText());
					}
				});
				message.addListener(SWT.DefaultSelection, new Listener() {

					@Override
					public void handleEvent(Event event) {
						if (send.isEnabled()) {
							sendChat();
						}
					}
				});
				GridData messageLayout = new GridData();
				messageLayout.grabExcessHorizontalSpace = true;
				messageLayout.horizontalAlignment = GridData.FILL;
				message.setLayoutData(messageLayout);

				this.server = new ComboViewer(target, SWT.DROP_DOWN | SWT.READ_ONLY);
				this.server.setContentProvider(new ArrayContentProvider());
				this.server.setLabelProvider(new ConnectionInfoLabelProvider(false));
				this.server.setFilters(new ViewerFilter[] { new ConnectedViewerFilter() });
				this.server.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						final Combo combo = server.getCombo();
						int selectionIndex = combo.getSelectionIndex();

						if (selectionIndex != -1) {
							IStructuredSelection selection = (IStructuredSelection) server.getSelection();
							selectedConnection.set((ConnectionInfo) selection.getFirstElement());
						} else {
							selectedConnection.set(null);
						}
						enableSendButton();
					}
				});
				this.hosts.addNewListener(this.hostListener = new IModelListener() {

					@Override
					public void onUpdate() {
						populateItems();
						target.layout();
						enableSendButton();
					}
				});
				populateItems();

				this.send = new Button(target, SWT.PUSH);
				send.setText("Send");
				send.setEnabled(false);
				send.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						sendChat();
					}
				});
				chatMessage.addNewListener(new IModelListener() {

					@Override
					public void onUpdate() {
						enableSendButton();
					}
				});
			}
		}

		installChatCallback(new HistoryCallback(history));
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		hosts.removeListener(hostListener);
		installChatCallback(null);
		super.dispose();
	}

	private void enableSendButton() {
		send.setEnabled(enableSend());
	}

	private boolean enableSend() {
		return !chatMessage.get().isEmpty() && this.server.getCombo().getSelectionIndex() != -1;
	}

	private void populateItems() {
		this.server.setInput(hosts.get().toArray());
		this.server.refresh();
	}

	private void installChatCallback(final IChatCallback adapted) {
		Activator.getChatCallback().setAdapted(adapted);
	}

	private void sendChat() {
		ConnectionInfo current = selectedConnection.get();
		ISession session = shareManager.getSession(current.getId());
		if (session != null) {
			final String chat = chatMessage.get();
			session.chat(chat);
			history.append(HistoryCallback.formatLocalChat(new Date(), chat, current));

			message.setText("");
			chatMessage.set("");
			message.setFocus();
		}
	}

	private class ConnectedViewerFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			return shareManager.isConnected(((ConnectionInfo) element).getId());
		}
	}
}
