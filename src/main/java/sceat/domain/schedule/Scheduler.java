package sceat.domain.schedule;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
	private static ThreadFactory factory = new ThreadFactory() {
		final AtomicInteger count = new AtomicInteger(0);
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable , "Scheduler pool - [Thrd: "+count+"]");
			thread.setDaemon(true);
			return thread;
		}
	};

	private static Scheduler instance = new Scheduler();

	public static Scheduler getScheduler() {
		return instance;
	}

	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(50, factory);

	private Scheduler() {
	}

	public void shutdown() {
		pool.shutdown();
	}

	public void register(Scheduled scheduled) {
		Arrays.stream(scheduled.getClass().getMethods()).filter((m) -> m.isAnnotationPresent(Schedule.class)).forEach((m) -> {
			Schedule s = m.getAnnotation(Schedule.class);
			long ns = s.unit().toNanos(s.rate());
			pool.scheduleAtFixedRate(() -> {
				try {
					m.invoke(scheduled, new Object[m.getParameterCount()]);
				} catch (Exception e) {
					System.out.println("[Scheduler] Error with " + "//: " + m.getDeclaringClass() + "//:" + m.getName());
					e.printStackTrace();
				}
			}, ns, ns, java.util.concurrent.TimeUnit.NANOSECONDS);
		});
	}

}
