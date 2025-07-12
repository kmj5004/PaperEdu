package org.edu.paperEdu.listener

import org.bukkit.plugin.PluginManager
import org.edu.paperEdu.PaperEduPlugins
import org.edu.paperEdu.listener.player.NpcClickListener
import org.edu.paperEdu.listener.player.PlayerListener
import org.edu.paperEdu.listener.shop.ShopListener

class ListenerManager(private val plugin: PaperEduPlugins) {
    private val listeners = listOf(
        PlayerListener(plugin),
        ShopListener(plugin),
        NpcClickListener(plugin)
    )

    fun registerAll() {
        val pluginManager: PluginManager = plugin.server.pluginManager

        listeners.forEach { listener ->
            pluginManager.registerEvents(listener, plugin)
        }

        plugin.logger.info("${listeners.size}개의 리스너가 성공적으로 등록되었습니다.")
    }
}