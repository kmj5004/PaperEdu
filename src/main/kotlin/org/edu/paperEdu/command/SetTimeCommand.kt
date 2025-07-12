package org.edu.paperEdu.command

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.edu.paperEdu.PaperEduPlugins

class SetTimeCommand(private val plugin: PaperEduPlugins): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("paperedu.setworld")) {
            sender.sendMessage("§c이 명령어를 사용할 권한이 없습니다.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(
                "§e사용법: §f/time set <값> [월드]\n" +
                        "§e       /time add <값> [월드]\n" +
                        "§e       /time query"
            )
            return true
        }

        when (args[0].lowercase()) {
            "set" -> {
                if (args.size < 2) {
                    sender.sendMessage("§e사용법: §f/time set <값> [월드]")
                    return true
                }
                handleSetTime(sender, args.getOrNull(2), args[1])
            }
            "add" -> {
                if (args.size < 2) {
                    sender.sendMessage("§e사용법: §f/time add <값> [월드]")
                    return true
                }
                handleAddTime(sender, args.getOrNull(2), args[1])
            }
            "query" -> handleQueryTime(sender)
            else -> sender.sendMessage("§c알 수 없는 서브 커맨드입니다. 'set', 'add', 'query'를 사용하세요.")
        }

        return true
    }

    private fun handleQueryTime(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.")
            return
        }

        val loginTime = plugin.loginSessions[sender.uniqueId]
        if (loginTime == null) {
            sender.sendMessage("§c접속 시간 정보를 찾을 수 없습니다. 재접속 후 다시 시도해주세요.")
            return
        }

        val durationMillis = System.currentTimeMillis() - loginTime
        val durationSeconds = durationMillis / 1000
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60

        sender.sendMessage("§b현재 세션 접속 시간: §e${minutes}분 ${seconds}초")
    }

    private fun getTargetWorld(sender: CommandSender, worldName: String?): World? {
        return if (worldName != null) {
            Bukkit.getWorld(worldName)
        } else if (sender is Player) {
            sender.world
        } else {
            null
        }
    }

    private fun handleSetTime(sender: CommandSender, worldName: String?, value: String) {
        val world = getTargetWorld(sender, worldName)
        if (world == null) {
            sender.sendMessage("§c월드를 찾을 수 없거나, 콘솔에서는 월드 이름을 지정해야 합니다.")
            return
        }
        val timeToSet = when (value.lowercase()) {
            "day", "낮" -> 1000L
            "midday", "정오" -> 6000L
            "night", "밤" -> 13000L
            "midnight", "자정" -> 18000L
            else -> value.toLongOrNull()
        }

        if (timeToSet == null) {
            sender.sendMessage("§c'§e${value}§c'는 올바른 시간 값(숫자, day, night 등)이 아닙니다.")
            return
        }

        world.time = timeToSet
        sender.sendMessage("§a성공! §f'§e${world.name}§f' 월드의 시간을 §b${timeToSet}§f (으)로 설정했습니다.")
    }

    private fun handleAddTime(sender: CommandSender, worldName: String?, value: String) {
        val world = getTargetWorld(sender, worldName)
        if (world == null) {
            sender.sendMessage("§c월드를 찾을 수 없거나, 콘솔에서는 월드 이름을 지정해야 합니다.")
            return
        }
        val timeToAdd = value.toLongOrNull()

        if (timeToAdd == null) {
            sender.sendMessage("§c'§e${value}§c'는 올바른 숫자 값이 아닙니다.")
            return
        }

        world.time += timeToAdd
        sender.sendMessage("§a성공! §f'§e${world.name}§f' 월드의 시간에 §b${timeToAdd}§f 만큼 추가했습니다. (현재: ${world.time})")
    }
}