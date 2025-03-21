package ssh;

import ch.ethz.ssh2.ConnectionMonitor;
import com.testingbot.tunnel.App;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TestingBot
 */
public class CustomConnectionMonitor implements ConnectionMonitor {

    private final SSHTunnel tunnel;
    private final App app;
    private Timer timer;
    private boolean retrying = false;

    public CustomConnectionMonitor(SSHTunnel tunnel, App app) {
        this.tunnel = tunnel;
        this.app = app;
    }

    public void connectionLost(Throwable reason) {
        if (tunnel.isShuttingDown() == true) {
            return;
        }

        app.getHttpProxy().stop();

        Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SSH Connection lost! {0}", reason.getMessage());

        if (this.retrying == false) {
            this.retrying = true;
            timer = new Timer();
            timer.schedule(new PollTask(), 5000, 5000);
        }
    }

    class PollTask extends TimerTask {
        private int retryAttempts = 0;
        public void run() {
            try {
                Logger.getLogger(App.class.getName()).log(Level.INFO, "Trying to re-establish SSH Connection");
                retryAttempts += 1;
                tunnel.stop();
                tunnel.connect();

                if (tunnel.isAuthenticated()) {
                    retrying = false;
                    retryAttempts = 0;
                    timer.cancel();
                    app.getHttpProxy().start();
                    tunnel.createPortForwarding();
                    Logger.getLogger(App.class.getName()).log(Level.INFO, "Successfully re-established SSH Connection");
                    return;
                }
<<<<<<< /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/ssh/CustomConnectionMonitor.java/left.java
                Logger.getLogger(App.class.getName()).log(Level.INFO, "Attempts {0}", retryAttempts);
||||||| /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/ssh/CustomConnectionMonitor.java/base.java
=======
                Logger.getLogger(App.class.getName()).log(Level.INFO, "Attempts " + retryAttempts);
>>>>>>> /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/ssh/CustomConnectionMonitor.java/right.java
            } catch (Exception ex) {
                Logger.getLogger(App.class.getName()).log(Level.WARNING, ex.getMessage());
            }
            if (retryAttempts >= 3) {
                Logger.getLogger(App.class.getName()).log(Level.INFO, "Giving up retrying this Connection. Creating a new Tunnel Connection.");
                // give up connecting to this tunnel VM, try another one
                timer.cancel();
                retrying = false;
                retryAttempts = 0;
                app.stop();
                try {
                    app.boot();
                } catch (Exception ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
