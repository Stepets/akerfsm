package pt.fsm;

import java.util.EnumSet;

public class LoggedFSM<STATES extends Enum<?>, EVENT> extends
		MonitoredFSM<STATES, EVENT> {

	// {{ Properties

	private EnumSet<FSM.FsmEvents> loggedEvents = EnumSet
			.noneOf(FSM.FsmEvents.class);

	public EnumSet<FSM.FsmEvents> getLoggedEvents() {
		return loggedEvents;
	}

	public void setLoggedEvents(final FSM.FsmEvents... loggedEvents) {
		this.loggedEvents.clear();
		for (final FsmEvents event : loggedEvents) {
			this.loggedEvents.add(event);
		}
	}

	public void setLogFull() {
		this.loggedEvents = EnumSet.allOf(FSM.FsmEvents.class);
	}

	public void setLogStandard() {
		setLoggedEvents(FsmEvents.AFTER_SWITCH_STATE,
				FsmEvents.BEFORE_HANDLE_EVENT, FsmEvents.BEFORE_STATE_ENTER,
				FsmEvents.BEFORE_STATE_HANDLE_EVENT,
				FsmEvents.BEFORE_STATE_EXIT);
	}

	public void setLogTransitions() {
		setLoggedEvents(FsmEvents.AFTER_SWITCH_STATE);
	}

	public void setLogCompact() {
		setLoggedEvents(FsmEvents.BEFORE_HANDLE_EVENT, FsmEvents.AFTER_SWITCH_STATE);
	}

	private LogHandler logHandler;

	public void addLogHandler(final LogHandler logHandler) {
		this.logHandler = logHandler;
	}

	public void removeLogHandler(final LogHandler logHandler) {
		this.logHandler = null;
	}

	private StringBuilder logMsg;

	public String getLogMsg() {
		if (logMsg == null)
			return "";
		return logMsg.toString();
	}

	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		if (name != null && name.length() > 0)
			this.name = name;
		else
			this.name = "A";
	}

	// }}

	public LoggedFSM(final State<STATES, EVENT>... statesArray) {
		super(statesArray);
	}

	public LoggedFSM(final String name,
			final State<STATES, EVENT>... statesArray) {
		super(statesArray);
		setName(name);
	}

	@Override
	protected void onFSMEvent(final FsmEvents e,
			final State<STATES, EVENT> state) {
		logMsg = new StringBuilder();
		logMsg.append(getName());
		logMsg.append("[");
		if (getCurrentState() != null)
			logMsg.append(getCurrentState().getId().name());
		else
			logMsg.append("state unknown");
		logMsg.append("]: ");
		if (logHandler != null && loggedEvents.contains(e)) {
			switch (e) {
			case BEFORE_INIT:
			case AFTER_INIT:
				logMsg.append(e.name());
				break;
			case BEFORE_HANDLE_EVENT:
			case AFTER_HANDLE_EVENT:
				logMsg.append(e.name());
				appendEventInfo();
				break;
			case BEFORE_TRANSITION:
			case BEFORE_SWITCH_STATE:
				logMsg.append(e.name());
				appendTransitionInfo(getCurrentState(), getNextState());
				break;
			case AFTER_TRANSITION:
			case AFTER_SWITCH_STATE:
				logMsg.append(e.name());
				appendTransitionInfo(getPrevState(), getCurrentState());
				break;
			case BEFORE_STATE_HANDLE_EVENT:
			case AFTER_STATE_HANDLE_EVENT:
				logMsg.append(e.name());
				appendStateInfo(state);
				appendEventInfo();
				break;
			case BEFORE_STATE_ENTER:
			case AFTER_STATE_ENTER:
			case BEFORE_STATE_EXIT:
			case AFTER_STATE_EXIT:
				logMsg.append(e.name());
				appendStateInfo(state);
				break;
			default:
				logMsg.append("Unknown event");
			}

			logHandler.callback(getLogMsg());
		}

		super.onFSMEvent(e, state);
	}

	private void appendStateInfo(final State<STATES, EVENT> state) {
		logMsg.append(" (");
		if (state.getId() != null)
			logMsg.append(state.getId().name());
		else
			logMsg.append(state.toString());
		logMsg.append(")");
	}

	private void appendTransitionInfo(final State<STATES, EVENT> from,
			final State<STATES, EVENT> to) {
		logMsg.append(" (");
		logMsg.append(from.getId().name());
		logMsg.append("->");
		logMsg.append(to.getId().name());
		logMsg.append(")");
	}

	private void appendEventInfo() {
		if (getEvent() != null) {
			logMsg.append(" (");
			logMsg.append(getEvent().toString());
			logMsg.append(")");
		}
	}

	public interface LogHandler {
		public void callback(final String logMsg);
	}
}
