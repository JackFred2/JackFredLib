package red.jackf.jackfredlib.impl.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    public static Logger getLogger(String suffix) {
        return LoggerFactory.getLogger("JackFredLib" + (!suffix.isEmpty() ? "/" + suffix : ""));
    }
}
