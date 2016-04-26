package sceat.domain.adapter.mq;

import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;

public interface Imessaging {

	void sendServer(PacketPhantomServerInfo pkt);

	void sendInfos(PacketPhantomSymbiote pkt);

}
