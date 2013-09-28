package de.kuei.metafora.lasadmapcreator.server;

import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.kuei.metafora.lasadmapcreator.client.serverlink.MapInit;

public class MapInitImpl extends RemoteServiceServlet implements MapInit {

	@Override
	public void sendCreateMapCommand(String token, Vector<String> users,
			String md5Password, String groupId, String challengeId,
			String challengeName, String ptNodeId, String ptMap,
			String lasadMapname, String lasadTemplate) {

		NodeManager.getInstance().setNodeToMap(lasadMapname, ptNodeId);

		LasadMapManager.getInstance(lasadMapname).sendCreateMapCommand(token,
				users, groupId, challengeId, challengeName, ptNodeId, ptMap,
				lasadMapname);
	}

	@Override
	public boolean showDialog(String ptNodeId) {
		boolean contains = false;
		if (!NodeManager.getInstance().containsNode(ptNodeId)) {
			contains = true;
		}
		return contains;
	}

	@Override
	public String[] getMapId(String ptNodeId) {
		String[] answer = new String[4];

		String map = NodeManager.getInstance().getMapName(ptNodeId);
		answer[0] = LasadMapManager.getInstance(map).getLasadMapid();

		answer[1] = "http";
		answer[2] = "adapterrex.hcii.cs.cmu.edu:8090";
		answer[3] = "lasad/";

		if (StartupServlet.lasad != null) {
			if (StartupServlet.lasad.startsWith("https")) {
				answer[1] = "https";
				answer[2] = StartupServlet.lasad.substring(8,
						StartupServlet.lasad.length());
			} else {
				answer[1] = "http";
				answer[2] = StartupServlet.lasad.substring(7,
						StartupServlet.lasad.length());
			}

			int start = answer[2].indexOf('/');
			if (start > 0 && start < answer[2].length() - 1) {
				answer[3] = answer[2].substring(start + 1, answer[2].length());
				answer[2] = answer[2].substring(0, start);
			} else {
				answer[3] = "";
			}
		}

		return answer;
	}

	@Override
	public boolean hasFailed(String lasadMapName) {
		return LasadMapManager.getInstance(lasadMapName).isFailed();
	}

	@Override
	public void removeNodeId(String ptNodeId) {
		NodeManager.getInstance().removeNodeId(ptNodeId);
	}

	@Override
	public boolean doesExist(String lasadMapName) {
		return LasadMapManager.getInstance(lasadMapName).doesAlreadyExist();
	}
}
