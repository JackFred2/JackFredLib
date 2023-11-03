package red.jackf.jackfredlib.impl.config;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.PathMatcherFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import red.jackf.jackfredlib.api.config.ConfigHandler;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class FileWatcher implements ModInitializer {
    private static final long INTERVAL_MILLIS = 1000L;
    private static final Map<Path, ConfigHandler<?>> watched = Collections.synchronizedMap(new HashMap<>());

    private static final Set<Path> skipNext = Collections.synchronizedSet(new HashSet<>());
    private static boolean setup = false;

    @Override
    public void onInitialize() {
        setupConfigWatcher();
    }

    public static void setupConfigWatcher() {
        if (setup) return;
        setup = true;
        JFLibConfig.LOGGER.info("Setting up file watchers");
        var filter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                new PathMatcherFileFilter(watched::containsKey)
        );

        var observer = new FileAlterationObserver(FabricLoader.getInstance().getConfigDir().toFile(), filter);

        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                Path path = file.toPath();
                if (!skipNext.remove(path)) {
                    ConfigHandler<?> handler = watched.get(path);
                    if (handler != null) handler.load();
                }
            }
        });

        var monitor = new FileAlterationMonitor(INTERVAL_MILLIS);
        monitor.setThreadFactory(run -> {
            Thread thread = new Thread(run);
            thread.setDaemon(true);
            thread.setName("JackFredLib: Config File Update Watcher");
            return thread;
        });
        monitor.addObserver(observer);

        try {
            monitor.start();
        } catch (Exception e) {
            JFLibConfig.LOGGER.error("Could not start config watcher; config file updates will not be read", e);
        }
    }

    public static void skip(Path path) {
        skipNext.add(path);
    }

    public static void enable(Path path, ConfigHandler<?> handler) {
        watched.put(path, handler);
    }

    public static void disable(Path path) {
        watched.remove(path);
    }
}
