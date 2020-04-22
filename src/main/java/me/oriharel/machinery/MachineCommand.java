package me.oriharel.machinery;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.command.CommandSender;
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

    // /machinery give playerName machineName amount
    @Subcommand("give")
    @CommandCompletion("@allplayers:30 @machines @range:1-10")
    @CommandPermission("machinery.givemachine")
    public void onMachineGive(Player executor, OnlinePlayer playerToGiveTo, int amount, @Flags("other") Machine machine) {
        if (machine == null) {
          executor.sendMessage("§c§lInvalid machine name!");
        } else if (amount < 1) {
            executor.sendMessage("§c§lAMount must be more than 0");
            return;
        }
        MachineBlock machineBlock = new MachineBlock(machine.getRecipe(), machine, machinery.getMachineManager().getMachineFactory());
        ItemStack machineItem = machineBlock.getItemStackWithAppliedPlaceholders();
        machineItem.setAmount(amount);
        if (!Utils.inventoryHasSpaceForItemAdd(playerToGiveTo.player.getInventory(), machineItem)) {
            return;
        }
        playerToGiveTo.player.getInventory().addItem(machineItem);
    }
}
