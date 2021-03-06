package sceat.domain.network;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Vps {

	private String label;
	private int ram;
	private InetAddress ip;
	private VpsState state;

	/**
	 * labels
	 */
	private Set<Server> servers;

	public static Vps fromBoot(String label, int ram, InetAddress ip) {
		return new Vps(label, ram, ip, new HashSet<Server>()).setState(VpsState.Deploying);
	}

	public Vps(String label, int ram, InetAddress ip, Set<Server> srvs) {
		this.label = label;
		this.ram = ram;
		this.servers = srvs;
		this.ip = ip;
	}

	public VpsState getState() {
		return state;
	}

	public Vps setState(VpsState st) {
		this.state = st;
		return this;
	}

	public InetAddress getIp() {
		return ip;
	}

	public String getLabel() {
		return label;
	}

	public int getRam() {
		return ram;
	}

	public Set<Server> getServers() {
		return servers;
	}

	public static enum VpsState {
		Deploying((byte) 0),
		Online((byte) 1),
		Destroying((byte) 2);

		private byte id;

		private VpsState(byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}

		public static VpsState fromId(byte id) {
			return Arrays.stream(values()).filter(i -> i.id == id).findFirst().orElse(null);
		}
	}
}
