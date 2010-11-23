package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ChatView extends ViewPart {

	private Text history;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));

		{
			this.history = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
			this.history.setText("historia del chat aca\nhola como lesva");
			GridData historyLayout = new GridData();
			historyLayout.grabExcessHorizontalSpace = true;
			historyLayout.grabExcessVerticalSpace = true;
			historyLayout.horizontalAlignment = GridData.FILL;
			historyLayout.verticalAlignment = GridData.FILL;
			this.history.setLayoutData(historyLayout);

			Composite target = new Composite(parent, SWT.NONE);
			target.setLayout(new GridLayout(3, false));
			GridData targetLayout = new GridData();
			targetLayout.grabExcessHorizontalSpace = true;
			targetLayout.horizontalAlignment = GridData.FILL;
			target.setLayoutData(targetLayout);
			{
				Text message = new Text(target, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
				message.setText("insert the new message here");
				GridData messageLayout = new GridData();
				messageLayout.grabExcessHorizontalSpace = true;
				messageLayout.horizontalAlignment = GridData.FILL;
				message.setLayoutData(messageLayout);

				Combo server = new Combo(target, SWT.DROP_DOWN | SWT.READ_ONLY);

				Button send = new Button(target, SWT.PUSH);
				send.setText("Send");
			}
		}
	}

	@Override
	public void setFocus() {
	}
}
