package sceat.domain.protocol.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
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
	private Set<String> keys = new HashSet<String>();
	private Statut state;
	private boolean fromSymbiote = false;

	public PacketPhantomServerInfo(Statut state, String label, String vpsLabel, InetAddress ip, ServerType type, int maxp, Map<Grades, Set<UUID>> pl, Set<String> keys, boolean fromSymbiote) {
		this.ip = ip.getHostAddress();
		this.vpsLabel = vpsLabel;
		this.label = label;
		this.keys = keys == null ? new HashSet<String>() : keys;
		this.type = type;
		this.players = pl == null ? new HashMap<Grades, Set<UUID>>() : pl;
		this.maxp = maxp;
		this.state = state;
	}

	public PacketPhantomServerInfo() {
	}

	@Override
	protected void serialize_() {
		writeString(getLabel());
		writeString(this.vpsLabel);
		writeString(getType().name());
		writeInt(getMaxp());
		writeString(this.ip);
		writeMap(this.players, d -> writeString(d.name()), d -> writeCollection(d, e -> writeString(e.toString())));
		writeCollection(this.keys, this::writeString);
		writeString(getState().name());
		writeBoolean(isFromSymbiote());
	}

	@Override
	protected void deserialize_() {
		this.label = readString();
		this.vpsLabel = readString();
		this.type = ServerType.valueOf(readString());
		this.maxp = readInt();
		this.ip = readString();
		this.players = readMap(() -> Grades.valueOf(readString()), () -> readCollection(new HashSet<UUID>(), () -> UUID.fromString(readString())));
		this.keys = readCollection(new HashSet<String>(), this::readString);
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

	public Collection<String> getKeys() {
		return keys;
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
		}).get();
	}

}