package net.fallenflower.consolelogger.util;

import net.fallenflower.consolelogger.ConsoleLoggerMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LocalizationHelper {
    
    public static MutableComponent getComponent(String key, Object... args) {
        return Component.translatable(ConsoleLoggerMod.MOD_ID + "." + key, args);
    }
    
    public static String getClientString(String key, Object... args) {
        try {
            Class<?> i18nClass = Class.forName("net.minecraft.client.resources.language.I18n");
            var getMethod = i18nClass.getMethod("get", String.class, Object[].class);
            return (String) getMethod.invoke(null, ConsoleLoggerMod.MOD_ID + "." + key, args);
        } catch (Exception e) {
            return ConsoleLoggerMod.MOD_ID + "." + key;
        }
    }
}