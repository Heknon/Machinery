package me.oriharel.machinery.inventory

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

open class Inventory
/**
 * Construct an inventory.
 *
 * @param routes must have a route named start as it will be the player's initial route
 * @param viewer the inventory viewer
 */
(private var routes: MutableMap<String?, InventoryPage>, private var viewer: Player) : InventoryHolder {
    private var currentPage: InventoryPage? = null
    fun start() {
        viewer.openInventory(inventory)
    }

    fun addRoute(name: String?, page: InventoryPage) {
        routes[name] = page
    }

    @JvmOverloads
    fun <T> navigateToNamedRouteData(name: String?, data: T? = null) {
        val page = routes[name]
        navigateDirect(page, data)
    }

    fun navigateToNamedRoute(name: String?) {
        val page = routes[name]
        navigateDirect(page, null)
    }

    fun <T> navigateDirect(page: InventoryPage?, data: T?) {
        if (data != null && page is NavigableDataPage<*>) {
            val tNavigableDataInventoryPage = page as NavigableDataPage<T?>
            tNavigableDataInventoryPage.storedData = data
            tNavigableDataInventoryPage.injector(data, tNavigableDataInventoryPage)
        }
        viewer.openInventory(page!!.inventory)
        currentPage = page
    }

    override fun getInventory(): Inventory {
        val page = routes["start"]
        currentPage = page
        return page!!.inventory
    }

}