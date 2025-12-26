
package net.fallenflower.consolelogger;

import net.fallenflower.consolelogger.util.LocalizationHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.fallenflower.consolelogger.util.LocalizationHelper;
import org.apache.logging.log4j.Level;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LogToChatManager {
    private static final Set<Level> enabledLevels = ConcurrentHashMap.newKeySet();
    private static final Set<String> subscribedPlayers = ConcurrentHashMap.newKeySet();

    static {
        enabledLevels.add(Level.ERROR);
        enabledLevels.add(Level.WARN);
    }

    public static boolean isLevelEnabled(Level level) {
        return enabledLevels.contains(level);
    }

    public static void setLevelEnabled(Level level, boolean enabled) {
        if (enabled) {
            enabledLevels.add(level);
        } else {
            enabledLevels.remove(level);
        }
    }

    public static MutableComponent getEnabledLevelsComponent() {
        if (enabledLevels.isEmpty()) {
            return LocalizationHelper.getComponent("log.manager.no_levels_enabled")
                .withStyle(ChatFormatting.RED);
        }
        
        MutableComponent result = Component.literal("");
        boolean first = true;
        for (Level level : enabledLevels) {
            if (!first) {
                result.append(Component.literal(", ").withStyle(ChatFormatting.RESET));
            }
            result.append(
                LocalizationHelper.getComponent("log.level." + level.name().toLowerCase())
                    .withStyle(getLevelColor(level))
            );
            first = false;
        }
        return result;
    }

    public static void subscribePlayer(String playerName) {
        subscribedPlayers.add(playerName);
    }

    public static void unsubscribePlayer(String playerName) {
        subscribedPlayers.remove(playerName);
    }

    public static boolean isPlayerSubscribed(String playerName) {
        return subscribedPlayers.contains(playerName);
    }

    public static Set<String> getSubscribedPlayers() {
        return Set.copyOf(subscribedPlayers);
    }

    public static void processLogMessage(Level level, String formattedMessage) {
        if (!isLevelEnabled(level)) {
            return;
        }

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return; 
        }

        MutableComponent chatMessage = LocalizationHelper.getComponent("log.chat.prefix")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("[")
                .append(LocalizationHelper.getComponent("log.level." + level.name().toLowerCase())
                    .withStyle(getLevelColor(level), ChatFormatting.BOLD))
                .append("] "))
            .append(Component.literal(truncateLogMessage(formattedMessage))
                .withStyle(ChatFormatting.WHITE));

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.hasPermissions(2) && isPlayerSubscribed(player.getScoreboardName())) {
                player.sendSystemMessage(chatMessage);
            }
        }
    }

    private static ChatFormatting getLevelColor(Level level) {
        if (level.isMoreSpecificThan(Level.ERROR)) {
            return ChatFormatting.DARK_RED;
        } else if (level.isMoreSpecificThan(Level.WARN)) {
            return ChatFormatting.GOLD;
        } else if (level.isMoreSpecificThan(Level.INFO)) {
            return ChatFormatting.GREEN;
        } else {
            return ChatFormatting.GRAY;
        }
    }

    private static String truncateLogMessage(String message) {
        int maxLength = 150;
        if (message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength) + "...";
    }
}