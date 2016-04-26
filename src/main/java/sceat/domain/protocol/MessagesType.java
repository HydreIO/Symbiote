package sceat.domain.protocol;

/**
 * Enum de type de messages
 *
 * @author MrSceat
 */
public enum MessagesType {
	UPDATE_SERVER("exchange_server", 3),
	BOOT_SERVER("exchange_symbiote_bootServer", 2),
	SYMBIOTE_INFOS("exchange_symbiote", 1);

	private String exchangeName;
	private int priority;

	MessagesType(String name, int priority) {
		this.exchangeName = name;
		this.priority = priority;
	}

	public String getName() {
		return exchangeName;
	}

	public static MessagesType fromString(String exchangeN, boolean notNull) {
		for (MessagesType e : values())
			if (e.getName().equals(exchangeN)) return e;
		if (notNull) throw new NullPointerException("Aucun type de message n'a pour valeur " + exchangeN);
		return null;
	}

	public int getPriority() {
		return priority;
	}
}