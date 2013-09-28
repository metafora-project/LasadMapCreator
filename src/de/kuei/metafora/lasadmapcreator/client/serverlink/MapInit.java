package de.kuei.metafora.lasadmapcreator.client.serverlink;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("mapinit")
public interface MapInit extends RemoteService {

	public void sendCreateMapCommand(String token, Vector<String> users,
			String md5Password, String groupId, String challengeId,
			String challengeName, String ptNodeId, String ptMap,
			String lasadMapname, String lasadTemplate);

	public String[] getMapId(String ptNodeId);

	public boolean showDialog(String ptNodeId);

	public boolean hasFailed(String lasadMapName);

	public boolean doesExist(String lasadMapName);

	public void removeNodeId(String ptNodeId);

}
