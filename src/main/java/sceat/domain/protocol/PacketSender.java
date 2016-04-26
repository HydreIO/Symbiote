package sceat.domain.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sceat.Symbiote;
import sceat.domain.adapter.mq.Imessaging;
import sceat.domain.network.Vps.VpsState;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;
import sceat.domain.utils.cosmos.MemoryParser;
import sceat.infra.connector.mq.RabbitMqConnector;

public class PacketSender {

	private static PacketSender instance;
	private Imessaging broker;

	public PacketSender(String user, String pass, String host, int port) {
		instance = this;
		broker = new RabbitMqConnector(user, pass, host, port);
		try {
			sendInfos(new PacketPhantomSymbiote(Symbiote.VpsLabel, VpsState.Online, MemoryParser.getRam(), InetAddress.getLocalHost()));
		} catch (UnknownHostException e) {
			Symbiote.printStackTrace(e);
		}
	}

	public static PacketSender getInstance() {
		return instance;
	}

	public Imessaging getBroker() {
		return broker;
	}

	public void sendServer(PacketPhantomServerInfo pkt) {
		Symbiote.print(">>>>]SEND] PacketServerInfos |to:SPHANTOM");
		getBroker().sendServer(pkt.serialize());
	}

	public void sendInfos(PacketPhantomSymbiote pkt) {
		Symbiote.print(">>>>]SEND] PacketSymbiote |to:SPHANTOM");
		getBroker().sendInfos(pkt.serialize());
	}

}
