package com.wmsbox.porters.overseer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.wmsbox.porters.commons.OverseerRemote;

public class ContextLoaderListener implements ServletContextListener {

	private static final String PORT_PARAM = "overseer-patron-port";

	public void contextDestroyed(ServletContextEvent evt) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent evt) {
		String portString = evt.getServletContext().getInitParameter(PORT_PARAM);

		if (portString == null) {
			throw new IllegalArgumentException("RMI port not defined");
		}

		final OverseerServer server = OverseerServer.INSTANCE;

		try {
			OverseerRemote stub = (OverseerRemote) UnicastRemoteObject.exportObject(server, 8888);
			Registry registry = LocateRegistry.createRegistry(8888);
			registry.rebind(OverseerRemote.REMOTE_REFERENCE_NAME, stub);
		} catch (RemoteException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
