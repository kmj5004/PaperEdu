package org.edu.paperEdu.listener.player

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.edu.paperEdu.PaperEduPlugins

class PlayerListener(private val plugin: PaperEduPlugins): Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        plugin.loginSessions[player.uniqueId] = System.currentTimeMillis()
        plugin.updatePlayerJoinData(player)

        val joinMessage = MiniMessage.miniMessage().deserialize("<yellow><b>${player.name}</b>님이 서버에 접속했습니다!</yellow>")
        event.joinMessage(joinMessage)

        player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>민재의 레전드 서버에 오신 것을 환영합니다! 즐거운 시간 보내세요.</aqua>"))

        plugin.econ?.let { economy ->
            try {
                if (plugin.isFirstJoin(player)) {
                    val depositResult = economy.depositPlayer(player, 100.0)
                    if (depositResult.transactionSuccess()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>첫 접속 보상으로 <gold>100원</gold>을 받았습니다! 환영합니다!</green>"))
                        plugin.setPlayerJoined(player)
                        plugin.logger.info("${player.name}에게 첫 접속 보상 100원을 지급했습니다.")
                    } else {
                        plugin.logger.warning("${player.name}에게 돈 지급 실패: ${depositResult.errorMessage}")
                    }
                } else {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>다시 접속해주셔서 감사합니다!</yellow>"))
                }

                val balance = economy.getBalance(player)
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>현재 소지금: <gold>${economy.format(balance)}</gold></gray>"))

            } catch (e: Exception) {
                plugin.logger.severe("경제 시스템 처리 중 오류 발생: ${e.message}")
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>경제 시스템에 문제가 발생했습니다. 관리자에게 문의하세요.</red>"))
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerJoinEvent) {
        val player = event.player

        plugin.loginSessions.remove(player.uniqueId)
    }
}