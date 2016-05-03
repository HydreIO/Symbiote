package sceat.domain;

import java.net.UnknownHostException;

import sceat.Symbiote;
import sceat.domain.network.Vps.VpsState;
import sceat.domain.protocol.PacketSender;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;
import sceat.domain.schedule.Schedule;
import sceat.domain.schedule.Scheduled;
import sceat.domain.schedule.Scheduler;
import sceat.domain.schedule.TimeUnit;
import sceat.domain.utils.cosmos.MemoryParser;

public class Core implements Scheduled {

	private static Core instance;

	public Core() {
		instance = this;
		Scheduler.getScheduler().register(this);
	}

	public static Core getInstance() {
		return instance;
	}

	@Schedule(rate = 1, unit = TimeUnit.MINUTES)
	public void run() throws UnknownHostException {
		PacketSender.getInstance().sendInfos(new PacketPhantomSymbiote(Symbiote.VpsLabel, VpsState.Online, MemoryParser.getRam(), Symbiote.getInstance().getIp(), PacketSender.created));
	}

}
