package com.wmsbox.porters.overseer;

import java.io.IOException;
import java.io.Serializable;
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
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Button;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.InputInteger;
import com.wmsbox.porters.commons.interaction.InputString;
import com.wmsbox.porters.commons.interaction.Message;

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
			PatronRemote patron = server.patron();

			if (navOption != null && navOption.length() > 0) {
				if (navOption.equals("logout")) {
					session.invalidate();
				} else if (navOption.equals("cancel")) {
					if (operation != null) {
						operation.cancelByPorter();
						patron.cancel(operation);
						session.removeAttribute("operation");
					}
				}

				response.sendRedirect(request.getRequestURI());
				return;
			}

			if (patron == null) {
				//TODO
			} else if (operation == null) {
				String operationType = request.getParameter("operationType");

				if (operationType != null) {
					if ("search".equals(operationType)) {
						String code = request.getParameter("code");

						if (code != null) {
							operation = patron.porterRequestOperation(code, ctx);
						}
					} else {
						operation = patron.porterRequestOperation(OperationTypeFormat.INSTANCE
								.parse(operationType), ctx);
					}
				}
			} else {
				String actionKey = request.getParameter("actionKey");
				String input = request.getParameter("input");

				if (input != null && (actionKey == null || actionKey.equals("input"))) {
					Action action = operation.action(request.getParameter("inputKey"));
					Serializable inputValue = null;

					if (action instanceof InputInteger) {
						try {
							inputValue = Integer.parseInt(input);
						} catch (Throwable e) {
							operation.error(new Message("inputIntegerWrong",
									"Numero incorrecto " + input));
						}
					} else {
						inputValue = input;
					}

					if (inputValue != null) {
						operation.reset();
						operation.porterDo(action, inputValue);
						operation = patron.porterIteracts(operation);
					}
				} else if (actionKey != null) {
					Action action = operation.action(actionKey);
					operation.reset();

					if (action instanceof Confirm) {
						String confirm = request.getParameter("confirm");
						operation.porterDo(action, "si".equals(confirm));
					} else {
						operation.porterDo(action, null);
					}

					operation = patron.porterIteracts(operation);
				}
			}

			prepareView(request, operation, patron);
		}

		request.getRequestDispatcher("/WEB-INF/template.jsp").forward(request, response);
	}

	private void prepareView(HttpServletRequest request, Operation operation, PatronRemote patron)
			throws RemoteException {
		request.getSession().setAttribute("operation", operation);

		if (operation != null) {
			if (operation.getError() != null) {
				request.setAttribute("error", operation.getError().getText());
			}

			List<Button> buttons = new ArrayList<Button>();

			for (Action action : operation.getPossibleActions()) {
				if (action instanceof InputString) {
					request.setAttribute("inputLabel", action.getText());
					request.setAttribute("inputKey", action.getKey());
					request.setAttribute("inputDefaultValue", ((InputString) action).getDefaultValue());
				} else if (action instanceof InputInteger) {
					request.setAttribute("inputMode", "integer");
					request.setAttribute("inputLabel", action.getText());
					request.setAttribute("inputKey", action.getKey());
					request.setAttribute("inputDefaultValue", ((InputInteger) action).getDefaultValue());
				} else if (action instanceof Confirm) {
					request.setAttribute("confirmText", action.getText());
					request.setAttribute("confirmKey", action.getKey());
				} else {
					buttons.add((Button) action);
				}
			}

			request.setAttribute("buttons", buttons);
		} else {
			request.setAttribute("operationTypes", patron.getOperationTypes());
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
