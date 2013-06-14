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
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationTypeFormat;
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
			Operation operation = (Operation) session.getAttribute("operation");
			OverseerServer server = OverseerServer.INSTANCE;
			PatronRemote patron = server.patron();

			if (patron == null) {
				//TODO
			} else if (operation == null) {
				String operationType = request.getParameter("operationType");

				if (operationType != null) {
					operation = patron.porterRequestOperation(OperationTypeFormat.INSTANCE.parse(operationType), ctx);
				} else {
					String code = request.getParameter("code");

					if (code != null) {
						operation = patron.porterRequestOperation(code, ctx);
					} else {
						request.setAttribute("operationTypes", patron.getOperationTypes());
					}
				}

				prepareView(request, operation);
			} else {
				String actionKey = request.getParameter("actionKey");
				log("actionKey " + actionKey);

				if (actionKey != null) {
					if (actionKey.equals("cancel")) {
						operation.cancelByPorter();
						patron.cancel(operation);
						session.removeAttribute("operation");
						request.setAttribute("operationTypes", patron.getOperationTypes());
					} else {
						Action action = operation.action(actionKey);
						operation.porterDo(action, action instanceof InputString
								? request.getParameter("input")	: null);
						operation = patron.porterIteracts(operation);
						prepareView(request, operation);
					}
				}
			}
		}

		request.getRequestDispatcher("/WEB-INF/template.jsp").forward(request, response);
	}

	private void prepareView(HttpServletRequest request, Operation operation) {
		System.out.println("--------- " + operation);
		request.getSession().setAttribute("operation", operation);

		if (operation != null) {
			if (operation.getError() != null) {
				request.setAttribute("error", operation.getError().getKey());
			}

			List<Button> buttons = new ArrayList<Button>();

			for (Action action : operation.getPossibleActions()) {
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
