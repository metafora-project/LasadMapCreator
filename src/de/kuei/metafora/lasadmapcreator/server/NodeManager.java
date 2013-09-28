package de.kuei.metafora.lasadmapcreator.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class NodeManager {

	private static NodeManager instance = null;
	private Vector<String> nodeIdVector = new Vector<String>();
	private Map<String, String> nodeToMap;

	/**
	 * Returns only existing instance of NodeManager.
	 * 
	 * @return NodeManager
	 */
	public static NodeManager getInstance() {
		if (instance == null)
			instance = new NodeManager();
		return instance;
	}

	private NodeManager() {
		nodeToMap = Collections.synchronizedMap(new HashMap<String, String>());
	}

	/**
	 * Sets LASAD map to Planning Tool nodeId.
	 * 
	 * @param lasadMap
	 *            LASAD map name
	 * @param nodeId
	 *            Planning Tool nodeId
	 */
	public void setNodeToMap(String lasadMap, String nodeId) {
		nodeToMap.put(nodeId, lasadMap);
	}

	/**
	 * Returns LASAD map to Planning Tool nodeId.
	 * 
	 * @param nodeId
	 *            Planning Tool nodeId
	 * @return lasadMap
	 */
	public String getMapName(String nodeId) {
		return nodeToMap.get(nodeId);
	}

	/**
	 * Adds a Planning Tool nodeId to node Vector.
	 * 
	 * @param aNodeId
	 *            Planning Tool nodeId
	 */
	public void addNodeId(String aNodeId) {
		nodeIdVector.add(aNodeId);
	}

	/**
	 * Removes Planning Tool nodeId and LASAD map name.
	 * 
	 * @param aNodeId
	 *            Planning Tool nodeId
	 */
	public void removeNodeId(String aNodeId) {
		nodeIdVector.remove(aNodeId);
		nodeToMap.remove(aNodeId);
	}

	/**
	 * Tests if Planning Tool nodeId is contained.
	 * 
	 * @param aNodeId
	 *            Planning Tool nodeId
	 * @return true (nodeId is contained) / false (nodeId is not contained)
	 */
	public boolean containsNode(String aNodeId) {
		return nodeIdVector.contains(aNodeId);
	}
}
