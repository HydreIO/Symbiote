package sceat.domain.protocol.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sceat.Symbiote;
import sceat.domain.network.Server;
import sceat.domain.network.Server.ServerType;
import sceat.domain.protocol.MessagesType;

public class PacketPhantomBootServer extends PacketPhantom {

	private String label;
	private String vpsLabel;
	private ServerType type;
	private InetAddress ip;
	private int ram;
	private int maxP;

	public PacketPhantomBootServer(Server srv, int ram) {
		this.label = srv.getLabel();
		this.type = srv.getType();
		this.vpsLabel = srv.getVpsLabel();
		this.ip = srv.getIpadress();
		this.ram = ram;
		this.maxP = srv.getMaxPlayers();
	}

	@Override
	protected void serialize_() {
		writeString(getLabel());
		writeString(getVpsLabel());
		writeString(getType().name());
		writeString(getIp().getHostAddress());
		writeInt(getRam());
		writeInt(getMaxP());
	}

	@Override
	protected void deserialize_() {
		this.label = readString();
		this.vpsLabel = readString();
		this.type = ServerType.valueOf(readString());
		try {
			this.ip = InetAddress.getByName(readString());
		} catch (UnknownHostException e) {
			Symbiote.printStackTrace(e);
		}
		this.ram = readInt();
		this.maxP = readInt();
	}

	public InetAddress getIp() {
		return ip;
	}

	public String getVpsLabel() {
		return vpsLabel;
	}

	public String getLabel() {
		return label;
	}

	public ServerType getType() {
		return type;
	}

	public int getRam() {
		return ram;
	}

	public int getMaxP() {
		return maxP;
	}

	@Override
	public void handleData(MessagesType type) {
		// TODO:
	}

}
