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
package ar.noxit.paralleleditor.eclipse.preferences;

public abstract class PreferenceConstants {

	public static final String DEFAULT_USERNAME = "defaultUsername";
	public static final String LOCAL_SERVICE_PORT = "localServicePort";
	public static final String LOCAL_SERVICE_HOSTNAME = "localServiceHostname";

	public static final String REMOTE_HOST = "remoteHost";
	public static final String REMOTE_USER = "remoteUser";
	public static final String REMOTE_PORT = "remotePort";
	public static final String HOST_COUNT = "hostCount";

	private PreferenceConstants() {
	}
}
