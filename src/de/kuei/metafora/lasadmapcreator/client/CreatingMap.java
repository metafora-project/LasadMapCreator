package de.kuei.metafora.lasadmapcreator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CreatingMap {
	private static CreatingMap creating = null;
	private VerticalPanel rootPanel;
	public Label label;
	// object needed to implement i18n through Languages interface
	final static Languages language = GWT.create(Languages.class);

	/**
	 * Returns the only existing instance of CreatingMap.
	 * 
	 * @return CreatingMap instance
	 */
	public static CreatingMap getInstance() {
		if (creating == null) {
			creating = new CreatingMap();
		}
		return creating;
	}
	
	public void main() {
		rootPanel = new VerticalPanel();
		rootPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		rootPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		rootPanel.setWidth("100%");
		rootPanel.getElement().getStyle().setBackgroundColor("#eaeaea");
		RootPanel.get().add(rootPanel);
		
		label = new Label(language.CreatingLasadMap());
		label.getElement().getStyle().setMargin(10, Unit.PX);
		label.setHeight("50px");
		rootPanel.add(label);
	}
	
	/**
	 * Hides the graphical user interface.
	 */
	public void hideUi(){
		rootPanel.setVisible(false);
		label.setVisible(false);
	}
}
