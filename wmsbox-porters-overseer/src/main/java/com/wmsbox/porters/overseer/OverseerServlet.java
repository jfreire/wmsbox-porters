package com.wmsbox.porters.overseer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.TaskTypeFormat;

public class OverseerServlet extends HttpServlet {

	private static final long serialVersionUID = 1646111608667964307L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String porter = validated(request);

		if (porter != null) {
			HttpSession session = request.getSession();
			Task task = (Task) session.getAttribute("task");
			OverseerServer server = OverseerServer.INSTANCE;
			PatronRemote patron = server.patron();

			if (patron == null) {
				//TODO
			} else if (task == null) {
				String taskType = request.getParameter("taskType");

				if (taskType != null) {
					task = patron.porterRequestTask(TaskTypeFormat.INSTANCE.parse(taskType), porter);
				} else {
					String code = request.getParameter("code");

					if (code != null) {
						task = patron.porterRequestTask(code, porter);
					} else {
						request.setAttribute("taskTypes", patron.getTaskTypes());
					}
				}

				session.setAttribute("task", task);
			} else {
				String actionKey = request.getParameter("actionKey");

				if (actionKey != null) {
					task.porterDo(task.action(actionKey), request.getParameter("value"));
					patron.porterIteracts(task);
				}
			}
		}

		request.getRequestDispatcher("/WEB-INF/template.jsp").forward(request, response);
	}

	private String validated(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String porter = null;

		if (session != null) {
			porter = (String) session.getAttribute("porter");

			if (porter == null) {
				//Login
				porter = request.getParameter("porter");
				String password = request.getParameter("password");

				if (porter != null && porter.trim().length() > 0 && password != null
						&& password.trim().length() > 0) {
					//TODO validate
					log("Porter logged " + porter);
					session.setAttribute("porter", porter);
				}
			}
		}

		return porter;
	}
}
