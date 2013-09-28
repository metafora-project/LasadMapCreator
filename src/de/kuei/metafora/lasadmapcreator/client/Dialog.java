package de.kuei.metafora.lasadmapcreator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInit;
import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInitAsync;
import de.kuei.metafora.lasadmapcreator.client.util.InputFilter;

public class Dialog implements ClickHandler, KeyPressHandler {
	private static Dialog dialog = null;
	private VerticalPanel rootPanel;
	// object needed to implement i18n through Languages interface
	final static Languages language = GWT.create(Languages.class);

	/**
	 * Returns the only existing instance of Dialog.
	 * 
	 * @return Dialog instance
	 */
	public static Dialog getInstance() {
		if (dialog == null) {
			dialog = new Dialog();
		}
		return dialog;
	}

	private TextBox tbMapname = null;
	// private TextBox tbTemplate = null;
	private Button btnCreate = null;
	public Label label = null;
	private CaptionPanel cpan = null;
	private HorizontalPanel userrow = null;
	private FlowPanel row = null;
	private final MapInitAsync mapInit = GWT.create(MapInit.class);

	private DialogTimer timer = null;

	public Dialog() {
		timer = new DialogTimer();
	}

	public void main() {
		rootPanel = new VerticalPanel();
		rootPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		rootPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		rootPanel.setWidth("100%");
		rootPanel.getElement().getStyle().setBackgroundColor("#eaeaea");
		RootPanel.get().add(rootPanel);

		label = new Label(language.EnterMapname());
		label.getElement().getStyle().setMargin(10, Unit.PX);
		label.setHeight("10px");
		rootPanel.add(label);

		userrow = new HorizontalPanel();

		cpan = new CaptionPanel(language.Mapname()+":");
		cpan.getElement().getStyle().setMargin(10, Unit.PX);
		cpan.setHeight("50px");
		tbMapname = new TextBox();
		tbMapname.setText("");
		tbMapname.setTabIndex(1);
		tbMapname.addKeyPressHandler(new InputFilter(tbMapname));
		tbMapname.addKeyPressHandler(this);
		cpan.add(tbMapname);
		userrow.add(cpan);

		/*
		 * cpan = new CaptionPanel("Template");
		 * cpan.getElement().getStyle().setMargin(10, Unit.PX);
		 * cpan.setHeight("50px"); tbTemplate = new TextBox();
		 * tbTemplate.setText("DiscussingMicroworlds-4");
		 * tbTemplate.setEnabled(false); tbTemplate.setTabIndex(2);
		 * cpan.add(tbTemplate); userrow.add(cpan);
		 */
		rootPanel.add(userrow);

		row = new FlowPanel();
		btnCreate = new Button(language.CreateMap());
		btnCreate.setText(language.CreateMap());
		btnCreate.addClickHandler(this);
		btnCreate.getElement().getStyle().setMargin(10, Unit.PX);
		btnCreate.getElement().getStyle().setMarginRight(100, Unit.PX);
		row.add(btnCreate);
		rootPanel.add(row);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(btnCreate)) {
			final String lasadMapname = tbMapname.getText();
			// String lasadTemplate = tbTemplate.getText();
			String lasadTemplate = "DiscussingMicroworlds-4";

			// Creating LASAD map ...
			CreatingMap.getInstance().main();
			hideUi();

			// send create map command to server
			mapInit.sendCreateMapCommand(LasadMapCreator.token,
					LasadMapCreator.users, LasadMapCreator.md5Password,
					LasadMapCreator.groupId, LasadMapCreator.challengeId,
					LasadMapCreator.challengeName, LasadMapCreator.ptNodeId,
					LasadMapCreator.ptMap, lasadMapname, lasadTemplate,
					new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {

						}

						@Override
						public void onFailure(Throwable caught) {
							CreatingMap.getInstance().label.setText(language
									.CouldNotBeCreated());
						}
					});

			timer.setMapName(lasadMapname);
			timer.scheduleRepeating(2000);
		}

	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (((int) event.getCharCode()) == 13
				|| (((int) event.getCharCode()) == 0 && ((int) event
						.getNativeEvent().getKeyCode()) == 13)) {
			if (event.getSource().equals(tbMapname)) {
				final String lasadMapname = tbMapname.getText();
				// String lasadTemplate = tbTemplate.getText();
				String lasadTemplate = "DiscussingMicroworlds-4";

				// Creating LASAD map ...
				CreatingMap.getInstance().main();
				hideUi();

				// send create map command to server
				mapInit.sendCreateMapCommand(LasadMapCreator.token,
						LasadMapCreator.users, LasadMapCreator.md5Password,
						LasadMapCreator.groupId, LasadMapCreator.challengeId,
						LasadMapCreator.challengeName,
						LasadMapCreator.ptNodeId, LasadMapCreator.ptMap,
						lasadMapname, lasadTemplate, new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {

							}

							@Override
							public void onFailure(Throwable caught) {
								CreatingMap.getInstance().label
										.setText(language.CouldNotBeCreated());
							}
						});

				timer.setMapName(lasadMapname);
				timer.scheduleRepeating(2000);
			}
		}
	}

	/**
	 * Hides the graphical user interface.
	 */
	public void hideUi() {
		tbMapname.setVisible(false);
		// tbTemplate.setVisible(false);
		btnCreate.setVisible(false);
		label.setVisible(false);
		cpan.setVisible(false);
		userrow.setVisible(false);
		row.setVisible(false);
	}

	/**
	 * Shows the graphical user interface.
	 */
	public void showUi() {
		tbMapname.setVisible(true);
		// tbTemplate.setVisible(true);
		btnCreate.setVisible(true);
		label.setVisible(true);
		cpan.setVisible(true);
		userrow.setVisible(true);
		row.setVisible(true);
		timer.setMapName(null);
	}
}
