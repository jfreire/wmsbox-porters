package com.wmsbox.porters.overseer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.TaskTypeFormat;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Button;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.InputInteger;
import com.wmsbox.porters.commons.interaction.InputString;

public class OverseerServlet extends HttpServlet {

	private static final long serialVersionUID = 1646111608667964307L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Context ctx = context(request);

		if (ctx != null) {
			HttpSession session = request.getSession();
			Task task = (Task) session.getAttribute("task");
			OverseerServer server = OverseerServer.INSTANCE;
			PatronRemote patron = server.patron();

			if (patron == null) {
				//TODO
			} else if (task == null) {
				String taskType = request.getParameter("taskType");

				if (taskType != null) {
					task = patron.porterRequestTask(TaskTypeFormat.INSTANCE.parse(taskType), ctx);
				} else {
					String code = request.getParameter("code");

					if (code != null) {
						task = patron.porterRequestTask(code, ctx);
					} else {
						request.setAttribute("taskTypes", patron.getTaskTypes());
					}
				}

				prepareView(request, task);
			} else {
				String actionKey = request.getParameter("actionKey");
				log("actionKey " + actionKey);

				if (actionKey != null) {
					if (actionKey.equals("cancel")) {
						task.cancelByPorter();
						patron.cancel(task);
						session.removeAttribute("task");
						request.setAttribute("taskTypes", patron.getTaskTypes());
					} else {
						Action action = task.action(actionKey);
						task.porterDo(action, action instanceof InputString? request.getParameter("input")
								: null);
						task = patron.porterIteracts(task);
						prepareView(request, task);
					}
				}
			}
		}

		request.getRequestDispatcher("/WEB-INF/template.jsp").forward(request, response);
	}

	private void prepareView(HttpServletRequest request, Task task) {
		System.out.println("--------- " + task);
		request.getSession().setAttribute("task", task);

		if (task != null) {
			if (task.getError() != null) {
				request.setAttribute("error", task.getError().getKey());
			}

			List<Button> buttons = new ArrayList<Button>();

			for (Action action : task.getPossibleActions()) {
				if (action instanceof InputString) {
					request.setAttribute("inputLabel", action.getText());
					request.setAttribute("inputDefaultValue", ((InputString) action).getDefaultValue());
				} else if (action instanceof InputInteger) {
					//TODO
				} else if (action instanceof Confirm) {
					//TODO
				} else {
					buttons.add((Button) action);
				}
			}

			request.setAttribute("buttons", buttons);
		}
	}

	private Context context(HttpServletRequest request) {
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

		return new Context(porter, Locale.getDefault());
	}
}
