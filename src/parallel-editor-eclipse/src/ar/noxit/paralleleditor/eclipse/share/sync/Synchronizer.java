/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.eclipse.share.sync;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;

import ar.noxit.paralleleditor.common.ApplyFunction;
import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.EditOperationJupiterSynchronizer;
import ar.noxit.paralleleditor.common.JEditOperationJupiterSynchronizer;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.SendFunction;
import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public class Synchronizer implements IOperationCallback, IRemoteMessageCallback {

	private final JEditOperationJupiterSynchronizer sync = new JEditOperationJupiterSynchronizer(
			new EditOperationJupiterSynchronizer(new BasicXFormStrategy()));

	private final IDocumentSession docSession;
	private final DocumentData documentData;
	private final ITextEditorDisabler disabler;

	private boolean disableFiringEvents = false;

	public Synchronizer(IDocumentSession docSession, DocumentData documentData, ITextEditorDisabler disabler) {
		Assert.isNotNull(docSession);
		Assert.isNotNull(documentData);
		Assert.isNotNull(disabler);

		this.docSession = docSession;
		this.documentData = documentData;
		this.disabler = disabler;
	}

	@Override
	public void apply(EditOperation editOperation) {
		if (!disableFiringEvents) {
			sync.generate(editOperation, new SendFunction() {

				@Override
				public void send(Message<EditOperation> message) {
					docSession.onNewLocalMessage(message);
				}
			});
		}
	}

	@Override
	public void onNewRemoteMessage(final Message<EditOperation> message) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					disabler.disableInput();
					sync.receive(message, new ApplyFunction() {

						@Override
						public void apply(EditOperation editOperation) {
							try {
								disableFiringEvents = true;
								editOperation.executeOn(documentData);
							} finally {
								disableFiringEvents = false;
							}
						}
					});
				} finally {
					disabler.enableInput();
				}
			}
		});
	}
}