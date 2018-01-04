package pt.fsmtest;

import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.fsm.FSM;
import pt.fsm.MonitoredFSM;
import pt.fsm.State;
import pt.fsm.SuperState;
import pt.fsm.FSM.FsmEvents;
import pt.fsm.MonitoredFSM.Monitor;

public class FSMTestMonitoredFSM {
	private MonitoredFSM<A, String> a;

	private enum A {
		ONE, TWO, THREE
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> MonitoredFSM<A, E> createA0() {
		return new MonitoredFSM<A, E>(

		new State<A, E>
		(A.ONE) {
			public void handleEvent() { next(A.TWO); }
		}, 
		new State<A, E>
		(A.TWO) {
			public void handleEvent() { next(A.THREE); }
		},
		new State<A, E>
		(A.THREE) {
			public void handleEvent() { next(A.ONE); }
		},
		new SuperState<A, E>
		(A.ONE, A.TWO),
		new SuperState<A, E>
		(A.TWO, A.THREE)
		);
	}

	private String log;

	@Before
	public void setUp() throws Exception {
		log = "";
		a = createA0();
	}

	@Test
	public void testFSM() {
		Monitor mon;
		mon = new Monitor() {
			@SuppressWarnings("unchecked")
			public void callback(final MonitoredFSM fsm, final FsmEvents e,
					final State state) {
				String logMsg = e.name();
				if (state != null) {
					logMsg += "(" + state.toString() + ")";
				}
				log(logMsg);
			}
		};
		a.addMonitor(mon, EnumSet.allOf(FSM.FsmEvents.class));

		a.handleEvent();
		Assert.assertEquals("BEFORE_HANDLE_EVENT"
				+ "-BEFORE_STATE_HANDLE_EVENT(SuperState for [ONE, TWO])"
				+ "-AFTER_STATE_HANDLE_EVENT(SuperState for [ONE, TWO])"
				+ "-BEFORE_STATE_HANDLE_EVENT(ONE[0])"
				+ "-AFTER_STATE_HANDLE_EVENT(ONE[0])"
				+ "-BEFORE_TRANSITION" 
				+ "-BEFORE_STATE_EXIT(ONE[0])"
				+ "-AFTER_STATE_EXIT(ONE[0])"
				+ "-BEFORE_SWITCH_STATE" 
				+ "-AFTER_SWITCH_STATE"
				+ "-BEFORE_STATE_ENTER(SuperState for [TWO, THREE])"
				+ "-AFTER_STATE_ENTER(SuperState for [TWO, THREE])"
				+ "-BEFORE_STATE_ENTER(TWO[1])" 
				+ "-AFTER_STATE_ENTER(TWO[1])"
				+ "-AFTER_TRANSITION" 
				+ "-AFTER_HANDLE_EVENT", log);
		log = "";

		a.removeMonitor(mon);
		a.addMonitor(mon, FSM.FsmEvents.AFTER_TRANSITION,
				FSM.FsmEvents.BEFORE_STATE_ENTER,
				FSM.FsmEvents.BEFORE_STATE_EXIT);
		a.handleEvent();
		Assert.assertEquals("BEFORE_STATE_EXIT(TWO[1])" 
				+ "-BEFORE_STATE_EXIT(SuperState for [ONE, TWO])" 
				+ "-BEFORE_STATE_ENTER(THREE[2])-AFTER_TRANSITION", log);
		log = "";
		a.handleEvent();
		Assert.assertEquals("BEFORE_STATE_EXIT(THREE[2])" 
				+ "-BEFORE_STATE_EXIT(SuperState for [TWO, THREE])" 
				+ "-BEFORE_STATE_ENTER(SuperState for [ONE, TWO])" 
				+ "-BEFORE_STATE_ENTER(ONE[0])-AFTER_TRANSITION", log);
		log = "";
	}

	private void log(final String msg) {
		if (log != null && log.length() > 0)
			log += "-";
		log += msg;
	}
}
