package de.kuei.metafora.lasadmapcreator.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInit;
import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInitAsync;
import de.kuei.metafora.lasadmapcreator.client.util.EncodingUrlBuilder;
import de.kuei.metafora.lasadmapcreator.client.util.UrlDecoder;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LasadMapCreator implements EntryPoint {

	public static String token = null;
	public static Vector<String> users = null;
	public static String md5Password = null;
	public static String groupId = null;
	public static String challengeId = null;
	public static String challengeName = null;
	public static String ptNodeId = null;
	public static String ptMap = null;

	// object needed to implement i18n through Languages interface
	final static Languages language = GWT.create(Languages.class);

	private final MapInitAsync mapInit = GWT.create(MapInit.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		users = new Vector<String>();
		// read info from URL
		LasadMapCreator.token = UrlDecoder.getParameter("token");

		String user = UrlDecoder.getParameter("user");

		LasadMapCreator.users.add(user);

		Map<String, List<String>> parameters = Window.Location
				.getParameterMap();
		Set<String> keySet = parameters.keySet();
		for (String key : keySet) {
			if (key.startsWith("otherUser")) {
				List<String> values = parameters.get(key);
				for (String u : values) {
					if (u != null)
						LasadMapCreator.users.add(URL.decode(u));
				}
			}
		}

		LasadMapCreator.md5Password = UrlDecoder.getParameter("pw");

		LasadMapCreator.groupId = UrlDecoder.getParameter("groupId");

		LasadMapCreator.challengeId = UrlDecoder.getParameter("challengeId");

		LasadMapCreator.challengeName = UrlDecoder
				.getParameter("challengeName");

		LasadMapCreator.ptNodeId = UrlDecoder.getParameter("ptNodeId");

		LasadMapCreator.ptMap = UrlDecoder.getParameter("ptMap");

		// connecting to LASAD
		Connecting.getInstance().main();

		mapInit.showDialog(ptNodeId, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Connecting.getInstance().hideUi();
					Dialog.getInstance().main();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(language.SomeoneAlreadyCreatesMap());
				Connecting.getInstance().label.setText(language
						.CreatingMapNotPossible());
			}
		});

		Timer t = new Timer() {

			@Override
			public void run() {
				mapInit.getMapId(ptNodeId, new AsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(String[] result) {
						if (result != null && result[0] != null) {
							// create URL
							UrlBuilder lasadUrlBuilder = new EncodingUrlBuilder();
							lasadUrlBuilder.setProtocol(result[1]);
							lasadUrlBuilder.setHost(result[2]);
							lasadUrlBuilder.setPath(result[3]);

							lasadUrlBuilder.setParameter("autologin", "true");
							lasadUrlBuilder.setParameter("user",
									LasadMapCreator.users.get(0));
							lasadUrlBuilder.setParameter("pw",
									LasadMapCreator.md5Password);
							lasadUrlBuilder.setParameter("pwEncrypted", "true");
							lasadUrlBuilder.setParameter("groupId",
									LasadMapCreator.groupId);
							lasadUrlBuilder.setParameter("challengeId",
									LasadMapCreator.challengeId);
							lasadUrlBuilder.setParameter("challengeName",
									LasadMapCreator.challengeName);
							lasadUrlBuilder.setParameter("mapId", result[0]);
							for (int i = 1; i < users.size(); i++) {
								if (!users.get(i).isEmpty())
									lasadUrlBuilder.setParameter("otherUser"
											+ i, users.get(i));
							}

							final String url = lasadUrlBuilder.buildString();

							mapInit.removeNodeId(ptNodeId,
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											// show URL
											Window.Location.replace(url);
										}

										@Override
										public void onSuccess(Void result) {
											// show URL
											Window.Location.replace(url);
										}
									});
						}
					}
				});
			}
		};

		t.scheduleRepeating(2000);
	}
}
