package net.derfarmer.moduleloader.commands.annotations

import net.derfarmer.moduleloader.commands.provider.CommandSuggestionProvider
import net.derfarmer.moduleloader.commands.provider.EmptyCommandSuggestionProvider
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandArgument(
    val name: String,
    val suggestionProvider: KClass<out CommandSuggestionProvider> = EmptyCommandSuggestionProvider::class
) {
}