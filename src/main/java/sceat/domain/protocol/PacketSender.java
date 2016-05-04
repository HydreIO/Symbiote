package sceat.domain.protocol;

import sceat.Symbiote;
import sceat.domain.Security;
import sceat.domain.common.mq.Broker;
import sceat.domain.common.system.Log;
import sceat.domain.network.Vps.VpsState;
import sceat.domain.protocol.packet.PacketPhantom;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;
import sceat.domain.utils.cosmos.MemoryParser;
import sceat.infra.connector.mq.RabbitMqConnector;

public class PacketSender {

	private static PacketSender instance;
	public static final long created = System.currentTimeMillis();

	public PacketSender(String user, String pass, String host, int port) {
		instance = this;
		RabbitMqConnector.init(user, pass, host, port);
		sendInfos(new PacketPhantomSymbiote(Symbiote.VpsLabel, VpsState.Online, MemoryParser.getRam(), Symbiote.getInstance().getIp(), created));
	}

	public static PacketSender getInstance() {
		return instance;
	}

	public static Broker getBroker() {
		return RabbitMqConnector.getInstance();
	}

	private void setSecurity(PacketPhantom pkt) {
		pkt.setSecu(Security.generateNull());
	}

	public void sendServer(PacketPhantomServerInfo pkt) {
		Log.packet(pkt, false);
		setSecurity(pkt);
		Broker.get().sendServer(pkt.serialize());
	}

	public void sendInfos(PacketPhantomSymbiote pkt) {
		Log.packet(pkt, false);
		setSecurity(pkt);
		getBroker().sendInfos(pkt.serialize());
	}

}
