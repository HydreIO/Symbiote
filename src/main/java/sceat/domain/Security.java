package sceat.domain;

import java.util.UUID;

/**
 * not used somewhere else than sphantom
 * 
 * @author MrSceat
 *
 */
public class Security {

	private String serial;
	private String security;

	public static Security generateNull() {
		return new Security("NaN", "NaN");
	}

	public Security(String serial, String security) {
		this.security = security;
		this.serial = serial;
	}

	public Security(UUID serial, UUID security) {
		this.security = security.toString();
		this.serial = serial.toString();
	}

	public Security setSerial(String serial) {
		this.serial = serial;
		return this;
	}

	public Security setSecurity(String security) {
		this.security = security;
		return this;
	}

	public boolean correspond(Security pkt) {
		return pkt.getSerial().equals(getSerial()) && pkt.getSecurity().equals(getSecurity());
	}

	public String getSecurity() {
		return security;
	}

	public String getSerial() {
		return serial;
	}

}
