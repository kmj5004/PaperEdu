package org.edu.paperEdu

import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.edu.paperEdu.listener.ListenerManager
import org.edu.paperEdu.command.CheckMoneyCommand
import org.edu.paperEdu.command.CreateNpcCommand
import org.edu.paperEdu.command.SetDifficultyCommand
import org.edu.paperEdu.command.SetGameModCommand
import org.edu.paperEdu.command.SetTimeCommand
import org.edu.paperEdu.command.ShopCommand
import java.io.File
import java.util.UUID

class PaperEduPlugins : JavaPlugin(), Listener {
    var econ: Economy? = null
        private set

    private lateinit var playerDataFile: File
    private lateinit var playerDataConfig: FileConfiguration
    val loginSessions = mutableMapOf<UUID, Long>()
    lateinit var shopConfigFile: File
    lateinit var shopConfig: FileConfiguration


    override fun onEnable() {
        setupPlayerData()
        createShopConfig()

        if (!setupEconomy()) {
            logger.severe("Vault 또는 경제 플러그인을 찾을 수 없어 경제 기능을 비활성화합니다.")
            server.pluginManager.disablePlugin(this)
            return
        }

        ListenerManager(this).registerAll()

        getCommand("difficulty")?.setExecutor(SetDifficultyCommand())
        econ?.let {
            getCommand("money")?.setExecutor(CheckMoneyCommand(econ))
        }
        getCommand("gamemod")?.setExecutor(SetGameModCommand())
        getCommand("time")?.setExecutor(SetTimeCommand(this))
        getCommand("shop")?.setExecutor(ShopCommand(this))
        getCommand("createshopnpc")?.setExecutor(CreateNpcCommand(this))

        logger.info("Hello Paper!")
    }

    private fun createShopConfig() {
        shopConfigFile = File(dataFolder, "shop.yml")
        if (!shopConfigFile.exists()) {
            saveResource("shop.yml", false)
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopConfigFile)
    }

    override fun onDisable() {
        logger.info("Goodbye Paper!")
    }

    private fun setupPlayerData() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        playerDataFile = File(dataFolder, "playerdata.yml")
        if (!playerDataFile.exists()) {
            playerDataFile.createNewFile()
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile)
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            logger.warning("Vault 플러그인이 없습니다!")
            return false
        }

        val rsp: RegisteredServiceProvider<Economy>? = server.servicesManager.getRegistration(Economy::class.java)
        if (rsp == null) {
            logger.warning("경제 플러그인이 없습니다!")
            return false
        }

        econ = rsp.provider
        logger.info("경제 시스템이 성공적으로 연결되었습니다: ${econ?.name}")
        return true
    }

    fun isFirstJoin(player: Player): Boolean {
        return !playerDataConfig.contains("players.${player.uniqueId}")
    }

    fun setPlayerJoined(player: Player) {
        playerDataConfig.set("players.${player.uniqueId}.name", player.name)
        playerDataConfig.set("players.${player.uniqueId}.first_join", System.currentTimeMillis())
        savePlayerData()
    }

    fun updatePlayerJoinData(player: Player) {
        val playerPath = "players.${player.uniqueId}"
        if (!playerDataConfig.contains("$playerPath.first_join")) {
            playerDataConfig.set("$playerPath.first_join", System.currentTimeMillis())
        }
        playerDataConfig.set("$playerPath.name", player.name)
        playerDataConfig.set("$playerPath.last_join", System.currentTimeMillis())
        savePlayerData()
    }


    private fun savePlayerData() {
        try {
            playerDataConfig.save(playerDataFile)
        } catch (e: Exception) {
            logger.severe("플레이어 데이터 저장 중 오류: ${e.message}")
        }
    }

    fun openShopGUI(player: Player) {
        val gui: Inventory = Bukkit.createInventory(null, 27, MiniMessage.miniMessage().deserialize("<dark_green><b>판매 상점</b></dark_green>"))
        val shopConfig = this.shopConfig

        shopConfig.getConfigurationSection("items")?.getKeys(false)?.forEachIndexed { index, key ->
            val material = Material.getMaterial(key)
            if (material != null) {
                val item = ItemStack(material)
                val meta = item.itemMeta
                val price = shopConfig.getDouble("items.$key.price")
                val displayName = shopConfig.getString("items.$key.display-name") ?: key
                meta.displayName(MiniMessage.miniMessage().deserialize(displayName))
                val loreLines = shopConfig.getStringList("items.$key.lore").map { line ->
                    MiniMessage.miniMessage().deserialize(line.replace("%price%", price.toString()))
                }
                meta.lore(loreLines)
                item.itemMeta = meta
                gui.setItem(index, item)
            }
        }
        player.openInventory(gui)
    }
}