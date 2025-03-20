package com.googlecode.greysanatomy.console.command;
import com.googlecode.greysanatomy.console.command.annotation.RiscCmd;
import com.googlecode.greysanatomy.console.server.ConsoleServer;
import com.googlecode.greysanatomy.util.GaStringUtils;

/**
 * ����汾
 *
 * @author vlinux
 */
@RiscCmd(named = "version", sort = 8, desc = "Output the target's greys version",
        eg = {
                "version"
        })
@RiscCmd(named = "version", sort = 8, desc = "Output the target's greys version")
public class VersionCommand extends Command {

    @Override
    public Action getAction() {
        return new Action() {

            @Override
            public void action(final ConsoleServer consoleServer, final Info info, final Sender sender) throws Throwable {
                sender.send(true, GaStringUtils.getLogo());
            }

        };
    }

}
