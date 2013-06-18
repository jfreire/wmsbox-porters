package com.wmsbox.porters.overseer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OverseerServlet extends HttpServlet {

	private static final long serialVersionUID = 1646111608667964307L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute("operations", OverseerServer.INSTANCE.operations());
		request.getRequestDispatcher("/WEB-INF/overseer.jsp").forward(request, response);
	}
}
