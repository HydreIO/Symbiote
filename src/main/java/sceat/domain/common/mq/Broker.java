package sceat.domain.common.mq;

import sceat.domain.protocol.PacketSender;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;

public interface Broker {

	void sendServer(PacketPhantomServerInfo pkt);

	void sendInfos(PacketPhantomSymbiote pkt);

	void close();

	public static Broker get() {
		return PacketSender.getBroker();
	}

}
