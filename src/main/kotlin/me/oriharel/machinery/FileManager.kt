package me.oriharel.machinery

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

class FileManager(private val plugin: JavaPlugin) {
    private val configs = HashMap<String, Config>()

    /**
     * Get the config by the name(Don't forget the .yml)
     *
     * @param name config name to get
     * @return this
     */
    fun getConfig(name: String): Config? {
        if (!configs.containsKey(name)) configs[name] = Config(name)
        return configs[name]
    }

    /**
     * Save the config by the name(Don't forget the .yml)
     *
     * @param name config name to save
     * @return this
     */
    fun saveConfig(name: String): Config {
        return getConfig(name)!!.save()
    }

    /**
     * Reload the config by the name(Don't forget the .yml)
     *
     * @param name config name to reload
     * @return this
     */
    fun reloadConfig(name: String): Config {
        return getConfig(name)!!.reload()
    }

    inner class Config(private val name: String) {
        private var file: File? = null
        private var config: YamlConfiguration? = null

        /**
         * Saves the config as long as the config isn't empty
         *
         * @return this
         */
        fun save(): Config {
            if (config == null || file == null) return this
            try {
                if (config!!.getConfigurationSection("")!!.getKeys(true).size != 0) config!!.save(file!!)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return this
        }

        /**
         * Used for when initializing a config into the data folder of the plugin.
         * Checks if the file exists already. If it doesn't it force copies the default values and saves.
         * If it does, it doesn't forcefully copy the default values.
         * @return this
         */
        fun initialize(): Config {
            val file = plugin.dataFolder.toPath().resolve(name)
            if (!Files.exists(file)) {
                copyDefaults(true).save()
            } else {
                copyDefaults(false).save()
            }
            return this
        }

        /**
         * Gets the config as a YamlConfiguration
         *
         * @return this
         */
        fun get(): YamlConfiguration? {
            if (config == null) reload()
            return config
        }

        /**
         * Saves the default config(Will overwrite anything in the current config's file)
         *
         *
         * Don't forget to reload after!
         *
         * @return this
         */
        fun saveDefaultConfig(): Config {
            file = File(plugin.dataFolder, name)
            plugin.saveResource(name, false)
            return this
        }

        /**
         * Reloads the config
         *
         * @return this
         */
        fun reload(): Config {
            if (file == null) file = File(plugin.dataFolder, name)
            config = YamlConfiguration.loadConfiguration(file!!)
            val defConfigStream: Reader
            try {
                defConfigStream = InputStreamReader(plugin.getResource(name), StandardCharsets.UTF_8)
                val defConfig = YamlConfiguration.loadConfiguration(defConfigStream)
                config!!.setDefaults(defConfig)
            } catch (ignored: NullPointerException) {
            }
            return this
        }

        /**
         * Copies the config from the resources to the config's default settings.
         *
         *
         * Force = true ----> Will add any new values from the default file
         *
         *
         * Force = false ---> Will NOT add new values from the default file
         *
         * @param force this
         * @return this
         */
        fun copyDefaults(force: Boolean): Config {
            get()!!.options().copyDefaults(force)
            return this
        }

        /**
         * An easy way to set a value into the config
         *
         * @param key config key
         * @param value value to set
         * @return this
         */
        operator fun set(key: String?, value: Any?): Config {
            get()!![key!!] = value
            return this
        }

        /**
         * An easy way to get a value from the config
         *
         * @param key config key
         * @return this
         */
        operator fun get(key: String?): Any {
            return get()!![key!!]!!
        }

    }

}