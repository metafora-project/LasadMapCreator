package de.kuei.metafora.lasadmapcreator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInit;
import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInitAsync;

public class DialogTimer extends Timer {

	private final MapInitAsync mapInit = GWT.create(MapInit.class);

	private String mapname = null;
	// object needed to implement i18n through Languages interface
	final static Languages language = GWT.create(Languages.class);

	public void setMapName(String name) {
		this.mapname = name;
	}

	@Override
	public void run() {
		if (mapname != null) {
			mapInit.hasFailed(mapname, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {

				}

				@Override
				public void onSuccess(Boolean result) {
					if (result.booleanValue()) {
						Window.alert(language.CreatingLasadMapFailed());
						CreatingMap.getInstance().hideUi();
						Dialog.getInstance().label.setText(language
								.CreatingLasadMapFailed()
								+ " "
								+ language.EnterAnotherName());
						Dialog.getInstance().showUi();
					}
				}
			});

			mapInit.doesExist(mapname, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						Window.alert(language.MapAlreadyExists());
						cancel();
					}
				}
			});
		}
	}
}
