/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.enderstone.server.commands.vanila;

import java.util.List;

import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.player.EnderPlayer;

public class TeleportCommand extends SimpleCommand {

	public TeleportCommand() {
		super("command.enderstone.teleport", "teleport", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "tp");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {

		if (args.length == 1) {
			if (sender instanceof EnderPlayer) {
				EnderPlayer ep = (EnderPlayer) sender;
				EnderPlayer toEp = Main.getInstance().getPlayer(args[0]);
				if (toEp == null) {
					sender.sendMessage(new SimpleMessage("Player " + args[0] + " not found."));
					return Command.COMMAND_FAILED;
				}
				ep.teleport(toEp);
			} else {
				sender.sendMessage(new SimpleMessage("You can only use the command this way if you are a player."));
				return Command.COMMAND_FAILED;
			}
		} else if (args.length == 2) {
			EnderPlayer ep = Main.getInstance().getPlayer(args[0]);
			EnderPlayer toEp = Main.getInstance().getPlayer(args[1]);

			if (ep == null) {
				sender.sendMessage(new SimpleMessage("Player " + args[0] + " not found."));
				return Command.COMMAND_FAILED;
			}
			if (toEp == null) {
				sender.sendMessage(new SimpleMessage("Player " + args[1] + " not found."));
				return Command.COMMAND_FAILED;
			}
			ep.teleport(toEp);
		} else if (args.length == 3) {
			if (sender instanceof EnderPlayer) {
				EnderPlayer ep = (EnderPlayer) sender;

				for (int i = 0; i < args.length; i++) {
					try {
						Integer.parseInt(args[i]);
					} catch (NumberFormatException e) {
						sender.sendMessage(new SimpleMessage(args[i] + " is not a valid number."));
						return Command.COMMAND_FAILED;
					}
				}

				Location toLocation = new Location(ep.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), 0F, 0F);
				ep.teleport(toLocation);
			} else {
				sender.sendMessage(new SimpleMessage("You can only use the command this way if you are a player."));
				return Command.COMMAND_FAILED;
			}
		} else {
			sender.sendMessage(new SimpleMessage("Correct usage: /tp <someone> <tosomeone>"));
			sender.sendMessage(new SimpleMessage("Or: /tp <tosomeone>"));
			return Command.COMMAND_FAILED;
		}
		return Command.COMMAND_SUCCES;
	}

	@Override
	public List<String> executeTabList(Command cmd, String alias, CommandSender sender, String[] args) {
		if (args.length == 0)
			return calculateMissingArgumentsPlayer("", sender);
		return calculateMissingArgumentsPlayer(args[args.length - 1], sender);
	}

}
