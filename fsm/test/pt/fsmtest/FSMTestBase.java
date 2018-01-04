package pt.fsmtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.fsm.FSM;
import pt.fsm.State;
import pt.fsm.SuperState;

public class FSMTestBase {
	private FSM<A, String> a;

	private enum A {
		ONE, TWO, THREE, FOUR, FIVE
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> FSM<A, E> createA() {
		return new FSM<A, E>(

		new State<A, E>
		(A.ONE) {
			public void enter()       { log("enter1");              next(A.TWO); }
			public void exit()        { log("exit1"); }
		}, 
		new State<A, E>
		(A.TWO) {
			public void enter()       { log("enter2");              next(A.THREE); }
			public void exit()        { log("exit2"); }
		}, 
		new State<A, E>
		(A.THREE) {
			public void enter()       { log("enter3"); }
			public void handleEvent() { log("handle3"); 
			                            if( e().equals("E1") )      next(A.FOUR); }
			public void exit()        { log("exit3"); }
		}, 
		new State<A, E>
		(A.FOUR) {
			public void enter()       { log("enter4"); }
			public void handleEvent() { log("handle4"); 
                                        if( e().equals("E2") )      next(A.FIVE); }
			public void exit()        { log("exit4"); }
		}, 
		new State<A, E>
		(A.FIVE) {
			public void enter()       { log("enter5"); }
			public void handleEvent() { log("handle5"); 
                                        if( e().equals("E4") )      next(A.ONE); }
			public void exit()        { log("exit5"); }
		},
		new SuperState<A, E>
		(A.TWO, A.FIVE) {
			public void enter()       { log("enterS1"); }
			public void handleEvent() { log("handleS1");  
                                        if( e().equals("E3") )      next(A.ONE); }
			public void exit()        { log("exitS1"); }
		}, 
		new SuperState<A, E>
		(A.THREE, A.FOUR, A.FIVE) {
			public void enter()       { log("enterS2"); }
			public void handleEvent() { log("handleS2"); 
                                        if( e().equals("E5") )      next(A.ONE); }
			public void exit()        { log("exitS2"); }
		} 
		);
	}

	private String log;

	@Before
	public void setUp() throws Exception {
		log = "";
		a = createA();
	}

	@Test
	public void testFSM() {
		String afterFirst = "enter1-exit1-enterS1-enter2-exit2-exitS1-enterS2-enter3";
		Assert.assertEquals(afterFirst, log);
		log = "";

		a.handleEvent("E5");
		Assert.assertEquals("handleS2-handle3-exit3-exitS2-" + afterFirst, log);
		log = "";

		a.handleEvent("E1");
		Assert.assertEquals("handleS2-handle3-exit3-enter4", log);
		log = "";

		a.handleEvent("E2");
		Assert.assertEquals("handleS2-handle4-exit4-enterS1-enter5", log);
		log = "";

		a.handleEvent("E4");
		Assert.assertEquals("handleS1-handleS2-handle5-exit5-exitS1-exitS2-"
				+ afterFirst, log);
		log = "";

		a.handleEvent("E1");
		a.handleEvent("E2");
		log = "";
		a.handleEvent("E3");
		Assert.assertEquals("handleS1-handleS2-handle5-exit5-exitS1-exitS2-"
				+ afterFirst, log);
		log = "";
	}

	private void log(String msg) {
		if (log != null && log.length() > 0)
			log += "-";
		log += msg;
	}
}
