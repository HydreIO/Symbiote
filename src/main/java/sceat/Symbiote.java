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

public class Symbiote {

	public static Logger logger = Logger.getLogger("Symbiote.class");
	public static File folder;
	private static Symbiote instance;

	public static void main(String[] args) {
		folder = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		Options opt = new Options();
		CommandLine cmd = setupOptions(opt, args);
		if (cmd.hasOption("auth")) {
			String str = cmd.getOptionValue("auth");
			if (!str.contains("@")) {
				print("[WARN] Invalid argument ! syntaxe must be \"user@pass\" for -auth");
				shutdown();
			}
			String user = str.substring(0, args[1].indexOf('@'));
			String pass = str.substring(args[1].indexOf('@') + 1);
			new Symbiote(user, pass);
		} else {
			print("[ERR] -auth \"user@pass\" argument required !");
			shutdown();
		}
	}

	public static void shutdown() {
		print("Shuting down..");
		print("Bye.");
		System.exit(1);
	}

	public Symbiote(String user, String pass) {
		instance = this;
		initLogger();

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
