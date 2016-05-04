package sceat.domain.protocol.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sceat.Symbiote;
import sceat.domain.network.Vps.VpsState;
import sceat.domain.protocol.MessagesType;

public class PacketPhantomSymbiote extends PacketPhantom {

	private String vpsLabel;
	private VpsState state;
	private int ram;
	private long created;
	private InetAddress ip;

	public PacketPhantomSymbiote(String vpsLabel, VpsState state, int ram, InetAddress ip, long created) {
		this.vpsLabel = vpsLabel;
		this.created = created;
		this.state = state;
		this.ip = ip;
		this.ram = ram;
	}

	public PacketPhantomSymbiote() {
	}

	@Override
	public String toString() {
		return "PacketSymbiote [" + getVpsLabel() + "|" + getState() + "|" + getIp().getHostAddress() + "|Ram(" + getRam() + ")]";
	}

	@Override
	protected void serialize_() {
		writeString(getVpsLabel());
		writeByte(getState().getId());
		writeInt(getRam());
		writeLong(created);
		writeString(getIp().getHostAddress());
	}

	@Override
	protected void deserialize_() {
		this.vpsLabel = readString();
		this.state = VpsState.fromId(readByte());
		this.ram = readInt();
		this.created = readLong();
		try {
			this.ip = InetAddress.getByName(readString());
		} catch (UnknownHostException e) {
			Symbiote.printStackTrace(e);
		}
	}

	public long getCreated() {
		return created;
	}

	public String getVpsLabel() {
		return vpsLabel;
	}

	public InetAddress getIp() {
		return ip;
	}

	public VpsState getState() {
		return state;
	}

	public int getRam() {
		return ram;
	}

	@Override
	public void handleData(MessagesType type) {
	}

}
