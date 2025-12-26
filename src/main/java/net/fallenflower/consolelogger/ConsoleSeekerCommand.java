
package net.fallenflower.consolelogger;

import net.fallenflower.consolelogger.util.LocalizationHelper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.fallenflower.consolelogger.util.LocalizationHelper;
import org.apache.logging.log4j.Level;

public class ConsoleSeekerCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("consoleseeker")
                .requires(source -> source.hasPermission(2)) 
                .then(Commands.argument("level", StringArgumentType.word())
                        .suggests((context, builder) -> 
                            SharedSuggestionProvider.suggest(new String[]{"info", "warn", "error"}, builder))
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests((context, builder) -> 
                                    SharedSuggestionProvider.suggest(new String[]{"on", "off"}, builder))
                                .executes(ConsoleSeekerCommand::executeCommand)))
                .executes(context -> {
                    sendStatus(context.getSource());
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static int executeCommand(CommandContext<CommandSourceStack> context) {
        String levelStr = StringArgumentType.getString(context, "level").toUpperCase();
        String action = StringArgumentType.getString(context, "action").toLowerCase();
        
        Level level = null;
        if ("INFO".equals(levelStr)) {
            level = Level.INFO;
        } else if ("WARN".equals(levelStr)) {
            level = Level.WARN;
        } else if ("ERROR".equals(levelStr)) {
            level = Level.ERROR;
        }
        
        if (level == null) {
            context.getSource().sendFailure(
                LocalizationHelper.getComponent("command.consoleseeker.invalid_level")
            );
            return 0;
        }
        
        if (!"on".equals(action) && !"off".equals(action)) {
            context.getSource().sendFailure(
                Component.literal("操作参数必须为 'on' 或 'off'").withStyle(ChatFormatting.RED)
            );
            return 0;
        }
        
        boolean enable = "on".equals(action);
        LogToChatManager.setLevelEnabled(level, enable);
        
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            String playerName = player.getScoreboardName();
            if (enable) {
                LogToChatManager.subscribePlayer(playerName);
            } else {
                boolean hasOtherEnabledLevels = false;
                for (Level lvl : new Level[]{Level.INFO, Level.WARN, Level.ERROR}) {
                    if (lvl != level && LogToChatManager.isLevelEnabled(lvl)) {
                        hasOtherEnabledLevels = true;
                        break;
                    }
                }
                if (!hasOtherEnabledLevels) {
                    LogToChatManager.unsubscribePlayer(playerName);
                }
            }
        }
        
        MutableComponent levelDisplay = LocalizationHelper.getComponent("log.level." + levelStr.toLowerCase())
            .withStyle(getTextColor(level));
        
        MutableComponent actionDisplay = LocalizationHelper.getComponent(
            enable ? "message.toggle.on" : "message.toggle.off"
        ).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED);
        
        MutableComponent statusDisplay = LogToChatManager.getEnabledLevelsComponent();
        
        MutableComponent feedback = LocalizationHelper.getComponent(
            "command.consoleseeker.success",
            levelDisplay,
            actionDisplay,
            statusDisplay
        );
        
        context.getSource().sendSuccess(() -> feedback, true);
        return Command.SINGLE_SUCCESS;
    }

    private static void sendStatus(CommandSourceStack source) {
        MutableComponent title = LocalizationHelper.getComponent("command.consoleseeker.status.title")
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        
        MutableComponent enabledLevels = LocalizationHelper.getComponent(
            "command.consoleseeker.status.enabled_levels",
            LogToChatManager.getEnabledLevelsComponent()
        ).withStyle(ChatFormatting.GRAY);
        
        String subscriptionStatusKey;
        if (source.getEntity() instanceof ServerPlayer player) {
            subscriptionStatusKey = LogToChatManager.isPlayerSubscribed(player.getScoreboardName()) ?
                "command.consoleseeker.status.subscribed" :
                "command.consoleseeker.status.unsubscribed";
        } else {
            subscriptionStatusKey = "command.consoleseeker.status.unsubscribed";
        }
        
        MutableComponent subscriptionStatus = LocalizationHelper.getComponent(subscriptionStatusKey)
            .withStyle(ChatFormatting.YELLOW);
        
        MutableComponent subscription = LocalizationHelper.getComponent(
            "command.consoleseeker.status.subscription",
            subscriptionStatus
        ).withStyle(ChatFormatting.GRAY);
        
        MutableComponent status = Component.literal("")
            .append(title)
            .append(Component.literal("\n"))
            .append(enabledLevels)
            .append(Component.literal("\n"))
            .append(subscription);
        
        source.sendSuccess(() -> status, false);
    }

    private static ChatFormatting getTextColor(Level level) {
        if (level == null) return ChatFormatting.GRAY;
        
        switch (level.getStandardLevel()) {
            case ERROR: return ChatFormatting.DARK_RED;
            case WARN: return ChatFormatting.GOLD;
            case INFO: return ChatFormatting.GREEN;
            default: return ChatFormatting.GRAY;
        }
    }
}