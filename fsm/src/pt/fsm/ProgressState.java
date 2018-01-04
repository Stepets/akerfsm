package pt.fsm;

public class ProgressState<STATES extends Enum<?>, EVENT> extends
		State<STATES, EVENT> implements IProgressState {

	// {{ Properties

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	// }}

	public ProgressState(final STATES id) {
		super(id);
	}

	public ProgressState(final STATES id, final String description) {
		super(id);
		setDescription(description);
	}
}
