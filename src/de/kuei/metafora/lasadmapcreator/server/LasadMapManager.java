package de.kuei.metafora.lasadmapcreator.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.kuei.metafora.lasadmapcreator.server.xml.Classification;
import de.kuei.metafora.lasadmapcreator.server.xml.CommonFormatCreator;
import de.kuei.metafora.lasadmapcreator.server.xml.Role;
import de.kuei.metafora.lasadmapcreator.server.xml.XMLException;

public class LasadMapManager {

	private static Map<String, LasadMapManager> lasadMaps = Collections
			.synchronizedMap(new HashMap<String, LasadMapManager>());

	public static boolean istKnownLasadMap(String aLasadMapName) {
		if ((lasadMaps != null) && (lasadMaps.containsKey(aLasadMapName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the only existing instance of Manager.
	 * 
	 * @param aLasadMapName
	 *            name of the LASAD map
	 * @return Manager
	 */
	public static LasadMapManager getInstance(String aLasadMapName) {
		if (lasadMaps.get(aLasadMapName) == null) {
			lasadMaps.put(aLasadMapName, new LasadMapManager());
		}
		return lasadMaps.get(aLasadMapName);
	}

	private boolean failed = false;
	private boolean exists = false;

	private String lasadMapId = null;

	private String token;
	private Vector<String> users;
	private String groupId;
	private String challengeId;
	private String challengeName;
	private String ptNodeId;
	private String ptMap;
	private String lasadMapname;

	private LasadMapManager() {

	}

	/**
	 * Sends LASAD the CREATE_MAP command.
	 * 
	 * @param aToken
	 *            token of instantiating client
	 * @param aUserName
	 *            username of instantiating user
	 * @param aMd5Password
	 *            password
	 * @param aGroupId
	 *            groupId of instantiating user
	 * @param aChallengeId
	 *            challengeId of instantiating user
	 * @param aChallengeName
	 *            challengeName of instantiating user
	 * @param aPtNodeId
	 *            Planning Tool Node Id of resource card
	 * @param aPtMap
	 *            Planning Tool map which includes resource card
	 * @param aLasadMapname
	 *            Name of LASAD map which shall be created
	 * @param aLasadTemplate
	 *            Template where LASAD map will be stored
	 */
	public void sendCreateMapCommand(String token, Vector<String> users,
			String groupId, String challengeId, String challengeName,
			String ptNodeId, String ptMap, String lasadMapname) {

		System.err.println("LasadMapCreator: send create map command: "
				+ lasadMapname);

		// save data for update command
		this.challengeId = challengeId;
		this.challengeName = challengeName;
		this.groupId = groupId;
		this.lasadMapname = lasadMapname;
		this.ptMap = ptMap;
		this.ptNodeId = ptNodeId;
		this.token = token;
		this.users = users;

		if (!lasadMapname.isEmpty()) {
			// send CREATE_MAP command
			CommonFormatCreator creator;

			try {
				creator = new CommonFormatCreator(System.currentTimeMillis(),
						Classification.create, "CREATE_MAP",
						StartupServlet.logged);
				creator.addContentProperty("SENDING_TOOL",
						StartupServlet.sendingToolForLasad);
				creator.addContentProperty("RECEIVING_TOOL",
						StartupServlet.receivingTool);

				for (String u : users) {
					creator.addUser(u, token, Role.originator);
				}

				creator.setObject("0", "CREATE_MAP_INFO");
				creator.addProperty("MAPNAME", lasadMapname);
				creator.addProperty("CHALLENGE_ID", challengeId);
				creator.addProperty("CHALLENGE_NAME", challengeName);
				creator.addContentProperty("GROUP_ID", groupId);
				creator.addContentProperty("CHALLENGE_ID", challengeId);
				creator.addContentProperty("CHALLENGE_NAME", challengeName);

				if (StartupServlet.commandLasad != null)
					StartupServlet.commandLasad.sendMessage(creator
							.getDocument());
				else
					System.err.println("LasadMapCreator: Lasad command channel is null!");

				// add nodeId
				NodeManager.getInstance().addNodeId(ptNodeId);
			} catch (XMLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends update command to Planning Tool with resource card URL.
	 */
	public void sendUpdateCommand() {
		CommonFormatCreator creator;
		try {
			creator = new CommonFormatCreator(System.currentTimeMillis(),
					Classification.modify, "MODIFY_NODE_URL",
					StartupServlet.logged);

			creator.setObject(ptNodeId, "PLANNING_TOOL_NODE");

			try {
				creator.addProperty("RESOURCE_URL", StartupServlet.lasad
						+ "?autologin=true&isStandAlone=false&mapId="
						+ URLEncoder.encode(lasadMapId, "UTF8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			creator.addProperty("PLANNING_TOOL_MAP", ptMap);
			creator.addContentProperty("SENDING_TOOL",
					StartupServlet.sendingTool);
			creator.addContentProperty("RECEIVING_TOOL",
					StartupServlet.planningTool);

			for (String u : users) {
				creator.addUser(u, token, Role.originator);
			}

			creator.addContentProperty("GROUP_ID", groupId);
			creator.addContentProperty("CHALLENGE_ID", challengeId);
			creator.addContentProperty("CHALLENGE_NAME", challengeName);

			if (StartupServlet.commandPlanningTool != null)
				StartupServlet.commandPlanningTool.sendMessage(creator
						.getDocument());
		} catch (XMLException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		lasadMaps.remove(lasadMapname);
		NodeManager.getInstance().removeNodeId(ptNodeId);
	}

	/**
	 * Sets the LASAD map id.
	 * 
	 * @param aLasadMapId
	 */
	public void setLasadMapid(String aLasadMapId) {
		this.lasadMapId = aLasadMapId;
	}

	/**
	 * Returns the LASAD map id.
	 * 
	 * @return lasadMapId
	 */
	public String getLasadMapid() {
		return this.lasadMapId;
	}

	/**
	 * Returns if creating LASAD map failed.
	 * 
	 * @return true (creating map failed) / false (succeeded)
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * Sets if creating LASAD map failed.
	 * 
	 * @param failed
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	/**
	 * Returns if LASAD map already exists in LASAD.
	 * 
	 * @return true (map exists) / false (does not exist, new)
	 */
	public boolean doesAlreadyExist() {
		return exists;
	}

	/**
	 * Sets if LASAD map already exists.
	 * 
	 * @param exists
	 */
	public void setDoesAlreadyExist(boolean exists) {
		this.exists = exists;
	}
}
