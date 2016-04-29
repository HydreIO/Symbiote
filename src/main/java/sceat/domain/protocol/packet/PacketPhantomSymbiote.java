package sceat.domain.protocol.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sceat.Symbiote;
import sceat.domain.network.Vps.VpsState;
import sceat.domain.protocol.MessagesType;

public class PacketPhantomSymbiote extends PacketPhantom {

	@Override
	protected void serialize_() {
		writeString(getVpsLabel());
		writeByte(getState().getId());
		writeInt(getRam());
		writeString(getIp().getHostAddress());
	}

	@Override
	protected void deserialize_() {
		this.vpsLabel = readString();
		this.state = VpsState.fromId(readByte());
		this.ram = readInt();
		try {
			this.ip = InetAddress.getByName(readString());
		} catch (UnknownHostException e) {
			Symbiote.printStackTrace(e);
		}
	}

	public PacketPhantomSymbiote() {

	}

	private String vpsLabel;
	private VpsState state;
	private int ram;
	private InetAddress ip;

	public PacketPhantomSymbiote(String vpsLabel, VpsState state, int ram, InetAddress ip) {
		this.vpsLabel = vpsLabel;
		this.state = state;
		this.ip = ip;
		this.ram = ram;
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
