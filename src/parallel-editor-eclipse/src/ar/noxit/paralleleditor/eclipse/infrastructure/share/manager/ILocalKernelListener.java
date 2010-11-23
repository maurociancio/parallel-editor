package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public interface ILocalKernelListener {

	void onCreation(ConnectionInfo localInfo);

	void onDestroy();
}
