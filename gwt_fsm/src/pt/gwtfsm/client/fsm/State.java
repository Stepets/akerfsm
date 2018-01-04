package pt.gwtfsm.client.fsm;

import java.util.ArrayList;
import java.util.List;

public class State<STATES extends Enum<?>, EVENT> implements IState {

	// {{ Properties

	private STATES id;

	public STATES getId() {
		return id;
	}

	protected void setId(final STATES id) {
		this.id = id;
	}

	private STATES nextState;

	protected STATES getNextState() {
		return nextState;
	}

	protected void setNextState(final STATES nextStateId) {
		nextState = nextStateId;
	}

	protected void clearNextState() {
		setNextState(null);
	}

	// }}

	// {{ Dependencies

	protected void validate() {
		assert (fsm != null);
	}

	private FSM<STATES, EVENT> fsm;

	public FSM<STATES, EVENT> getFsm() {
		return fsm;
	}

	protected void setFsm(final FSM<STATES, EVENT> fsm) {
		this.fsm = fsm;
	}

	private List<SuperState<STATES, EVENT>> superStates;

	public List<SuperState<STATES, EVENT>> getSuperStates() {
		return superStates;
	}

	protected void addSuperState(SuperState<STATES, EVENT> superState) {
		if (superStates == null)
			superStates = new ArrayList<SuperState<STATES, EVENT>>(1);
		this.superStates.add(superState);
	}

	// }}

	public State(final STATES id) {
		super();
		this.id = id;
	}

	public EVENT e() {
		return getFsm().getEvent();
	}

	public void next(final STATES nextStateId) {
		setNextState(nextStateId);
	}

	protected void handleEventInternal() {
		handleEvent();
	}

	public void handleEvent() {
	}

	protected void exitInternal() {
		exit();
	}

	protected void enterInternal() {
		enter();
	}

	public void exit() {
	}

	public void enter() {
	}

	@Override
	public String toString() {
		return id.name() + "[" + id.ordinal() + "]";
	}
}
