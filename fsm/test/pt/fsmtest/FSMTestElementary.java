package pt.fsmtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.fsm.FSM;
import pt.fsm.State;
import pt.fsm.SuperState;

public class FSMTestElementary {
	private FSM<A, String> a;

	private enum A {
		ONE, TWO
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> FSM<A, E> createA0() {
		return new FSM<A, E>(

		new State<A, E>
		(A.ONE) {
			public void enter()       { log("enter1"); }
			public void handleEvent() { log("handle1");
                                        if( e().equals("E1") )      next(A.TWO); }
			public void exit()        { log("exit1"); }
		}, 
		new State<A, E>
		(A.TWO) {
			public void enter()       { log("enter2"); }
			public void handleEvent() { log("handle2");
			                            if( e().equals("E1") )      next(A.ONE); }
			public void exit()        { log("exit2"); }
		},
		new SuperState<A, E>
		(A.ONE, A.TWO)
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
		Assert.assertEquals(a.getState(A.ONE), a.getCurrentState());
		Assert.assertNull(a.getEvent());
		Assert.assertNull(a.getPrevState());
		Assert.assertEquals(a.getCurrentState().toString(), a.toString());
		Assert.assertEquals(A.ONE, a.y());
		
		a.setEvent("E1");
		Assert.assertEquals("E1", a.getEvent());
		a.handleEvent();
		Assert.assertEquals(a.getState(A.TWO), a.getCurrentState());
		Assert.assertNull(a.getEvent());
		Assert.assertEquals(a.getState(A.ONE), a.getPrevState());
		
		Assert.assertEquals(a, a.getCurrentState().getFsm());
		Assert.assertTrue(a.getCurrentState().getSuperStates().get(0).toString().contains("ONE"));
		Assert.assertTrue(a.getCurrentState().getSuperStates().get(0).toString().contains("TWO"));
		Assert.assertTrue(a.getCurrentState().getSuperStates().get(0).getIncluded().contains(A.ONE));
		Assert.assertTrue(a.getCurrentState().getSuperStates().get(0).getIncluded().contains(A.TWO));
		Assert.assertEquals("TWO", a.getCurrentState().getId().name());
		Assert.assertEquals(A.TWO, a.y());
	}
	
	private void log(final String msg) {
		if (log != null && log.length() > 0)
			log += "-";
		log += msg;
	}
}
