package sceat.domain.network;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import sceat.domain.minecraft.Grades;
import sceat.domain.minecraft.RessourcePack;
import sceat.domain.minecraft.Statut;
import sceat.domain.protocol.RoutingKey;

public class Server {

	private String label;
	private String vpsLabel;
	private ServerType type;
	private int maxPlayers;
	private Statut status;
	private RessourcePack pack;
	private Map<Grades, Set<UUID>> players = new HashMap<Grades, Set<UUID>>();
	private InetAddress ipadress;
	private long timeout;

	/**
	 * Lors de la gestion, sphantom decide en fonction du nombre de joueurs combien d'instance de ce type de serveur sont requise
	 * <p>
	 * si il y a d�ja suffisament d'instance, "needed" passe sur false et permet ainsi la destruction du serveur si le dernier joueur se d�connecte
	 */
	private boolean needed = true;

	public Server(String label, ServerType type, Statut state, int maxplayer, InetAddress ip, RessourcePack pack) {
		this.label = label;
		this.type = type;
		this.maxPlayers = maxplayer;
		this.status = state;
		this.pack = pack;
		this.ipadress = ip;
	}

	public Server setVpsLabel(String label) {
		this.vpsLabel = label;
		return this;
	}

	public String getVpsLabel() {
		return vpsLabel;
	}

	public void heartBeat() {
		this.timeout = System.currentTimeMillis();
	}

	public boolean hasTimeout() {
		return System.currentTimeMillis() > this.timeout + 10000;
	}

	public boolean isNeeded() {
		return this.needed;
	}

	public Server setStatus(Statut st) {
		this.status = st;
		return this;
	}

	public Server setType(ServerType type) {
		this.type = type;
		return this;
	}

	public Server setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
		return this;
	}

	public Server setPack(RessourcePack pack) {
		this.pack = pack;
		return this;
	}

	public Server setPlayers(Map<Grades, Set<UUID>> players) {
		this.players = players;
		return this;
	}

	public Server setIpadress(InetAddress ipadress) {
		this.ipadress = ipadress;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public InetAddress getIpadress() {
		return ipadress;
	}

	public ServerType getType() {
		return type;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public Statut getStatus() {
		return status;
	}

	public RessourcePack getPack() {
		return pack;
	}

	public Set<UUID> getPlayers(Grades gr) {
		return getPlayersMap().get(gr);
	}

	public Map<Grades, Set<UUID>> getPlayersMap() {
		return players;
	}

	public int countPlayers() {
		return getPlayersMap().entrySet().stream().mapToInt(e -> e.getValue().size()).reduce((a, b) -> a + b).getAsInt();
	}

	public Set<UUID> getPlayers() {
		return getPlayersMap().values().stream().reduce((t, u) -> {
			t.addAll(u);
			return t;
		}).get();
	}

	public enum ServerType {
		PROXY((byte) 0, RessourcePack.RESSOURCE_PACK_DEFAULT, RoutingKey.PROXY),
		LOBBY((byte) 1, RessourcePack.RESSOURCE_PACK_DEFAULT, RoutingKey.HUBS),
		AGARES((byte) 2, RessourcePack.AGARES, RoutingKey.SERVERS),
		ARES_RPG((byte) 3, RessourcePack.ARESRPG, RoutingKey.SERVERS),
		IRON((byte) 4, RessourcePack.IRON, RoutingKey.SERVERS);

		private byte id;
		private RoutingKey key;
		private RessourcePack pack;

		private ServerType(byte id, RessourcePack pack, RoutingKey key) {
			this.key = key;
			this.id = id;
			this.pack = pack;
		}

		public byte getId() {
			return id;
		}

		public RessourcePack getPack() {
			return pack;
		}

		public static ServerType fromByte(byte id) {
			return Arrays.stream(values()).filter(i -> i.id == id).findFirst().orElse(null);
		}

		public RoutingKey getKey() {
			return key;
		}
	}

}