package sceat.domain.common.system;

import java.util.function.Consumer;

import sceat.Symbiote;

public interface Root {

	public static <T> T chain(T t, Consumer<T> consumer) {
		consumer.accept(t);
		return t;
	}

	void exit();

	public static void exit(boolean crash) {
		Symbiote.shutDown();
	}

	public static Symbiote get() {
		return Symbiote.getInstance();
	}

}