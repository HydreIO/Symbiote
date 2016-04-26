package sceat.infra.connector.mq;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import sceat.Symbiote;
import sceat.domain.adapter.mq.Imessaging;
import sceat.domain.protocol.DestinationKey;
import sceat.domain.protocol.MessagesType;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConnector implements Imessaging {

	private static RabbitMqConnector instance;
	private RabbitMqReceiver receiver;

	public static boolean routingEnabled = true;

	private ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection;

	private static Channel channel;

	public static final String type = routingEnabled ? "direct" : "fanout";

	public RabbitMqConnector(String user, String pass, String host, int port) {
		init(user, pass, host, port);
	}

	public RabbitMqReceiver getReceiver() {
		return this.receiver;
	}

	/**
	 * Initialisation de la connection et du channel, ainsi que d�claration des messages json a envoyer (par leur nom : banP etc) On initialise aussi les receiver (une fois le channel cr��)
	 */
	public void init(String user, String passwd, String host, int port) {
		instance = this;
		getFactory().setHost(host);
		getFactory().setPort(port);
		getFactory().setUsername(user);
		getFactory().setPassword(passwd);
		try {
			connection = getFactory().newConnection();
			channel = getConnection().createChannel();
		} catch (IOException | TimeoutException e) {
			Symbiote.print("Unable to access message broker RMQ, Symbiote is going down..", true);
			Symbiote.printStackTrace(e);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				Symbiote.printStackTrace(e);
			}
			Symbiote.shutDown();
			return;
		}
		Symbiote.print("Sucessfully connected to broker RMQ");
		Arrays.stream(MessagesType.values()).forEach(this::exchangeDeclare);
		this.receiver = new RabbitMqReceiver();
	}

	/**
	 * Utilis� pour fermer la connection onDisable // A METTRE ONDISABLE()
	 */
	public void close() {
		try {
			getChannel().close();
			getConnection().close();
		} catch (IOException | TimeoutException e) {
			Symbiote.printStackTrace(e);
		}
	}

	// **************** Getters ***************

	public ConnectionFactory getFactory() {
		return this.factory;
	}

	public Connection getConnection() {
		return connection;
	}

	public Channel getChannel() {
		return channel;
	}

	// **************** Utils *****************

	/**
	 * D�claration d'un nouveau type d'�change (type de message comme le ban d'un joueur)
	 *
	 * @param exchange
	 */
	public void exchangeDeclare(MessagesType msg) {
		try {
			getChannel().exchangeDeclare(msg.getName(), type);
		} catch (IOException e) {
			Symbiote.printStackTrace(e);
		}
	}

	public static RabbitMqConnector getInstance() {
		return instance;
	}

	/**
	 * Publication d'un message
	 *
	 * @param msg
	 *            le type du message
	 * @param key
	 *            le ou les endroit qui vont recevoir le message
	 * @param array
	 *            le pkt
	 */
	public void basicPublich(MessagesType msg, String key, byte[] array) {
		try {
			getChannel().basicPublish(msg.getName(), routingEnabled ? key : "", null, array);
		} catch (IOException e) {
			Symbiote.printStackTrace(e);
		}
	}

	@Override
	public void sendServer(PacketPhantomServerInfo pkt) {
		basicPublich(MessagesType.UPDATE_SERVER, DestinationKey.SPHANTOM, pkt.toByteArray());
	}

	@Override
	public void sendInfos(PacketPhantomSymbiote pkt) {
		basicPublich(MessagesType.SYMBIOTE_INFOS, DestinationKey.SPHANTOM, pkt.toByteArray());
	}

}
