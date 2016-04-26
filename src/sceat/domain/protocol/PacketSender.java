package sceat.domain.protocol;

import sceat.Symbiote;
import sceat.domain.adapter.mq.Imessaging;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;
import sceat.infra.connector.mq.RabbitMqConnector;

public class PacketSender {

	private static PacketSender instance;
	private Imessaging broker;

	public PacketSender(String user, String pass) {
		instance = this;
		broker = new RabbitMqConnector(user, pass);
	}

	public static PacketSender getInstance() {
		return instance;
	}

	public Imessaging getBroker() {
		return broker;
	}

	public void sendServer(PacketPhantomServerInfo pkt) {
		Symbiote.print(">>>>]SEND] PacketServerInfos |to:SPHANTOM");
		getBroker().sendServer(pkt);
	}

	public void sendInfos(PacketPhantomSymbiote pkt) {
		Symbiote.print(">>>>]SEND] PacketSymbiote |to:SPHANTOM");
		getBroker().sendInfos(pkt);
	}

}
