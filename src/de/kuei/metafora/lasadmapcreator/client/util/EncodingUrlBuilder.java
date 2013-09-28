package de.kuei.metafora.lasadmapcreator.client.util;

import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;

public class EncodingUrlBuilder extends UrlBuilder {

	@Override
	public UrlBuilder setParameter(String key, String... values) {

		for (int i = 0; i < values.length; i++) {
			values[i] = URL.encode(values[i]);
		}

		return super.setParameter(key, values);
	}

}
