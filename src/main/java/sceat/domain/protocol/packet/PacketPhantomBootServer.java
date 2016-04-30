package sceat.domain.protocol.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import sceat.Symbiote;
import sceat.domain.minecraft.Grades;
import sceat.domain.minecraft.Statut;
import sceat.domain.network.Server;
import sceat.domain.network.Server.ServerType;
import sceat.domain.protocol.MessagesType;
import sceat.domain.protocol.PacketSender;

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

	public PacketPhantomBootServer() {

	}

	@Override
	protected void serialize_() {
		writeString(getLabel());
		writeString(getVpsLabel());
		writeByte(getType().getId());
		writeString(getIp().getHostAddress());
		writeInt(getRam());
		writeInt(getMaxP());
	}

	@Override
	protected void deserialize_() {
		this.label = readString();
		this.vpsLabel = readString();
		this.type = ServerType.fromByte(readByte());
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
	public void handleData(MessagesType tp) {
		try {
			if (getVpsLabel() != null) {
				if (!Symbiote.VpsLabel.equalsIgnoreCase(getVpsLabel())) return;
			} else if (!getIp().getHostAddress().equals(Symbiote.getInstance().getIp().getHostAddress())) return;
			Symbiote.getInstance().getServerBuilder().startServer(getType(), getLabel(), getVpsLabel(), getRam());
			Symbiote.print("Creating MC server with param : |Label('" + getLabel() + "')|VpsLabel('" + getVpsLabel() + "')|Type('" + getType() + "')|MaxP(" + getMaxP() + ")");
		} catch (Exception e) {
			Symbiote.printStackTrace(e);
		} finally {
			PacketSender.getInstance()
					.sendServer(new PacketPhantomServerInfo(Statut.BOOTING, label, vpsLabel, ip, getType(), getMaxP(), new HashMap<Grades, Set<UUID>>(), new HashSet<String>(), true));
		}
	}
}
