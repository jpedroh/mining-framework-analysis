/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.tridentsdk.plugin.cmd;

import com.google.common.collect.Maps;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.plugin.PluginLoadException;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.plugin.annotation.CommandDescription;
import net.tridentsdk.util.TridentLogger;

import java.util.Map;

public class CommandHandler {
    // TODO: Make this a dictionary tree for fast lookup
    private static final Map<String, CommandData> COMMANDS = Factories.collect().createMap();

    public CommandHandler() {
        if (!Trident.isTrident())
            TridentLogger.error(new IllegalAccessException("Only TridentSDK is allowed to make a new command handler"));
    }

    /**
     * Handles the message sent, without the preceding "/"
     *
     * @param message the command executed
     */
    public void handleCommand(String message, CommandIssuer issuer) {
        if (message.isEmpty()) {
            return;
        }

        String[] contents = message.split(" ");
        String label = contents[0].toLowerCase();
        CommandData cmdData = COMMANDS.get(label);

        if (cmdData != null) {
            String args = message.substring(label.length());

            if (issuer instanceof Player) {
                cmdData.getCommand().handlePlayer((Player) issuer, args, contents[0]);
            } else if (issuer instanceof ServerConsole) {
                cmdData.getCommand().handleConsole((ServerConsole) issuer, args, contents[0]);
            }
            cmdData.getCommand().handle(issuer, args, contents[0]);

            for (Map.Entry<String, CommandData> entry : COMMANDS.entrySet()) {
                if (entry.getValue().hasAlias(label)) {
                    CommandData command = entry.getValue();
                    if (issuer instanceof Player) {
                        command.getCommand().handlePlayer((Player) issuer, args, contents[0]);
                    } else if (issuer instanceof ServerConsole) {
                        command.getCommand().handleConsole((ServerConsole) issuer, args, contents[0]);
                    }
                    command.getCommand().handle(issuer, args, contents[0]);
                }
            }
        } else {
            // Command not found
            issuer.sendRaw("Command not found");
        }
    }

    public int addCommand(TridentPlugin plugin, Command command) throws PluginLoadException {
        CommandDescription description = command.getClass().getAnnotation(CommandDescription.class);

        if (description == null) {
            TridentLogger.error(new PluginLoadException(
                    "Error in registering commands: Class does not have annotation " + "\"CommandDescription\"!"));
            return 0;
        }

        String name = description.name();
        int priority = description.priority();
        String[] aliases = description.aliases();
        String permission = description.permission();

        if (name == null || "".equals(name)) {
            TridentLogger.error(new PluginLoadException("cmd does not declare a valid name!"));
            return 0;
        }

        if (COMMANDS.containsKey(name.toLowerCase())) {
            if (COMMANDS.get(name.toLowerCase()).getPriority() > priority) {
                // put the new, more important cmd in place and notify the old cmd that it has been overridden
                COMMANDS.put(name.toLowerCase(), new CommandData(name, priority, aliases, permission, command, plugin))
                        .getCommand()
                        .notifyOverriden();
            } else {
                // don't register this cmd and notify it has been overridden
                command.notifyOverriden();
            }
        } else {
            COMMANDS.put(name, new CommandData(name, priority, aliases, permission, command, plugin));
        }

        // TODO: return something meaningful
        return 0;
    }

    public void removeCommand(Class<? extends Command> cls) {
        for (Map.Entry<String, CommandData> e : COMMANDS.entrySet()) {
            if (e.getValue().getCommand().getClass().equals(cls))
                COMMANDS.remove(e.getKey());
        }
    }

    public Map<Class<? extends Command>, Command> commandsFor(TridentPlugin plugin) {
        Map<Class<? extends Command>, Command> map = Maps.newHashMap();
        for (CommandData data : COMMANDS.values())
            if (data.plugin().equals(plugin))
                map.put(data.encapsulated.getClass(), data.encapsulated);
        return map;
    }

    private static class CommandData {
        private final String permission;
        private final int priority;
        private final String[] aliases;
        private final String name;
        private final Command encapsulated;
        private final TridentPlugin plugin;

        public CommandData(String name, int priority, String[] aliases, String permission, Command command,
                TridentPlugin plugin) {
            this.priority = priority;
            this.name = name;
            this.aliases = aliases;
            this.permission = permission;
            this.encapsulated = command;
            this.plugin = plugin;
        }

        public Command getCommand() {
            return this.encapsulated;
        }

        public boolean hasAlias(String alias) {
            for (String string : this.aliases) {
                if (alias.equalsIgnoreCase(string)) {
                    return true;
                }
            }
            return false;
        }

        public int getPriority() {
            return this.priority;
        }

        public TridentPlugin plugin() {
            return plugin;
        }
    }
}
