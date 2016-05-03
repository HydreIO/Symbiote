package sceat.domain.protocol;

import sceat.Symbiote;
import sceat.domain.Security;
import sceat.domain.adapter.mq.Imessaging;
import sceat.domain.network.Vps.VpsState;
import sceat.domain.protocol.packet.PacketPhantom;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;
import sceat.domain.utils.cosmos.MemoryParser;
import sceat.infra.connector.mq.RabbitMqConnector;

public class PacketSender {

	private static PacketSender instance;
	private Imessaging broker;
	public static final long created = System.currentTimeMillis();

	public PacketSender(String user, String pass, String host, int port) {
		instance = this;
		broker = new RabbitMqConnector(user, pass, host, port);
		sendInfos(new PacketPhantomSymbiote(Symbiote.VpsLabel, VpsState.Online, MemoryParser.getRam(), Symbiote.getInstance().getIp(), created));
	}

	public static PacketSender getInstance() {
		return instance;
	}

	public Imessaging getBroker() {
		return broker;
	}

	private void setSecurity(PacketPhantom pkt) {
		pkt.setSecu(Security.generateNull());
	}

	public void sendServer(PacketPhantomServerInfo pkt) {
		Symbiote.print("<<<<]RECV] PacketUpdateServer [" + pkt.getLabel() + "|" + pkt.getState().name() + "|players(" + pkt.getPlayers().size() + ")] |to:SPHANTOM");
		setSecurity(pkt);
		getBroker().sendServer(pkt.serialize());
	}

	public void sendInfos(PacketPhantomSymbiote pkt) {
		Symbiote.print(">>>>]SEND] PacketSymbiote [" + pkt.getVpsLabel() + "|" + pkt.getState() + "|" + pkt.getIp().getHostAddress() + "|Ram(" + pkt.getRam() + ")] |to:SPHANTOM");
		setSecurity(pkt);
		getBroker().sendInfos(pkt.serialize());
	}

}
