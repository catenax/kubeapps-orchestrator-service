package simplewfms;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable{
    @Override
    default void run() {
        try {
            runThrows();
        } catch (final Exception t) {
            throw LombokTool.sneakyThrow(t);
        }
    }

    void runThrows() throws Exception;

}
