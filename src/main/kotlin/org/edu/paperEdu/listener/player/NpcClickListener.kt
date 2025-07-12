package org.edu.paperEdu.listener.player

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.persistence.PersistentDataType
import org.edu.paperEdu.PaperEduPlugins

class NpcClickListener(private val plugin: PaperEduPlugins) : Listener {

    @EventHandler
    fun onNpcClick(event: PlayerInteractEntityEvent) {
        val player = event.player
        val clickedEntity = event.rightClicked

        // 1. NPC에게 부여했던 특별한 표식이 있는지 확인
        val key = NamespacedKey(plugin, "shop_npc")
        if (clickedEntity.persistentDataContainer.has(key, PersistentDataType.BYTE)) {
            // 2. 기본 주민 거래창이 열리는 것을 막음
            event.isCancelled = true

            // 3. 우리가 만든 상점 GUI를 열어줌
            plugin.openShopGUI(player)
        }
    }
}