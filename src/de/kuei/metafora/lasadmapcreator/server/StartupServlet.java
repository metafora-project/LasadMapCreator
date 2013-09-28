package de.kuei.metafora.lasadmapcreator.server;

import java.util.Vector;

import javax.servlet.http.HttpServlet;

import de.kuei.metafora.lasadmapcreator.server.mysql.ChannelDescription;
import de.kuei.metafora.lasadmapcreator.server.mysql.MysqlInitConnector;
import de.kuei.metafora.lasadmapcreator.server.mysql.ServerDescription;
import de.kuei.metafora.lasadmapcreator.server.xmpp.XMPPListener;
import de.kuei.metafora.xmppbridge.xmpp.NameConnectionMapper;
import de.kuei.metafora.xmppbridge.xmpp.ServerConnection;
import de.kuei.metafora.xmppbridge.xmpp.XmppMUC;
import de.kuei.metafora.xmppbridge.xmpp.XmppMUCManager;

public class StartupServlet extends HttpServlet {

	public static String sendingToolForLasad = "METAFORA";
	public static String sendingTool = "LASAD_MAP_CREATOR";
	public static String receivingTool = "LASAD";
	public static String planningTool = "PLANNING_TOOL";
	public static boolean logged = true;

	public static String lasad = "http://adapterrex.hcii.cs.cmu.edu:8090/lasad/";

	public static XmppMUC commandPlanningTool = null;
	public static XmppMUC commandLasad = null;

	public void init() {

		MysqlInitConnector.getInstance().loadData("LasadMapCreator");

		sendingToolForLasad = MysqlInitConnector.getInstance().getParameter(
				"SENDING_TOOL");
		sendingTool = MysqlInitConnector.getInstance().getParameter(
				"LasadMapCreator");
		receivingTool = MysqlInitConnector.getInstance().getParameter(
				"RECEIVING_TOOL");
		planningTool = MysqlInitConnector.getInstance().getParameter(
				"PlanningTool");

		if (MysqlInitConnector.getInstance().getParameter("logged")
				.toLowerCase().equals("false")) {
			logged = false;
		}

		System.err.println("SENDING_TOOL (LASAD): " + sendingToolForLasad);
		System.err.println("SENDING_TOOL: " + sendingTool);
		System.err.println("RECEIVING_TOOL (LASAD): " + receivingTool);
		System.err.println("RECEIVING_TOOL: " + planningTool);
		System.err.println("logged: " + logged);

		ServerDescription lasadServer = MysqlInitConnector.getInstance()
				.getAServer("lasad");
		if (lasadServer != null) {
			lasad = lasadServer.getServer();
		}

		// configure xmpp
		Vector<ServerDescription> xmppServers = MysqlInitConnector
				.getInstance().getServer("xmpp");

		for (ServerDescription xmppServer : xmppServers) {
			System.err.println("XMPP server: " + xmppServer.getServer());
			System.err.println("XMPP user: " + xmppServer.getUser());
			System.err.println("XMPP password: " + xmppServer.getPassword());
			System.err.println("XMPP device: " + xmppServer.getDevice());
			System.err.println("Modul: " + xmppServer.getModul());

			System.err.println("Starting XMPP connection...");

			NameConnectionMapper.getInstance().createConnection(
					xmppServer.getConnectionName(), xmppServer.getServer(),
					xmppServer.getUser(), xmppServer.getPassword(),
					xmppServer.getDevice());

			NameConnectionMapper.getInstance()
					.getConnection(xmppServer.getConnectionName())
					.addPacketListener(new XMPPListener());

			NameConnectionMapper.getInstance()
					.getConnection(xmppServer.getConnectionName()).login();
		}

		Vector<ChannelDescription> channels = MysqlInitConnector.getInstance()
				.getXMPPChannels();

		for (ChannelDescription channeldesc : channels) {
			ServerConnection connection = NameConnectionMapper.getInstance()
					.getConnection(channeldesc.getConnectionName());

			if (connection == null) {
				System.err.println("StartupServlet: Unknown connection: "
						+ channeldesc.getUser());
				continue;
			}

			System.err.println("Joining channel " + channeldesc.getChannel()
					+ " as " + channeldesc.getAlias());

			XmppMUC muc = XmppMUCManager.getInstance().getMultiUserChat(
					channeldesc.getChannel(), channeldesc.getAlias(),
					connection);
			muc.join(0);

			if (channeldesc.getConnectionName()
					.equals("LasadMapCreatorCommand")) {
				System.err.println("StartupServlet: command configured.");
				commandPlanningTool = muc;
			} else if (channeldesc.getConnectionName().equals(
					"LasadMapCreatorLasadCommand")) {
				System.err
						.println("StartupServlet: command for lasad connection configured.");
				commandLasad = muc;
			}
		}
	}

	@Override
	public void destroy() {
		Vector<ServerDescription> xmppServers = MysqlInitConnector
				.getInstance().getServer("xmpp");

		for (ServerDescription xmppServer : xmppServers) {
			NameConnectionMapper.getInstance()
					.getConnection(xmppServer.getConnectionName()).disconnect();
		}
	}
}
