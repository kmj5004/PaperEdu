package org.edu.paperEdu.command

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetGameModCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("paperedu.setworld")) {
            sender.sendMessage("§c이 명령어를 사용할 권한이 없습니다.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§e사용법: §f/setgamemod <mode> [player]")
            return true
        }

        val mod = when (args[0].lowercase()) {
            "survival" -> GameMode.SURVIVAL
            "creative" -> GameMode.CREATIVE
            "adventure" -> GameMode.ADVENTURE
            "spectator" -> GameMode.SPECTATOR
            else -> {
                sender.sendMessage("§c알 수 없는 게임모드입니다: §e${args[0]}")
                return true
            }
        }

        val targetPlayer: Player? = if (args.size > 1) {
            Bukkit.getPlayer(args[1])
        } else if (sender is Player) {
            sender
        } else {
            sender.sendMessage("§c콘솔에서는 대상 플레이어 이름을 반드시 지정해야 합니다.")
            return true
        }

        if (targetPlayer == null) {
            sender.sendMessage("§c플레이어 '§e${args[1]}§c'를 찾을 수 없거나 오프라인 상태입니다.")
            return true
        }

        targetPlayer.gameMode = mod

        sender.sendMessage("§a성공! §f${targetPlayer.name}님의 게임모드를 §e${mod.name}§f (으)로 변경했습니다.")
        if (sender != targetPlayer) {
            targetPlayer.sendMessage("§a당신의 게임모드가 §e${mod.name}§a (으)로 변경되었습니다.")
        }

        return true
    }
}