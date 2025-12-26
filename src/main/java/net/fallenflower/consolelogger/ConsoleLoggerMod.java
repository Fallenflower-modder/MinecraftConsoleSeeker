
package net.fallenflower.consolelogger;

import net.fallenflower.consolelogger.util.LocalizationHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

@Mod(ConsoleLoggerMod.MOD_ID)
public class ConsoleLoggerMod {
    public static final String MOD_ID = "fallenflowers_console_logger";
    public static final Logger LOGGER = LogManager.getLogger();

    public ConsoleLoggerMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        setupChatAppender();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("[ConsoleLogger] 日志到聊天栏系统已加载。使用 /consoleseeker 命令控制。");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ConsoleSeekerCommand.register(event.getDispatcher());
        LOGGER.info("[ConsoleLogger] /consoleseeker 命令已注册。");
    }

    private void setupChatAppender() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        
        if (config.getAppender("ChatAppender") != null) {
            return;
        }
        
        ChatAppender appender = ChatAppender.createAppender(
                "ChatAppender",
                null,
                null
        );
        
        if (appender != null) {
            appender.start();
            config.addAppender(appender);
            config.getRootLogger().addAppender(appender, null, null);
            ctx.updateLoggers(config);
            LOGGER.info("[ConsoleLogger] ChatAppender 配置完成。");
        }
    }
}