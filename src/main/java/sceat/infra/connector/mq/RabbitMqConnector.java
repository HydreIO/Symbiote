package sceat.infra.connector.mq;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sceat.Symbiote;
import sceat.domain.common.java.Lambdas;
import sceat.domain.common.mq.Broker;
import sceat.domain.common.system.Log;
import sceat.domain.common.system.Root;
import sceat.domain.common.thread.ScThread;
import sceat.domain.protocol.MessagesType;
import sceat.domain.protocol.RoutingKey;
import sceat.domain.protocol.packet.PacketPhantomServerInfo;
import sceat.domain.protocol.packet.PacketPhantomSymbiote;
import sceat.domain.utils.Try;
import sceat.domain.utils.Try.TryVoidRunnable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConnector implements Broker {

	private static final RabbitMqConnector instance = new RabbitMqConnector();
	private RabbitMqReceiver receiver;
	private ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection;
	private static Channel channel;
	public static final String type = "direct";

	private final String PHANTOM_key = RoutingKey.genKey(RoutingKey.SPHANTOM);

	private RabbitMqConnector() {
	}

	public RabbitMqReceiver getReceiver() {
		return this.receiver;
	}

	/**
	 * Initialisation de la connection et du channel, ainsi que déclaration des messages json a envoyer (par leur nom : banP etc) On initialise aussi les receiver (une fois le channel créé)
	 */
	public static void init(String user, String passwd, String host, int port) {
		RabbitMqConnector co = instance;
		ConnectionFactory fac = co.getFactory();
		Lambdas.<ConnectionFactory> emptyConsumer((f) -> {
			f.setHost(host);
			f.setPort(port);
			f.setUsername(user);
			f.setPassword(passwd);
		}).accept(fac);
		Try.orVoidWithActions(TryVoidRunnable.empty(() -> {
			connection = fac.newConnection();
			channel = connection.createChannel();
		}), true, () -> {
			Log.out("Unable to access message broker RMQ, Symbiote is going down..");
			ScThread.sleep(3, TimeUnit.SECONDS);
			Root.exit(false);
		});
		Log.out("Sucessfully connected to broker RMQ");
		Arrays.stream(MessagesType.values()).forEach(co::exchangeDeclare);
		co.receiver = new RabbitMqReceiver();
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
	 * Déclaration d'un nouveau type d'échange (type de message comme le ban d'un joueur)
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
			getChannel().basicPublish(msg.getName(), key, null, array);
		} catch (IOException e) {
			Symbiote.printStackTrace(e);
		}
	}

	@Override
	public void sendServer(PacketPhantomServerInfo pkt) {
		basicPublich(MessagesType.UPDATE_SERVER, this.PHANTOM_key, pkt.toByteArray());
	}

	@Override
	public void sendInfos(PacketPhantomSymbiote pkt) {
		basicPublich(MessagesType.SYMBIOTE_INFOS, this.PHANTOM_key, pkt.toByteArray());
	}

}
