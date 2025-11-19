package net.derfarmer.moduleloader.modules

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance

@Suppress("MemberVisibilityCanBePrivate")
object ModuleManager {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val modules = mutableListOf<Module>()
    val modulesDir = File(Bukkit.getPluginsFolder().parent, "modules")
    private val moduleFiles = hashMapOf<Module, File>()

    init {
        loadModulesFromFiles()
    }

    //<editor-fold desc="Load Plugins Jar for plugin dir and call loadPlugin() method">
    fun loadModulesFromFiles() {

        if (!modulesDir.exists()) modulesDir.mkdir()

        modulesDir.listFiles()?.forEach { file ->
            loadModuleFromFile(file)
        }
    }

    fun loadModuleFromFile(file: File) {

        val jarFile = JarFile(file)

        val configFile = jarFile.getJarEntry("plugin.json")
            ?: throw Exception("Plugin ${file.name} does not have a plugin.json file")

        val configJson = JsonParser().parse(
            jarFile.getInputStream(configFile).readAllBytes()
                .toString(Charsets.UTF_8)
        ).asJsonObject

        loadModule(configJson, file)

        jarFile.close()

    }

    //</editor-fold>

    fun loadModule(moduleConfig: JsonObject, file: File) {

        val classloader = URLClassLoader(arrayOf(file.toURI().toURL()), this.javaClass.classLoader)

        val moduleClass = classloader.loadClass(moduleConfig["main"].asString).asSubclass(Module::class.java).kotlin

        val module = moduleClass.objectInstance ?: moduleClass.createInstance()

        val nameField = module.javaClass.superclass.getDeclaredField("name")
        val authorsField = module.javaClass.superclass.getDeclaredField("authors")
        val loggerField = module.javaClass.superclass.getDeclaredField("logger")

        nameField.isAccessible = true
        authorsField.isAccessible = true
        loggerField.isAccessible = true

        nameField.set(module, moduleConfig["name"].asString)
        authorsField.set(module, moduleConfig["authors"].asJsonArray.map { it.asString }.toTypedArray())
        loggerField.set(module, LoggerFactory.getLogger(module.name))

        nameField.isAccessible = false
        authorsField.isAccessible = false
        loggerField.isAccessible = false

        modules.add(module)
        moduleFiles[module] = file

        module.logger.info("Enabling Module ${module.name}")

        module.onEnable()
    }

    fun reloadModule(plugin: Module) {

        plugin.unregisterAll()
        plugin.onReload()

        val file = moduleFiles[plugin]!!

        if (!file.exists()) {
            logger.error("File of \"${plugin.name}\" Plugin does not exist.")
            return
        }

        modules.remove(plugin)

        loadModuleFromFile(file)

    }

    fun disableModule(plugin: Module) {
        plugin.unregisterAll()
        plugin.onDisable()
        modules.remove(plugin)
    }

    fun disableAllModules() = modules.toMutableList().forEach { disableModule(it) }

    fun reloadAllModules() = modules.toMutableList().forEach { reloadModule(it) }

}