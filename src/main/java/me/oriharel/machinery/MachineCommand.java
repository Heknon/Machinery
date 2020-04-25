package me.oriharel.machinery;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("machinery")
public class MachineCommand extends BaseCommand {

    private Machinery machinery;

    public MachineCommand(Machinery machinery) {
        this.machinery = machinery;
    }

    @Default
    @CommandPermission("machinery.help")
    public void help(CommandSender executor) {
        executor.sendMessage("§e§lCOMMANDS:");
        executor.sendMessage("§b/machinery give [playerToGiveTo] [machineName] <amount>");
    }

    @Subcommand("give")
    public class GiveCommand extends BaseCommand {

        // /machinery give machine playerName amount machineName
        @Subcommand("machine")
        @CommandCompletion("@players @range:1-10 @machines")
        @CommandPermission("machinery.give.machine")
        public void onMachineGive(CommandSender executor, OnlinePlayer playerToGiveTo, int amount, @Flags("other") Machine machineName) {
            if (machineName == null) {
                executor.sendMessage("§c§lInvalid machine name!");
                return;
            } else if (amount < 1) {
                executor.sendMessage("§c§lAMount must be more than 0");
                return;
            }
            MachineBlock machineBlock = new MachineBlock(machineName.getRecipe(), machineName, machinery.getMachineManager().getMachineFactory());
            ItemStack machineItem = machineBlock.getItemStackWithAppliedPlaceholders();
            machineItem.setAmount(amount);
            if (!Utils.inventoryHasSpaceForItemAdd(playerToGiveTo.player.getInventory())) {
                return;
            }
            playerToGiveTo.player.getInventory().addItem(machineItem);
        }

        // /machinery give fuel playerName amount energy
        @Subcommand("fuel")
        @CommandCompletion("@players @range:1-10 @range:20-50")
        @CommandPermission("machinery.give.fuel")
        public void onFuelGive(CommandSender executor, OnlinePlayer playerToGiveTo, int amount, int energy) {
            if (!fuelCommandBaseChecks(executor, energy, amount, playerToGiveTo.player)) return;

            playerToGiveTo.player.getInventory().addItem(machinery.getFuelManager().getFuel(amount, energy));
        }

        // /machinery give fuel playerName material amount energy
        @Subcommand("fuel")
        @CommandCompletion("@players @range:1-10 @range:20-50")
        @CommandPermission("machinery.give.fuel")
        public void onFuelGive(CommandSender executor, OnlinePlayer playerToGiveTo, Material material, int amount, int energy) {
            if (!fuelCommandBaseChecks(executor, energy, amount, playerToGiveTo.player)) return;

            playerToGiveTo.player.getInventory().addItem(machinery.getFuelManager().getFuel(material, amount, energy));
        }

        private boolean fuelCommandBaseChecks(CommandSender commandSender, int energy, int amount, Player giveTo) {
            if (energy < 1) {
                commandSender.sendMessage("§c§lMost enter energy above 0!");
                return false;
            } else if (amount < 1) {
                commandSender.sendMessage("§c§lAmount must be more than 0!");
                return false;
            }

            return Utils.inventoryHasSpaceForItemAdd(giveTo.getInventory());
        }

    }

}
