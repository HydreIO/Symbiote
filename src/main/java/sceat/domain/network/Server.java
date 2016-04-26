package sceat.domain.network;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import sceat.domain.minecraft.Grades;
import sceat.domain.minecraft.RessourcePack;
import sceat.domain.minecraft.Statut;
import sceat.domain.protocol.DestinationKey;

public class Server {

	private String label;
	private String vpsLabel;
	private ServerType type;
	private int maxPlayers;
	private Statut status;
	private RessourcePack pack;
	private Map<Grades, Set<UUID>> players = new HashMap<Grades, Set<UUID>>();
	private Set<String> keys = new HashSet<String>();
	private InetAddress ipadress;
	private long timeout;

	/**
	 * Lors de la gestion, sphantom decide en fonction du nombre de joueurs combien d'instance de ce type de serveur sont requise
	 * <p>
	 * si il y a d�ja suffisament d'instance, "needed" passe sur false et permet ainsi la destruction du serveur si le dernier joueur se d�connecte
	 */
	private boolean needed = true;

	public Server(String label, ServerType type, Statut state, int maxplayer, InetAddress ip, RessourcePack pack, String... destinationKeys) {
		this.label = label;
		this.type = type;
		this.maxPlayers = maxplayer;
		this.status = state;
		this.pack = pack;
		this.ipadress = ip;
		Arrays.stream(destinationKeys).forEach(keys::add);
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

	public Server setKeys(Set<String> keys) {
		this.keys = keys;
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

	public Set<String> getKeys() {
		return keys;
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

	public static enum ServerType {
		Proxy(
				RessourcePack.RESSOURCE_PACK_DEFAULT,
				DestinationKey.PROXY,
				DestinationKey.HUBS_AND_PROXY,
				DestinationKey.HUBS_PROXY_SPHANTOM,
				DestinationKey.HUBS_PROXY_SPHANTOM_SYMBIOTE,
				DestinationKey.ALL,
				DestinationKey.ALL_SPHANTOM),
		Lobby(
				RessourcePack.RESSOURCE_PACK_DEFAULT,
				DestinationKey.ALL,
				DestinationKey.HUBS,
				DestinationKey.HUBS_AND_PROXY,
				DestinationKey.HUBS_PROXY_SPHANTOM,
				DestinationKey.HUBS_PROXY_SPHANTOM_SYMBIOTE,
				DestinationKey.ALL_SPHANTOM),
		Agares(RessourcePack.AGARES, DestinationKey.ALL, DestinationKey.SERVEURS, DestinationKey.SRV_AGARES, DestinationKey.ALL_SPHANTOM),
		AresRpg(RessourcePack.ARESRPG, DestinationKey.ALL, DestinationKey.SERVEURS, DestinationKey.SRV_ARES, DestinationKey.ALL_SPHANTOM),
		Iron(RessourcePack.IRON, DestinationKey.ALL, DestinationKey.SERVEURS, DestinationKey.SRV_IRON, DestinationKey.ALL_SPHANTOM);

		private String[] keys;
		private RessourcePack pack;

		private ServerType(RessourcePack pack, String... keys) {
			this.keys = keys;
			this.pack = pack;
		}

		public RessourcePack getPack() {
			return pack;
		}

		public String[] getKeys() {
			return keys;
		}

		public List<String> getKeysAslist() {
			return Arrays.asList(getKeys());
		}

		public Set<String> getKeysAsSet() {
			return new HashSet<String>(getKeysAslist());
		}
	}

}