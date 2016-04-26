package sceat;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import sceat.domain.Core;
import sceat.domain.adapter.general.IserverMC;
import sceat.domain.protocol.PacketSender;

public class Symbiote {

	public static Logger logger = Logger.getLogger("Symbiote.class");
	public static File folder;
	private static Symbiote instance;
	public static String VpsLabel;

	public static void main(String[] args) {
		folder = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		Options opt = new Options();
		CommandLine cmd = setupOptions(opt, args);
		if (!cmd.hasOption("label")) {
			print("[WARN] Missing argument -label \"label\" (vm host/server label)");
			shutDown();
		}
		VpsLabel = cmd.getOptionValue("label");
		Symbiote.print("Launching symbiote..");
		Symbiote.print("VpsLabel : " + VpsLabel);
		if (cmd.hasOption("auth") && cmd.hasOption("host") && cmd.hasOption("port")) {
			String auth = cmd.getOptionValue("auth");
			String host = cmd.getOptionValue("host");
			String port = cmd.getOptionValue("port");
			if (!auth.contains("@")) {
				print("[WARN] Invalid argument ! syntaxe must be \"user@pass\" for -auth");
				shutDown();
			}
			if (!port.matches("^-?\\d+$")) {
				print("[WARN] Invalid argument ! -port is not a number");
				shutDown();
			}
			int portt = Integer.parseInt(port);
			String user = auth.substring(0, args[1].indexOf('@'));
			String pass = auth.substring(args[1].indexOf('@') + 1);
			new Symbiote(user, pass, host, portt);
		} else {
			print("[ERR] An argument is missing, required args :");
			print("> -auth \"user@pass\"");
			print("> -host \"0.0.0.0\"");
			print("> -port \"000\"");
			shutDown();
		}
	}

	private IserverMC serverBuilder;

	public static void shutDown() {
		print("Shuting down..");
		print("Bye.");
		System.exit(1);
	}

	public Symbiote(String user, String pass, String host, int port) {
		instance = this;
		initLogger();
		this.serverBuilder = null;
		new PacketSender(user, pass, host, port);
		new Core();
	}

	public IserverMC getServerBuilder() {
		return serverBuilder;
	}

	public static void printStackTrace(Exception e) {
		getLogger().log(Level.SEVERE, e.getMessage(), e);
	}

	public static void printStackTrace(Throwable e) {
		getLogger().log(Level.SEVERE, e.getMessage(), e);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Symbiote getInstance() {
		return instance;
	}

	public static void print(String txt) {
		print(txt, true);
	}

	public static void print(String txt, boolean log) {
		if (log) logger.info(txt);
		else System.out.println(new java.sql.Timestamp(System.currentTimeMillis()).toString().substring(0, 16) + " | [Symbiote] > " + txt);
	}

	public static CommandLine setupOptions(Options opt, String[] args) {
		opt.addOption("auth", true, "RabbitMq User@Pass");
		opt.addOption("host", true, "RabbitMq host");
		opt.addOption("port", true, "RabbitMq port");
		opt.addOption("label", true, "Vm host/Server label from api");
		try {
			return new BasicParser().parse(opt, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void initLogger() {
		FileHandler file;
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}

		try {
			file = new FileHandler(folder.getAbsolutePath() + "/Symbiote.log");
			file.setFormatter(new Formatter() {

				@Override
				public String format(LogRecord record) {
					return new java.sql.Timestamp(System.currentTimeMillis()).toString().substring(0, 16) + " | [Symbiote] > " + record.getMessage() + "\n";
				}
			});
			ConsoleHandler hand = new ConsoleHandler();
			hand.setFormatter(new Formatter() {

				@Override
				public String format(LogRecord record) {
					return new java.sql.Timestamp(System.currentTimeMillis()).toString().substring(0, 16) + " | [Symbiote] > " + record.getMessage() + "\n";
				}
			});
			logger.addHandler(hand);
			logger.addHandler(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
