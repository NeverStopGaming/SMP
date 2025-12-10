package net.derfarmer.moduleloader.modules

import com.github.shynixn.mccoroutine.folia.launch
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance

@Suppress("MemberVisibilityCanBePrivate")
object ModuleManager {

    val modules = mutableListOf<Module>()
    val modulesDir = File(Bukkit.getPluginsFolder().parent, "modules")
    private val moduleFiles = hashMapOf<Module, File>()

    val logger = LoggerFactory.getLogger(this::class.java)

    init {
        loadModulesFromFiles()
    }

    //<editor-fold desc="Load Plugins Jar for plugin dir and call loadPlugin() method">
    fun loadModulesFromFiles() {

        if (!modulesDir.exists()) modulesDir.mkdir()

        val moduleJarFiles = modulesDir.listFiles() ?: arrayOf()
        val moduleUrls = moduleJarFiles.map { it.toURI().toURL() }.toTypedArray()
        val sharedLoader = URLClassLoader(moduleUrls, this.javaClass.classLoader)

        moduleJarFiles.forEach {
            loadModuleFromFile(it, sharedLoader)
        }
    }

    fun loadModuleFromFile(file: File, classloader: ClassLoader) {

        val jarFile = JarFile(file)

        val configFile = jarFile.getJarEntry("plugin.json")
            ?: throw Exception("Plugin ${file.name} does not have a plugin.json file")

        val configJson = JsonParser().parse(
            jarFile.getInputStream(configFile).readAllBytes()
                .toString(Charsets.UTF_8)
        ).asJsonObject

        try {
            loadModule(configJson, file, classloader)
        } catch (e: Exception) {

            logger.error("Load failed on ${file.name}")
            e.printStackTrace()
        }

        jarFile.close()

    }

    //</editor-fold>

    fun loadModule(moduleConfig: JsonObject, file: File, classloader: ClassLoader) {

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

    fun disableModule(module: Module) {

        module.logger.info("Disabling Module ${module.name}")

        module.unregisterAll()
        module.onDisable()
        modules.remove(module)
    }

    fun disableAllModules() = modules.forEach { disableModule(it) }

    fun reloadModules() {
        modules.forEach {
            it.onReload()
            //disableModule(it)
        }

        loadModulesFromFiles()
    }
}