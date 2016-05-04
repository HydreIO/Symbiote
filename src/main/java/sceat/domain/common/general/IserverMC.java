package sceat.domain.common.general;

import sceat.domain.network.Server.ServerType;

public interface IserverMC {

	void startServer(ServerType type, String label, String vpsLabel, int ram);

	void killProcess(String server, boolean removeFolder, boolean extractLog);

}
