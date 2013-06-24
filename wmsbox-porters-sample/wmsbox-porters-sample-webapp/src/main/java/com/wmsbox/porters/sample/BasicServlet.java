package com.wmsbox.porters.sample;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class BasicServlet extends HttpServlet {

	private static final long serialVersionUID = 6864794347936219991L;

	private String template;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.template = config.getInitParameter("template");
		
		if (this.template == null) {
			throw new ServletException("Init param template must be configured");
		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("logout") != null) {
			HttpSession session = request.getSession();
			
			if (session != null) {
				session.invalidate();
				response.sendRedirect(request.getRequestURI());
			
				return;
			}
		}
		
		if (process(request, response)) {
			request.getRequestDispatcher(this.template).forward(request, response);
		} else {
			response.sendRedirect(request.getRequestURI());
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return Si invocar jsp o hacer redirect a si mismo.
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract boolean process(HttpServletRequest request, HttpServletResponse response)
			throws IOException;
}
