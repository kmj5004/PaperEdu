package org.edu.paperEdu.command

import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.persistence.PersistentDataType
import org.edu.paperEdu.PaperEduPlugins

class CreateNpcCommand(private val plugin: PaperEduPlugins) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.")
            return true
        }

        // 1. NPC 스폰
        val npc = sender.world.spawnEntity(sender.location, EntityType.VILLAGER) as Villager

        // 2. NPC 설정
        npc.customName(Component.text("§a[상인] §f민재"))
        npc.isCustomNameVisible = true
        npc.setAI(false) // 움직이지 않도록 AI 비활성화
        npc.isInvulnerable = true // 무적 설정
        npc.setRemoveWhenFarAway(false) // 멀리 가도 사라지지 않음
        npc.profession = Villager.Profession.LIBRARIAN // 직업 설정 (선택사항)

        // 3. NPC에게 특별한 표식(Tag) 부여 (가장 중요)
        val key = NamespacedKey(plugin, "shop_npc")
        npc.persistentDataContainer.set(key, PersistentDataType.BYTE, 1)

        sender.sendMessage("§a성공! §e상인 NPC를 현재 위치에 생성했습니다.")
        return true
    }
}