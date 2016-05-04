package sceat.domain.protocol.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import sceat.Symbiote;
import sceat.domain.minecraft.Grades;
import sceat.domain.minecraft.Statut;
import sceat.domain.network.Server.ServerType;
import sceat.domain.protocol.MessagesType;

public class PacketPhantomServerInfo extends PacketPhantom {

	private String label;
	private String vpsLabel;
	private ServerType type;
	private int maxp;
	private String ip;
	private Map<Grades, Set<UUID>> players = new HashMap<Grades, Set<UUID>>();
	private Statut state;
	private boolean fromSymbiote = false;

	public PacketPhantomServerInfo(Statut state, String label, String vpsLabel, InetAddress ip, ServerType type, int maxp, Map<Grades, Set<UUID>> pl, boolean fromSymbiote) {
		this.ip = ip.getHostAddress();
		this.vpsLabel = vpsLabel;
		this.label = label;
		this.type = type;
		this.players = pl == null ? new HashMap<Grades, Set<UUID>>() : pl;
		if (pl == null) Arrays.stream(Grades.values()).forEach(g -> players.put(g, new HashSet<UUID>()));
		this.maxp = maxp;
		this.state = state;
	}

	public PacketPhantomServerInfo() {
	}

	@Override
	public String toString() {
		return "PacketUpdateServer [" + getLabel() + "|" + getState().name() + "|players(" + getPlayers().size() + ")]";
	}

	@Override
	protected void serialize_() {
		writeString(getLabel());
		writeString(this.vpsLabel);
		writeByte(getType().getId());
		writeInt(getMaxp());
		writeString(this.ip);
		writeMap(this.players, d -> writeString(d.name()), d -> writeCollection(d, e -> writeString(e.toString())));
		writeString(getState().name());
		writeBoolean(isFromSymbiote());
	}

	@Override
	protected void deserialize_() {
		this.label = readString();
		this.vpsLabel = readString();
		this.type = ServerType.fromByte(readByte());
		this.maxp = readInt();
		this.ip = readString();
		this.players = readMap(() -> Grades.valueOf(readString()), () -> readCollection(new HashSet<UUID>(), () -> UUID.fromString(readString())));
		this.state = Statut.valueOf(readString());
		this.fromSymbiote = readBoolean();
	}

	@Override
	public void handleData(MessagesType tp) {
	}

	public boolean isFromSymbiote() {
		return this.fromSymbiote;
	}

	public Statut getState() {
		return state;
	}

	public String getVpsLabel() {
		return vpsLabel;
	}

	public int getMaxp() {
		return maxp;
	}

	public String getLabel() {
		return label;
	}

	public ServerType getType() {
		return type;
	}

	public InetAddress getIp() {
		try {
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			Symbiote.printStackTrace(e);
			return null;
		}
	}

	public Map<Grades, Set<UUID>> getPlayersPerGrade() {
		return players;
	}

	public Set<UUID> getPlayers() {
		return getPlayersPerGrade().values().stream().reduce((s1, s2) -> {
			s1.addAll(s2);
			return s1;
		}).orElse(Collections.emptySet());
	}

}
