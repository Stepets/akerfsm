package pt.gwtfsm.client.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSM<STATES extends Enum<?>, EVENT> {

	// {{ Properties

	private EVENT event;

	public EVENT getEvent() {
		return event;
	}

	public void setEvent(final EVENT event) {
		this.event = event;
	}

	private State<STATES, EVENT> currentState;

	public State<STATES, EVENT> getCurrentState() {
		return currentState;
	}

	protected void setCurrentState(final State<STATES, EVENT> currentState) {
		this.currentState = currentState;
	}

	public STATES y() {
		return getCurrentState().getId();
	}

	private State<STATES, EVENT> nextState;

	protected State<STATES, EVENT> getNextState() {
		return nextState;
	}

	protected void setNextState(final State<STATES, EVENT> nextState) {
		this.nextState = nextState;
	}

	private State<STATES, EVENT> prevState;

	public State<STATES, EVENT> getPrevState() {
		return prevState;
	}

	protected void setPrevState(final State<STATES, EVENT> prevState) {
		this.prevState = prevState;
	}

	// }}

	private final Map<STATES, State<STATES, EVENT>> states = new HashMap<STATES, State<STATES, EVENT>>();
	private final List<SuperState<STATES, EVENT>> superStates = new ArrayList<SuperState<STATES, EVENT>>(
			0);

	public FSM(final State<STATES, EVENT>... statesArray) {
		init(statesArray);
		start();
	}

	protected void init(final State<STATES, EVENT>... statesArray) {
		onBeforeInit();
		for (final State<STATES, EVENT> state : statesArray) {
			addState(state);
		}

		for (final SuperState<STATES, EVENT> superState : superStates) {
			for (final STATES stateId : superState.getIncluded()) {
				final State<STATES, EVENT> state = getState(stateId);
				if (state == null) {
					throw new FSMException("Unconfigured state ["
							+ stateId.toString()
							+ "] was included into superstate");
				}
				state.addSuperState(superState);
			}
		}

		onAfterInit();
	}

	protected void addState(final State<STATES, EVENT> state) {
		state.setFsm(this);
		state.validate();

		if (state instanceof ISuperState) {
			assert (!superStates.contains(state));
			superStates.add((SuperState<STATES, EVENT>) state);
		} else if (state instanceof IState) {
			if (getCurrentState() == null)
				setCurrentState(state);
			assert (!states.containsValue(state));
			states.put(state.getId(), state);
		}
	}

	protected void start() {
		enterState();
		doTransition();
	}

	public void handleEvent(final EVENT e) {
		setEvent(e);
		handleEvent();
	}

	public void handleEvent() {
		onBeforeHandleEvent();

		handleState();
		doTransition();

		onAfterHandleEvent();
		setEvent(null);
	}

	protected void doTransition() {
		STATES nextStateId = null;
		while ((nextStateId = pickNextState()) != null) {
			final State<STATES, EVENT> nextState = getState(nextStateId);
			if (nextState == null) {
				throw new FSMException(
						"Error trying to transit into unconfigured state ["
								+ nextStateId.toString() + "]");
			}
			setNextState(nextState);
			changeState();
		}
	}

	protected STATES pickNextState() {
		if (getCurrentState().getSuperStates() != null)
			for (final SuperState<STATES, EVENT> superState : getCurrentState()
					.getSuperStates()) {
				final STATES nextStateFromSuper = superState.getNextState();
				if (nextStateFromSuper != null)
					return nextStateFromSuper;
			}

		return getCurrentState().getNextState();
	}

	protected void changeState() {
		onBeforeTransition();

		exitState();

		onBeforeSwitchState();
		setPrevState(getCurrentState());
		setCurrentState(getNextState());
		onAfterSwitchState();
		
		enterState();
		setNextState(null);

		onAfterTransition();
	}

	protected void enterState() {
		if (getCurrentState().getSuperStates() != null)
			for (final SuperState<STATES, EVENT> superState : getCurrentState()
					.getSuperStates()) {
				if (getPrevState() == null
						|| !superState.getIncluded().contains(
								getPrevState().getId())) {
					onBeforeStateEnter(superState);
					superState.enterInternal();
					onAfterStateEnter(superState);
				}
			}
		onBeforeStateEnter(getCurrentState());
		getCurrentState().enterInternal();
		onAfterStateEnter(getCurrentState());
	}

	protected void handleState() {
		if (getCurrentState().getSuperStates() != null)
			for (final SuperState<STATES, EVENT> superState : getCurrentState()
					.getSuperStates()) {
				onBeforeStateHandleEvent(superState);
				superState.handleEventInternal();
				onAfterStateHandleEvent(superState);
			}
		onBeforeStateHandleEvent(getCurrentState());
		getCurrentState().handleEventInternal();
		onAfterStateHandleEvent(getCurrentState());
	}

	protected void exitState() {
		onBeforeStateExit(getCurrentState());
		getCurrentState().exitInternal();
		onAfterStateExit(getCurrentState());

		getCurrentState().clearNextState();
		if (getCurrentState().getSuperStates() != null)
			for (final SuperState<STATES, EVENT> superState : getCurrentState()
					.getSuperStates()) {
				if (!superState.getIncluded().contains(getNextState().getId())) {
					onBeforeStateExit(superState);
					superState.exitInternal();
					onAfterStateExit(superState);
				}
				superState.clearNextState();
			}
	}

	public State<STATES, EVENT> getState(final STATES stateId) {
		return states.get(stateId);
	}

	@Override
	public String toString() {
		return getCurrentState().toString();
	}

	// {{ Event hooks

	public enum FsmEvents {
		BEFORE_INIT, AFTER_INIT, BEFORE_HANDLE_EVENT, AFTER_HANDLE_EVENT, //
		BEFORE_SWITCH_STATE, AFTER_SWITCH_STATE, //
		BEFORE_TRANSITION, AFTER_TRANSITION, BEFORE_STATE_ENTER, AFTER_STATE_ENTER, //
		BEFORE_STATE_HANDLE_EVENT, AFTER_STATE_HANDLE_EVENT, //
		BEFORE_STATE_EXIT, AFTER_STATE_EXIT
	};

	protected void onFSMEvent(final FsmEvents e, final State<STATES, EVENT> state) {
	}

	protected void onBeforeInit() {
		onFSMEvent(FsmEvents.BEFORE_INIT, null);
	}

	protected void onAfterInit() {
		onFSMEvent(FsmEvents.AFTER_INIT, null);
	}

	protected void onBeforeHandleEvent() {
		onFSMEvent(FsmEvents.BEFORE_HANDLE_EVENT, null);
	}

	protected void onAfterHandleEvent() {
		onFSMEvent(FsmEvents.AFTER_HANDLE_EVENT, null);
	}

	protected void onBeforeTransition() {
		onFSMEvent(FsmEvents.BEFORE_TRANSITION, null);
	}

	protected void onAfterTransition() {
		onFSMEvent(FsmEvents.AFTER_TRANSITION, null);
	}

	protected void onBeforeSwitchState() {
		onFSMEvent(FsmEvents.BEFORE_SWITCH_STATE, null);
	}

	protected void onAfterSwitchState() {
		onFSMEvent(FsmEvents.AFTER_SWITCH_STATE, null);
	}

	protected void onBeforeStateEnter(final State<STATES, EVENT> state) {
		onFSMEvent(FsmEvents.BEFORE_STATE_ENTER, state);
	}

	protected void onAfterStateEnter(final State<STATES, EVENT> state) {
		onFSMEvent(FsmEvents.AFTER_STATE_ENTER, state);
	}

	protected void onBeforeStateHandleEvent(final State<STATES, EVENT> state) {
		onFSMEvent(FsmEvents.BEFORE_STATE_HANDLE_EVENT, state);
	}

	protected void onAfterStateHandleEvent(final State<STATES, EVENT> state) {
		onFSMEvent(FsmEvents.AFTER_STATE_HANDLE_EVENT, state);
	}

	protected void onBeforeStateExit(final State<STATES, EVENT> state) {
		onFSMEvent(FsmEvents.BEFORE_STATE_EXIT, state);
	}

	protected void onAfterStateExit(final State<STATES, EVENT> state) {
		onFSMEvent(FsmEvents.AFTER_STATE_EXIT, state);
	}

	// }}
}
