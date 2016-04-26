package sceat.domain.utils.cosmos;

import java.util.function.Supplier;

public class MemoryParser {

	private static MemoryParser parser = new MemoryParser(null);

	private byte ram;

	private MemoryParser(Supplier<Byte> t) {
		this.ram = t.get();
	}

	/**
	 * Ram de la machine
	 * 
	 * @return la ram en GO (3774 = 4GO)
	 */
	public static byte getRam() {
		return parser.ram;
	}

}
