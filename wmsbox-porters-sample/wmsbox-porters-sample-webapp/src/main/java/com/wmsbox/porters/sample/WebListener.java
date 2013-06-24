package com.wmsbox.porters.sample;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.wmsbox.porters.overseer.OverseerService;

public class WebListener implements ServletContextListener, HttpSessionListener {

	private static final String PORT_PARAM = "overseer-patron-port";
	private static final String PING_PARAM = "ping-period-in-millis";
	
	public static final OverseerService SERVICE = new OverseerService();

	public void contextDestroyed(ServletContextEvent evt) {
		SERVICE.stop();
	}

	public void contextInitialized(ServletContextEvent evt) {
		String portString = evt.getServletContext().getInitParameter(PORT_PARAM);
		String pingPeriodInMillis = evt.getServletContext().getInitParameter(PING_PARAM);
		
		if (portString == null) {
			throw new IllegalArgumentException("RMI port not defined");
		}

		SERVICE.setPingPeriodInMillis(Integer.parseInt(pingPeriodInMillis));
		SERVICE.setPort(Integer.parseInt(portString));
		
		SERVICE.start();
	}

	public void sessionCreated(HttpSessionEvent evt) {
		// Nada
	}

	public void sessionDestroyed(HttpSessionEvent evt) {
		String porter = (String) evt.getSession().getAttribute("porter");

		if (porter != null) {
			SERVICE.facade().logout(porter);
		}
	}
}
