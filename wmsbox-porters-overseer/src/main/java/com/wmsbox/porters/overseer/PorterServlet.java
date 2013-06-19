package com.wmsbox.porters.overseer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationTypeFormat;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Button;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.Input;

public class PorterServlet extends HttpServlet {

	private static final long serialVersionUID = 1646111608667964307L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Context ctx = context(request);

		if (ctx != null) {
			String navOption = request.getParameter("nav");
			HttpSession session = request.getSession();
			Operation operation = (Operation) session.getAttribute("operation");
			OverseerServer server = OverseerServer.INSTANCE;

			if (navOption != null && navOption.length() > 0) {
				if (navOption.equals("logout")) {
					session.invalidate();
				} else if (navOption.equals("cancel")) {
					if (operation != null) {
						OverseerServer.INSTANCE.cancel(operation);
						session.removeAttribute("operation");
					}
				}

				response.sendRedirect(request.getRequestURI());
				return;
			}

			if (operation == null) {
				String operationType = request.getParameter("operationType");
				
				if (operationType != null) {
					operation = server.porterRequestOperation(OperationTypeFormat.INSTANCE
							.parse(operationType), ctx);
				} else {
					String code = request.getParameter("code");

					if (code != null) {
						operation = server.porterRequestOperation(code, ctx);

						if (operation == null) {
							request.setAttribute("error", "Código inválido " + code);
						}
					}
				}
			} else {
				Action action = action(request, operation);

				if (action != null) {
					operation.reset();

					if (action instanceof Input) {
						operation.porterDo(action, request.getParameter("input"));
					} else if (action instanceof Confirm) {
						operation.porterDo(action, "SI".equals(request.getParameter("confirm"))
								? "true" : "false");
					} else {
						operation.porterDo(action, null);
					}

					operation = server.porterIteracts(operation);

					if (!operation.getState().isLive()) {
						request.setAttribute("endMessage", operation.getEndMessage());
						operation = null;
					} else if (operation.getPreviousEndMessage() != null) {
						request.setAttribute("endMessage", operation.getPreviousEndMessage());
						operation.setPreviousEndMessage(null);
					}
				}
			}

			prepareView(request, operation);
		}

		request.getRequestDispatcher("/WEB-INF/porter.jsp").forward(request, response);
	}

	private Action action(HttpServletRequest request, Operation operation) {
		String actionKey = request.getParameter("actionKey");

		if (actionKey == null) {
			actionKey = request.getParameter("inputKey");
		}

		for (Action action : operation.getPossibleActions()) {
			if (action.getText().equals(actionKey) || action.getKey().equals(actionKey)) {
				return action;
			}
		}

		return null;
	}

	private void prepareView(HttpServletRequest request, Operation operation)
			throws RemoteException {
		request.getSession().setAttribute("operation", operation);

		if (operation != null) {
			if (operation.getError() != null) {
				request.setAttribute("error", operation.getError().getText());
			}

			List<Button> buttons = new ArrayList<Button>();

			for (Action action : operation.getPossibleActions()) {
				if (action instanceof Input) {
					request.setAttribute("input", action);
				} else if (action instanceof Confirm) {
					request.setAttribute("confirm", action);
				} else {
					buttons.add((Button) action);
				}
			}

			request.setAttribute("buttons", buttons);
		} else {
			request.setAttribute("operationTypes", OverseerServer.INSTANCE.getOperationTypes());
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
				
				return null;
			}
		}

		return new Context(porter, Locale.getDefault());
	}
}
