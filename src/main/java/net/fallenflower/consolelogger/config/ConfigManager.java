package net.fallenflower.consolelogger.config;

import net.fallenflower.consolelogger.ConsoleLoggerMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ConsoleLoggerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {
    
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        ConsoleLoggerMod.LOGGER.info("Loading ConsoleSeeker configuration...");
        ConsoleLoggerConfig.onConfigReload();
    }
    
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        ConsoleLoggerMod.LOGGER.info("ConsoleSeeker configuration reloaded");
        ConsoleLoggerConfig.onConfigReload();
    }
}