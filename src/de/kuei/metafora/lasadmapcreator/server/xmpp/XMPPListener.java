package de.kuei.metafora.lasadmapcreator.server.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.kuei.metafora.lasadmapcreator.server.LasadMapManager;
import de.kuei.metafora.lasadmapcreator.server.xml.XMLException;
import de.kuei.metafora.lasadmapcreator.server.xml.XMLUtils;

public class XMPPListener implements PacketListener {

	public void newMessage(String message, String chat) {
		if (chat.contains("logger") || chat.contains("analysis")
				|| message == null)
			return;

		message = message.replaceAll("\n", "");

		if (message.toLowerCase().contains("create_map")) {
			try {
				Document doc = XMLUtils.parseXMLString(message, false);

				String receivingTool = null;
				String sendingTool = null;

				NodeList properties = doc.getElementsByTagName("property");
				for (int i = 0; i < properties.getLength(); i++) {
					Node propertyNode = properties.item(i);
					String name = propertyNode.getAttributes()
							.getNamedItem("name").getNodeValue();
					if (name.toLowerCase().equals("receiving_tool")) {
						receivingTool = propertyNode.getAttributes()
								.getNamedItem("value").getNodeValue();
					}
					if (name.toLowerCase().equals("sending_tool")) {
						sendingTool = propertyNode.getAttributes()
								.getNamedItem("value").getNodeValue();
					}
				}

				if (((receivingTool != null) && (sendingTool != null))
						&& receivingTool.toLowerCase().startsWith("metafora")
						&& sendingTool.toLowerCase().startsWith("lasad")) {

					NodeList actiontypenl = doc
							.getElementsByTagName("actiontype");

					if (actiontypenl.getLength() > 0) {
						Node actiontype = actiontypenl.item(0);
						String succeed = actiontype.getAttributes()
								.getNamedItem("succeed").getNodeValue();

						NodeList propertiesList = doc
								.getElementsByTagName("property");

						String mapname = null;
						String id = null;
						String mapExists = null;

						for (int i = 0; i < propertiesList.getLength(); i++) {

							Node propertyNode = propertiesList.item(i);

							String name = propertyNode.getAttributes()
									.getNamedItem("name").getNodeValue();

							String value = propertyNode.getAttributes()
									.getNamedItem("value").getNodeValue();

							if (name.toLowerCase().equals("map_id")) {

								id = value;

							} else if (name.toLowerCase().equals("mapname")) {

								mapname = value;

							} else if (name.toLowerCase().equals("reason")) {

								mapExists = value;

							}
						}

						if (succeed.equals("true")) {

							if ((mapname != null) && (id != null)) {

								if (LasadMapManager.istKnownLasadMap(mapname)) {

									if ((mapExists != null)
											&& (mapExists.toLowerCase()
													.equals("already_exists"))) {

										LasadMapManager.getInstance(mapname)
												.setDoesAlreadyExist(true);

									}

									LasadMapManager.getInstance(mapname)
											.setLasadMapid(id);

									LasadMapManager.getInstance(mapname)
											.sendUpdateCommand();

								} else {
									System.err
											.println("XMPPListener.newMessage(): XMPP Message dropped because of unknown mapname: "
													+ mapname);
								}
							} else {
								System.err
										.println("XMPPListener.newMessage(): LASAD map name == null.");
							}
						} else {
							LasadMapManager.getInstance(mapname)
									.setFailed(true);
							System.err
									.println("XMPPListener.newMessage(): LASAD map name already taken.");
						}
					}
				}
			} catch (XMLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void processPacket(Packet packet) {
		if (packet instanceof Message) {
			Message message = (Message) packet;
			newMessage(message.getBody(), packet.getFrom());
		}
	}
}
