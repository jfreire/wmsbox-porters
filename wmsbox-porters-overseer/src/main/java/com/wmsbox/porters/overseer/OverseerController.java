package com.wmsbox.porters.overseer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationRequest;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;

public class OverseerController implements OverseerRemote, OverseerFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverseerController.class);

	private final AtomicLong ids = new AtomicLong();
	private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<String, SessionInfo>();
	private final Map<Long, Operation> operations = new ConcurrentHashMap<Long, Operation>();

	private PatronRemote patron = null;

	public void register(PatronRemote patron) throws RemoteException {
		LOGGER.info("Registrado ..... {} ", patron.getKey());
		this.patron = patron;
	}

	public void request(OperationRequest request) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	public Operation createOperation(OperationType type, Context context) throws RemoteException {
		return new Operation(this.ids.getAndIncrement(), type, context);
	}

	public void ping() throws RemoteException {
		// Nada
	}

	public void login(String sessionId, String porter) {
		LOGGER.info("Logged {} {} ", porter, sessionId);

		if (this.sessions.containsKey(sessionId)) {
			cancelCurrentOperation(sessionId);
		}

		this.sessions.put(sessionId, new SessionInfo(sessionId, porter,
				System.currentTimeMillis()));
	}

	public void logout(String sessionId, String porter) {
		LOGGER.info("Logged out {}", porter);
		SessionInfo sessionInfo = this.sessions.remove(sessionId);

		if (sessionInfo != null) {
			cancelCurrentOperation(sessionInfo);
		}
	}

	public List<SessionInfo> findCurrentSessions() {
		return new ArrayList<SessionInfo>(this.sessions.values());
	}

	public void cancelCurrentOperation(String porter) {
		LOGGER.info("Cancel current operation {} ", porter);

		SessionInfo sessionInfo = this.sessions.get(porter);

		if (sessionInfo != null) {
			cancelCurrentOperation(sessionInfo);
		}
	}

	private void cancelCurrentOperation(SessionInfo sessionInfo) {
		Operation operation = sessionInfo.getCurrentOperation();

		if (operation != null) {
			try {
				operation.cancelByPatron(null);
				this.patron.cancel(operation);
			} catch (RemoteException e) {
				LOGGER.error("Error canceling " + operation.getId(), e);
			}

			this.operations.remove(operation.getId());
			sessionInfo.setCurrentOperation(null);
		}
	}

	protected void cancelAll() {
		for (SessionInfo session : this.sessions.values()) {
			cancelCurrentOperation(session);
		}
	}

	public Operation porterIteracts(Operation operation) throws RemoteException {
		LOGGER.info("porterIteracts {} {}", operation.getPorterDo().getKey(),
				operation.getPorderDoValue());

		if (this.patron != null) {
			String sessionId = operation.getContext().getSessionId();
			SessionInfo sessionInfo = this.sessions.get(sessionId);

			if (sessionInfo != null) {
				try {
					if (sessionInfo.checkThatIsTheFirst()) {
					    try {
					        operation = this.patron.porterInteracts(operation);
					    } catch (RemoteException e) {
					        LOGGER.error(e.getMessage(), e);
					        
					        throw e;
					    } finally {
					        sessionInfo.markAsReady();
					    }
					} else {
						operation = sessionInfo.getCurrentOperation();
					}

					if (operation.getPossibleActions() == null && operation.getState().isLive()) {
						LOGGER.debug("Invalid state " + operation);
						operation = null;
					}
				} catch (InterruptedException e) {
					operation = null;

					LOGGER.error(e.getMessage());
				}

				operationChanged(sessionId, operation);
			} else {
				LOGGER.error("Interacts without session {} ", operation);
				operation = null;
			}
		}

		return operation;
	}

	public Operation porterRequestOperation(String code, Context ctx) throws RemoteException {
		LOGGER.info("porterRequestOperation {} ", code);
		Operation operation = null;

		if (this.patron != null) {
			operation = this.patron.porterRequestOperation(code, ctx);
			operationChanged(ctx.getSessionId(), operation);
		}

		return operation;
	}

	public Operation porterRequestOperation(OperationType type, Context ctx) throws RemoteException {
		LOGGER.info("porterRequestOperation {} ", type);
		Operation operation = null;

		if (this.patron != null) {
			operation = this.patron.porterRequestOperation(type, ctx);
			operationChanged(ctx.getSessionId(), operation);
		}

		return operation;
	}

	private void operationChanged(String sessionId, Operation operation) {
		SessionInfo session = this.sessions.get(sessionId);
		Operation lastOp = session.getCurrentOperation();
		LOGGER.debug("OperationChanged " + sessionId + " - " + operation + " - " + lastOp);

		if (lastOp != null) {
			this.operations.remove(lastOp.getId());

			if (lastOp.getState().isLive()
					&& (operation == null || lastOp.getId() != operation.getId())) {
				cancelCurrentOperation(sessionId);
			}
		}

		session.setCurrentOperation(operation);

		if (operation != null) {
			this.operations.put(operation.getId(), operation);
		}
	}

	public void pingPatrons() throws RemoteException {
		if (this.patron != null) {
			this.patron.ping();
		}
	}
}
