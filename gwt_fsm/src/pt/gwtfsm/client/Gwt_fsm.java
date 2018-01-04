package pt.gwtfsm.client;

import pt.gwtfsm.client.fsm.IProgressState;
import pt.gwtfsm.client.fsm.MonitoredFSM;
import pt.gwtfsm.client.fsm.ProgressState;
import pt.gwtfsm.client.fsm.State;
import pt.gwtfsm.client.fsm.SuperState;
import pt.gwtfsm.client.fsm.FSM.FsmEvents;
import pt.gwtfsm.client.fsm.MonitoredFSM.Monitor;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Gwt_fsm implements EntryPoint {
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private MonitoredFSM<A, String> a;

	private enum A {
		INIT, LOADCONFIG, LOADDATA, SHOW, HIDE
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> MonitoredFSM<A, E> createA() {
		return new MonitoredFSM<A, E>(

		new State<A, E>
		(A.INIT) {
			public void enter()       { init();                        next(A.HIDE); }
		}, 
		new ProgressState<A, E>
		(A.LOADCONFIG, "Загрузка конфигурации") {
			public void enter()       { loadConfig(); }
			public void handleEvent() { if( formConfig != null )       next(A.LOADDATA); }
		},
		new ProgressState<A, E>
		(A.LOADDATA, "Загрузка данных") {
			public void enter()       { loadData(); }
			public void handleEvent() { if( e().equals("DataLoaded") ) next(A.SHOW); }
		},
		new State<A, E>
		(A.SHOW) {
			public void enter()       { show(); }
		},
		new State<A, E>
		(A.HIDE) {
			public void enter()       { hide(); }
			public void handleEvent() { if( e().equals("ShowEvent") )  next(A.LOADCONFIG); }
		},
		new SuperState<A, E>
		(A.LOADCONFIG, A.LOADDATA) {
			public void handleEvent() { if( e().equals("RPCFailure") ) next(A.HIDE); }
		},
		new SuperState<A, E>
		(A.LOADCONFIG, A.LOADDATA, A.SHOW) {
			public void handleEvent() { if( e().equals("HideEvent"))   next(A.HIDE); }
		}
		);
	};

	@SuppressWarnings("unchecked")
	public void onModuleLoad() {
		a = createA();
		final Monitor mon = new Monitor() {
			@Override
			public void callback(MonitoredFSM fsm, FsmEvents e, State state) {
				final State newState = fsm.getCurrentState();
				if (newState instanceof IProgressState) {
					final int currentStep = newState.getId().ordinal() + 1;
					final int totalSteps = A.values().length;
					showProgressBar(currentStep, totalSteps,
							((IProgressState) newState).getDescription());
				} else {
					hideProgressBar();
				}
			}
		};

		a.addMonitor(mon, FsmEvents.AFTER_SWITCH_STATE);
	}

	private DialogBox box;
	private TextBox text;

	private void init() {
		box = new DialogBox();
		box.setModal(false);
		box.setText("Form");

		VerticalPanel panel = new VerticalPanel();
		Button closeBtn = new Button("Close");
		text = new TextBox();
		panel.add(new HTML("<b>Form data:</b>"));
		panel.add(text);
		panel.add(closeBtn);
		box.setWidget(panel);

		Button btn1 = new Button("Show form");
		Button btn2 = new Button("Hide form");
		btn1.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				a.handleEvent("ShowEvent");
			}
		});

		ClickHandler closeHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				a.handleEvent("HideEvent");
			}
		};
		btn2.addClickHandler(closeHandler);
		closeBtn.addClickHandler(closeHandler);

		RootPanel.get().add(btn1);
		RootPanel.get().add(btn2);
	}

	private String formConfig;
	private String formData;

	private void loadConfig() {
		greetingService.getConfig("formId", new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				a.handleEvent("RPCFailure");
			}

			@Override
			public void onSuccess(String result) {
				formConfig = result;
				a.handleEvent("");
			}
		});
	}

	private void loadData() {
		greetingService.getData("formId", new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				a.handleEvent("RPCFailure");
			}

			@Override
			public void onSuccess(String result) {
				formData = result;
				a.handleEvent("DataLoaded");
			}
		});
	}

	private void show() {
		text.setValue(formData);
		box.center();
		box.show();
	}

	private void hide() {
		formConfig = null;
		formData = null;
		box.hide();
	}

	DialogBox progress;

	private void showProgressBar(int stage, int totalStages, String msg) {
		msg += " (" + stage + "/" + totalStages + ")";

		if (progress == null) {
			progress = new DialogBox();
			progress.setModal(false);
			progress.setWidth("280");
		}

		progress.setText(msg);
		progress.center();
		progress.show();
	}

	private void hideProgressBar() {
		progress.hide();
	}
}
