package de.kuei.metafora.lasadmapcreator.client.serverlink;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MapInitAsync {
	public void sendCreateMapCommand(String token, Vector<String> users,
			String md5Password, String groupId, String challengeId,
			String challengeName, String ptNodeId, String ptMap,
			String lasadMapname, String lasadTemplate,
			AsyncCallback<Void> callback);

	public void showDialog(String ptNodeId, AsyncCallback<Boolean> callback);

	public void getMapId(String ptNodeId, AsyncCallback<String[]> callback);

	void hasFailed(String lasadMapName, AsyncCallback<Boolean> callback);

	void doesExist(String lasadMapName, AsyncCallback<Boolean> callback);

	public void removeNodeId(String ptNodeId, AsyncCallback<Void> callback);

}
