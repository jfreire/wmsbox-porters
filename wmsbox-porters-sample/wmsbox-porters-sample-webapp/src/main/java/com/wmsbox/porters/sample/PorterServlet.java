package com.wmsbox.porters.sample;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.wmsbox.porters.overseer.OverseerService;

public class PorterServlet extends BasicServlet {

	private static final long serialVersionUID = 1646111608667964307L;


	@Override
	protected boolean process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Context ctx = context(request);

		if (ctx != null) {
			HttpSession session = request.getSession();
			Operation operation = (Operation) session.getAttribute("operation");
			OverseerService service = WebListener.SERVICE;

			if (request.getParameter("cancel") != null && operation != null) {
				Boolean confirm = confirm(request);

				if (confirm != null) {
					session.removeAttribute("canceling");

					if (confirm.booleanValue()) {
						service.get().cancelCurrentOperation(ctx.getSessionId());
						session.removeAttribute("operation");
					} else {
						operation.request((Action[]) session.getAttribute("oldActions"));
						session.removeAttribute("oldActions");
					}
				} else {
					session.setAttribute("oldActions", operation.getPossibleActions());
					session.setAttribute("canceling", true);
					operation.reset();
					operation.request(new Confirm("confirm.cancel", "Está seguro de querer cancelar la operación actual?"));
				}

				return false;
			}

			if (request.getParameter("changeStyle") != null) {
				if (session.getAttribute("style").equals("css/mainInditex.css")) {
					session.setAttribute("style", "css/mainInditex2.css");
				} else {
					session.setAttribute("style", "css/mainInditex.css");
				}

				return false;
			}

			if (operation == null) {
				String operationType = request.getParameter("operationType");

				if (operationType != null) {
					operation = service.get().porterRequestOperation(OperationTypeFormat.INSTANCE
							.parse(operationType), ctx);
				} else {
					String code = request.getParameter("code");

					if (code != null) {
						operation = service.get().porterRequestOperation(code, ctx);

						if (operation == null) {
							request.setAttribute("error", "Código inválido " + code);
							request.setAttribute("initValue", code);
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
						operation.porterDo(action, confirm(request).toString());
					} else {
						operation.porterDo(action, null);
					}

					operation = service.get().porterIteracts(operation);

					if (!operation.getState().isLive()) {
						request.setAttribute("endMessage", operation.getMessage());
						operation = null;
					}
				}
			}

			prepareView(request, operation);
		}

		return true;
	}

	private Boolean confirm(HttpServletRequest request) {
		if (request.getParameter("confirm") == null) {
			return null;
		}

		return "SI".equals(request.getParameter("confirm")) ? Boolean.TRUE : Boolean.FALSE;
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
			//request.setAttribute("operationTypes", OverseerServer.INSTANCE.getOperationTypes());
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
					session.setAttribute("style", "css/mainInditex.css");
					log("Porter logged " + porter);
					WebListener.SERVICE.get().login(session.getId(), porter);
					session.setAttribute("porter", porter);
				}

				return null;
			}
		}

		return new Context(session.getId(), porter, Locale.getDefault());
	}
}
