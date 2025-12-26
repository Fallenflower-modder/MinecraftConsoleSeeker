package net.fallenflower.consolelogger.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ConsoleLoggerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_LOG_LENGTH;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FILTER_CHAT_LOGS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FILTER_NETWORK_LOGS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FILTER_PACKET_LOGS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_TIMESTAMP;
    
    private static final int DEFAULT_MAX_LENGTH = 150;
    private static final boolean DEFAULT_FILTER_CHAT = true;
    private static final boolean DEFAULT_FILTER_NETWORK = false;
    private static final boolean DEFAULT_FILTER_PACKET = false;
    private static final boolean DEFAULT_ENABLE_TIMESTAMP = false;
    
    static {
        BUILDER.comment("ConsoleSeeker Mod Configuration")
               .push("General Settings");
        
        MAX_LOG_LENGTH = BUILDER
            .comment("Maximum length of log messages displayed in chat (default: 150)",
                     "Set to 0 to disable truncation",
                     "单条日志输出最大字数（默认：150）",
                     "设置为0以禁用字数限制")
            .defineInRange("maxLogLength", DEFAULT_MAX_LENGTH, 0, 1000);
        
        ENABLE_TIMESTAMP = BUILDER
            .comment("Whether to include timestamp in chat log messages (default: false)",
                     "是否在输出的日志消息中包含时间戳（默认：否）")
            .define("enableTimestamp", DEFAULT_ENABLE_TIMESTAMP);
        
        BUILDER.pop();
        BUILDER.push("Filter Settings");
        
        FILTER_CHAT_LOGS = BUILDER
            .comment("Filter out chat-related logs to prevent spam (default: true)",
                     "This filters logs from net.minecraft.client.gui.components.ChatComponent and similar",
                     "是否过滤聊天相关的日志以避免刷屏（默认：是）",
                     "会过滤来源像 net.minecraft.client.gui.components.ChatComponent 这样的日志")
            .define("filterChatLogs", DEFAULT_FILTER_CHAT);
        
        FILTER_NETWORK_LOGS = BUILDER
            .comment("Filter out network connection/disconnection logs (default: false)",
                     "是否过滤掉网络连接/断开日志（默认：否）")
            .define("filterNetworkLogs", DEFAULT_FILTER_NETWORK);
        
        FILTER_PACKET_LOGS = BUILDER
            .comment("Filter out packet sending/receiving logs (default: false)",
                     "是否过滤掉收发包日志（默认：否）")
            .define("filterPacketLogs", DEFAULT_FILTER_PACKET);
        
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    public static void register() {
        ModLoadingContext.get().registerConfig(Type.COMMON, SPEC, "consoleseeker-common.toml");
    }
    
    public static int getMaxLogLength() {
        return MAX_LOG_LENGTH.get();
    }
    
    public static boolean shouldFilterChatLogs() {
        return FILTER_CHAT_LOGS.get();
    }
    
    public static boolean shouldFilterNetworkLogs() {
        return FILTER_NETWORK_LOGS.get();
    }
    
    public static boolean shouldFilterPacketLogs() {
        return FILTER_PACKET_LOGS.get();
    }
    
    public static boolean shouldEnableTimestamp() {
        return ENABLE_TIMESTAMP.get();
    }

    public static void onConfigReload(){
        //Do nothing!
    }
}