package me.oriharel.machinery.data

import com.google.gson.Gson
import me.oriharel.machinery.machines.MachineFactory
import me.oriharel.machinery.machines.machine.PlayerMachine
import me.oriharel.machinery.utilities.Utils
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

class PlayerMachinePersistentDataType(factory: MachineFactory?) : PersistentDataType<String, PlayerMachine> {
    private val gson: Gson?
    override fun getPrimitiveType(): Class<String> {
        return String::class.java
    }

    override fun getComplexType(): Class<PlayerMachine> {
        return PlayerMachine::class.java
    }

    override fun toPrimitive(playerMachine: PlayerMachine, persistentDataAdapterContext: PersistentDataAdapterContext): String {
        return gson!!.toJson(playerMachine, PlayerMachine::class.java)
    }

    override fun fromPrimitive(string: String, persistentDataAdapterContext: PersistentDataAdapterContext): PlayerMachine {
        return gson!!.fromJson(string, PlayerMachine::class.java)
    }

    init {
        gson = Utils.getGsonSerializationBuilderInstance(PlayerMachine::class.java, factory)
    }
}