package pt.fsmtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.fsm.FSM;
import pt.fsm.State;

public class FSMTestIncl {
	private FSM<A0, String> a0;

	private enum A0 {
		ONE, TWO, THREE
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> FSM<A0, E> createA0() {
		return new FSM<A0, E>(

		new State<A0, E>
		(A0.ONE) {
			public void enter()       { log("enter1");              next(A0.TWO); }
			public void exit()        { log("exit1"); }
		}, 
		new State<A0, E>
		(A0.TWO) {
			public void enter()       { log("enter2"); }
			public void handleEvent() { a1.handleEvent((String) e()); 
			                            log("handle2");
			                            if( e().equals("E1") )      next(A0.THREE); }
			public void exit()        { log("exit2"); }
		}, 
		new State<A0, E>
		(A0.THREE) {
			public void enter()       { log("enter3"); }
			public void handleEvent() { a1.handleEvent((String) e()); 
				                        log("handle3"); 
			                            if( e().equals("E2") )      next(A0.ONE); }
			public void exit()        { log("exit3"); }
		} 
		);
	}
	
	private FSM<A1, String> a1;

	private enum A1 {
		ONE, TWO
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> FSM<A1, E> createA1() {
		return new FSM<A1, E>(

		new State<A1, E>
		(A1.ONE) {
			public void enter()       { log("enter11"); }
			public void handleEvent() { log("handle11"); 
                                        if( e().equals("E2") )      next(A1.TWO); }
			public void exit()        { log("exit11"); }
		}, 
		new State<A1, E>
		(A1.TWO) {
			public void enter()       { log("enter12"); }
			public void handleEvent() { log("handle12"); 
                                        if( e().equals("E2") )      next(A1.ONE); }
			public void exit()        { log("exit12"); }
		} 
		);
	}

	private String log;

	@Before
	public void setUp() throws Exception {
		log = "";
		a0 = createA0();
		a1 = createA1();
	}

	@Test
	public void testFSM() {
		// "enter11" because of a1 creation
		Assert.assertEquals("enter1-exit1-enter2-enter11", log);
		log = "";

		a0.handleEvent("E2");
		Assert.assertEquals("handle11-exit11-enter12-handle2", log);
		log = "";

		a0.handleEvent("E2");
		Assert.assertEquals("handle12-exit12-enter11-handle2", log);
		log = "";

		a0.handleEvent("E1");
		Assert.assertEquals("handle11-handle2-exit2-enter3", log);
		log = "";

		a0.handleEvent("E2");
		Assert.assertEquals(
				"handle11-exit11-enter12-handle3-exit3-enter1-exit1-enter2",
				log);
		log = "";
	}

	private void log(String msg) {
		if (log != null && log.length() > 0)
			log += "-";
		log += msg;
	}
}
