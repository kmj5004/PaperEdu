package org.edu.paperEdu.command

import net.milkbowl.vault.economy.Economy
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CheckMoneyCommand(private val econ: Economy?): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            econ?.let { economy ->
                val balance = economy.getBalance(sender)
                sender.sendMessage("§e현재 잔액: §a${econ.format(balance)}")
            } ?: sender.sendMessage("경제 시스템을 사용할 수 없습니다.")
        } else {
            sender.sendMessage("§c플레이어만 사용할 수 있는 명령어입니다.")
        }
        return true
    }
}