package me.oriharel.machinery

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.oriharel.machinery.items.MachineItem
import me.oriharel.machinery.machine.Machine
import me.oriharel.machinery.utilities.Utils
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("machinery")
class MachineCommand(private val machinery: Machinery) : BaseCommand() {
    @Default
    @CommandPermission("machinery.help")
    fun help(executor: CommandSender) {
        executor.sendMessage("§e§lCOMMANDS:")
        executor.sendMessage("§b/machinery give [playerToGiveTo] [machineName] <amount>")
    }

    @Subcommand("give")
    inner class GiveCommand : BaseCommand() {
        // /machinery give machine playerName amount machineName
        @Subcommand("machine")
        @CommandCompletion("@players @range:1-10 @machines")
        @CommandPermission("machinery.give.machine")
        fun onMachineGive(executor: CommandSender, playerToGiveTo: OnlinePlayer, amount: Int, @Flags("other") machineName: Machine?) {
            if (machineName == null) {
                executor.sendMessage("§c§lInvalid machine name!")
                return
            } else if (amount < 1) {
                executor.sendMessage("§c§lAMount must be more than 0")
                return
            }
            val machineBlock = MachineItem(machineName.recipe, machineName, machinery.machineManager.machineFactory)
            val machineItem = machineBlock.itemStackWithAppliedPlaceholders
            machineItem!!.amount = amount
            if (!Utils.inventoryHasSpaceForItemAdd(playerToGiveTo.player.inventory)) {
                return
            }
            playerToGiveTo.player.inventory.addItem(machineItem)
        }

        // /machinery give fuel playerName amount energy
        @Subcommand("fuel")
        @CommandCompletion("@players @range:1-10 @range:20-50")
        @CommandPermission("machinery.give.fuel")
        fun onFuelGive(executor: CommandSender, playerToGiveTo: OnlinePlayer, amount: Int, energy: Int) {
            if (!fuelCommandBaseChecks(executor, energy, amount, playerToGiveTo.player)) return
            playerToGiveTo.player.inventory.addItem(machinery.fuelManager.getFuel(amount, energy))
        }

        // /machinery give fuel playerName material amount energy
        @Subcommand("fuel")
        @CommandCompletion("@players @range:1-10 @range:20-50")
        @CommandPermission("machinery.give.fuel")
        fun onFuelGive(executor: CommandSender, playerToGiveTo: OnlinePlayer, material: Material?, amount: Int, energy: Int) {
            if (!fuelCommandBaseChecks(executor, energy, amount, playerToGiveTo.player)) return
            playerToGiveTo.player.inventory.addItem(machinery.fuelManager.getFuel(material, amount, energy))
        }

        /**
         * helper function to check if fuel given is valid fuel syntax
         * @param commandSender sender of command
         * @param energy energy to give
         * @param amount amount to give
         * @param giveTo who to give to
         * @return if you can give the player fuel or not. true if yes.
         */
        private fun fuelCommandBaseChecks(commandSender: CommandSender, energy: Int, amount: Int, giveTo: Player): Boolean {
            if (energy < 1) {
                commandSender.sendMessage("§c§lMost enter energy above 0!")
                return false
            } else if (amount < 1) {
                commandSender.sendMessage("§c§lAmount must be more than 0!")
                return false
            }
            return Utils.inventoryHasSpaceForItemAdd(giveTo.inventory)
        }
    }

}