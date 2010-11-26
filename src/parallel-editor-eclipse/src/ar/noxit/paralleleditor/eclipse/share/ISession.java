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
package ar.noxit.paralleleditor.eclipse.share;

import java.util.Date;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;

public interface ISession {

	public static interface IUserListCallback {
		void onUserListResponse(Map<String, List<String>> usernames);
	}

	public static interface IDocumentListCallback {
		void onDocumentListResponse(List<String> docs);
	}

	public static interface ISubscriptionCallback {
		void onSubscriptionResponse(String docTitle,
				String initialContent,
				IDocumentSession documentSession);
	}

	public static interface IChatCallback {
		void onNewChat(Date when,
				ConnectionInfo from,
				String username,
				String message);

		void onNewSubscriber(Date when,
				String username,
				String docTitle);

		void onSubscriberLeft(Date when,
				String username,
				String docTitle);

		void onNewLogin(Date when,
				String username);

		void onNewLogout(Date date,
				String username);
	}

	public static interface IOnLoginFailureCallback {
		void onLoginFailure();
	}

	void chat(String message);

	void installUserListCallback(IUserListCallback callback);

	void requestUserList();

	void installDocumentListCallback(IDocumentListCallback callback);

	void requestDocumentList();

	void subscribe(String docTitle);

	void installSubscriptionResponseCallback(ISubscriptionCallback callback);

	void installChatCallback(IChatCallback chatCallback);

	void installOnLoginFailureCallback(IOnLoginFailureCallback callback);

	void close();
}
