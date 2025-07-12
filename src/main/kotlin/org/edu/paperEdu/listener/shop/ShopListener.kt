package org.edu.paperEdu.listener.shop

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.edu.paperEdu.PaperEduPlugins

class ShopListener(private val plugin: PaperEduPlugins) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!event.view.title.contains("판매 상점")) return
        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        val material = clickedItem.type

        val price = plugin.shopConfig.getDouble("items.${material.name}.price", 0.0)
        if (price <= 0) return

        if (player.inventory.contains(material)) {
            player.inventory.removeItem(ItemStack(material, 1))

            plugin.econ?.depositPlayer(player, price)

            player.sendMessage(MiniMessage.miniMessage().deserialize("<green><b>${material.name}</b> 아이템을 <gold>${price}원</gold>에 판매했습니다.</green>"))
            player.updateInventory()
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>판매할 아이템이 부족합니다.</red>"))
        }
    }
}