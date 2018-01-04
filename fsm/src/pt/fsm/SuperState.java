package pt.fsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperState<STATES extends Enum<?>, EVENT> extends
		State<STATES, EVENT> implements ISuperState {

	// {{ Properties

	private List<STATES> included = new ArrayList<STATES>(2);

	public List<STATES> getIncluded() {
		return included;
	}

	protected void setIncluded(List<STATES> included) {
		this.included = included;
	}

	// }}

	public SuperState(final STATES... includedIds) {
		super(null);
		included.addAll(Arrays.asList(includedIds));
	}

	private String stateName;

	@Override
	public String toString() {
		if (stateName == null) {
			StringBuilder sb = new StringBuilder("SuperState for [");
			boolean firstToken = true;
			for (STATES stateId : included) {
				if (firstToken)
					firstToken = false;
				else
					sb.append(", ");
				sb.append(stateId.name());
			}
			sb.append("]");
			stateName = sb.toString();
		}
		
		return stateName;
	}
}
