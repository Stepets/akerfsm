package pt.fsm;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class MonitoredFSM<STATES extends Enum<?>, EVENT> extends
		FSM<STATES, EVENT> {

	// {{ Properties

	private final Map<Monitor, EnumSet<FsmEvents>> monitors = new HashMap<Monitor, EnumSet<FsmEvents>>(
			1);

	public void addMonitor(final Monitor monitor,
			final EnumSet<FsmEvents> events) {
		monitors.put(monitor, events);
	}

	public void addMonitor(final Monitor monitor, final FSM.FsmEvents... events) {
		final EnumSet<FsmEvents> eset = EnumSet.noneOf(FSM.FsmEvents.class);
		for (final FsmEvents event : events) {
			eset.add(event);
		}
		addMonitor(monitor, eset);
	}

	public void removeMonitor(final Monitor monitor) {
		monitors.remove(monitor);
	}

	// }}

	public MonitoredFSM(final State<STATES, EVENT>... statesArray) {
		super(statesArray);
	}

	@Override
	protected void onFSMEvent(final FsmEvents e,
			final State<STATES, EVENT> state) {
		if (monitors != null)
			for (final Monitor monitor : monitors.keySet()) {
				final EnumSet<FsmEvents> events = monitors.get(monitor);
				if (events.contains(e)) {
					monitor.callback(this, e, state);
				}
			}
	}

	public interface Monitor {
		@SuppressWarnings("unchecked")
		public void callback(final MonitoredFSM fsm, final FsmEvents e,
				final State state);
	}
}
