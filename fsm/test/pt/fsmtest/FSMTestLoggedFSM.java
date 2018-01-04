package pt.fsmtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.fsm.LoggedFSM;
import pt.fsm.State;
import pt.fsm.SuperState;
import pt.fsm.LoggedFSM.LogHandler;

public class FSMTestLoggedFSM {
	private LoggedFSM<A, String> a;

	private enum A {
		ONE, TWO, THREE
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> LoggedFSM<A, E> createA0() {
		return new LoggedFSM<A, E>("A0",

		new State<A, E>(A.ONE) {
			public void handleEvent() {
				next(A.TWO);
			}
		}, new State<A, E>(A.TWO) {
			public void handleEvent() {
				next(A.THREE);
			}
		}, new State<A, E>(A.THREE) {
			public void handleEvent() {
				next(A.ONE);
			}
		}, new SuperState<A, E>(A.ONE, A.TWO), new SuperState<A, E>(A.TWO,
				A.THREE));
	}

	private String log;

	@Before
	public void setUp() throws Exception {
		log = "";
		a = createA0();
	}

	@Test
	public void testFSM() {
		LogHandler logHandler;
		logHandler = new LogHandler() {
			public void callback(final String logMsg) {
				log(logMsg);
			}
		};
		a.addLogHandler(logHandler);
		
		a.setLogFull();
		a.handleEvent("E1");
		Assert.assertEquals("A0[ONE]: BEFORE_HANDLE_EVENT (E1)" 
				+ "-A0[ONE]: BEFORE_STATE_HANDLE_EVENT (SuperState for [ONE, TWO]) (E1)" 
				+ "-A0[ONE]: AFTER_STATE_HANDLE_EVENT (SuperState for [ONE, TWO]) (E1)" 
				+ "-A0[ONE]: BEFORE_STATE_HANDLE_EVENT (ONE) (E1)" 
				+ "-A0[ONE]: AFTER_STATE_HANDLE_EVENT (ONE) (E1)" 
				+ "-A0[ONE]: BEFORE_TRANSITION (ONE->TWO)" 
				+ "-A0[ONE]: BEFORE_STATE_EXIT (ONE)" 
				+ "-A0[ONE]: AFTER_STATE_EXIT (ONE)" 
				+ "-A0[ONE]: BEFORE_SWITCH_STATE (ONE->TWO)" 
				+ "-A0[TWO]: AFTER_SWITCH_STATE (ONE->TWO)"
				+ "-A0[TWO]: BEFORE_STATE_ENTER (SuperState for [TWO, THREE])" 
				+ "-A0[TWO]: AFTER_STATE_ENTER (SuperState for [TWO, THREE])" 
				+ "-A0[TWO]: BEFORE_STATE_ENTER (TWO)" 
				+ "-A0[TWO]: AFTER_STATE_ENTER (TWO)" 
				+ "-A0[TWO]: AFTER_TRANSITION (ONE->TWO)" 
				+ "-A0[TWO]: AFTER_HANDLE_EVENT (E1)"
				, log);
		log = "";

		a.setLogStandard();
		a.handleEvent();
		Assert.assertEquals("A0[TWO]: BEFORE_HANDLE_EVENT" 
				+ "-A0[TWO]: BEFORE_STATE_HANDLE_EVENT (SuperState for [ONE, TWO])" 
				+ "-A0[TWO]: BEFORE_STATE_HANDLE_EVENT (SuperState for [TWO, THREE])" 
				+ "-A0[TWO]: BEFORE_STATE_HANDLE_EVENT (TWO)" 
				+ "-A0[TWO]: BEFORE_STATE_EXIT (TWO)" 
				+ "-A0[TWO]: BEFORE_STATE_EXIT (SuperState for [ONE, TWO])" 
				+ "-A0[THREE]: AFTER_SWITCH_STATE (TWO->THREE)" 
				+ "-A0[THREE]: BEFORE_STATE_ENTER (THREE)", log);
		log = "";
		
		a.setLogTransitions();
		a.handleEvent();
		Assert.assertEquals("A0[ONE]: AFTER_SWITCH_STATE (THREE->ONE)", log);
		log = "";
	}

	private void log(final String msg) {
		if (log != null && log.length() > 0)
			log += "-";
		log += msg;
	}
}
