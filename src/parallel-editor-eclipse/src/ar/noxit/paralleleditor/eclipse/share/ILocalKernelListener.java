package ar.noxit.paralleleditor.eclipse.share;

import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;

public interface ILocalKernelListener {

	void onCreation(ConnectionInfo localInfo);

	void onDestroy();
}
