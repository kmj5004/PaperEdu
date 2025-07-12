package org.edu.paperEdu.command

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetDifficultyCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("paperedu.setworld")) {
            sender.sendMessage("§c이 명령어를 사용할 권한이 없습니다.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§e사용법: §f/setdifficulty <difficulty> [world]")
            return true
        }

        val difficulty = when (args[0].lowercase()) {
            "peaceful", "평화로움" -> Difficulty.PEACEFUL
            "easy", "쉬움" -> Difficulty.EASY
            "normal", "보통" -> Difficulty.NORMAL
            "hard", "어려움" -> Difficulty.HARD
            else -> {
                sender.sendMessage("§c알 수 없는 난이도입니다: §e${args[0]}")
                return true
            }
        }

        val targetWorld: World? = if (args.size > 1) {
            Bukkit.getWorld(args[1])
        } else if (sender is Player) {
            sender.world
        } else {
            sender.sendMessage("§c콘솔에서는 월드 이름을 반드시 지정해야 합니다.")
            return true
        }

        if (targetWorld == null) {
            sender.sendMessage("§c'§e${args.getOrNull(1)}§c' 월드를 찾을 수 없습니다.")
            return true
        }

        targetWorld.difficulty = difficulty
        sender.sendMessage("§a성공! §f월드 '§e${targetWorld.name}§f'의 난이도를 §b${difficulty.name}§f (으)로 설정했습니다.")

        return true
    }
}