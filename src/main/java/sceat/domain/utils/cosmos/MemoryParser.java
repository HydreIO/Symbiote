package sceat.domain.utils.cosmos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sceat.Symbiote;

public class MemoryParser {

	private static MemoryParser parser = new MemoryParser(() -> Ram.getRam());

	private int ram;

	public MemoryParser(Supplier<Integer> t) {
		this.ram = t.get();
	}

	/**
	 * Ram de la machine
	 * 
	 * @return la ram en GO (3774 = 4GO)
	 */
	public static int getRam() {
		return parser.ram;
	}

	static class Ram {
		public enum Unit {
			GIGABYTES("--giga"),
			MEGABYTES("--mega"),
			KILOBYTES("--kilo");

			private final String option;

			Unit(String option) {
				this.option = option;
			}

			public String getOption() {
				return option;
			}
		}

		public static final String COMMAND = "free -t";
		public static final Pattern RAM_PATTERN = Pattern.compile("Mem: \\s*([0-9]*)");

		public static int getRam() {
			try {
				String mem = Files.lines(Paths.get("/proc/meminfo")).findFirst().orElse("a");
				if (mem.length() < 2) return 0;
				return (int) (Integer.parseInt(mem.replaceAll("[^-?0-9]+", "")) * 0.000001);
			} catch (Exception e) {
				Symbiote.printStackTrace(e);
				return 0;
			}
		}

		public static int getRam(Unit unit) {
			try {
				Process ramProcces = Runtime.getRuntime().exec(COMMAND + " " + unit.getOption());
				ramProcces.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ramProcces.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null)
					sb.append(line);
				reader.close();
				Matcher matcher = RAM_PATTERN.matcher(sb.toString());
				matcher.find();
				return Integer.parseInt(matcher.group(1));
			} catch (Exception e) {
				Symbiote.print("[ERR] Ram command");
				Symbiote.printStackTrace(e);
				return 0;
			}
		}
	}
}
