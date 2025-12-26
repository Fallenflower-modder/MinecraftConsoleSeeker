package net.fallenflower.consolelogger.util;

import net.fallenflower.consolelogger.config.ConsoleLoggerConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;

public class LogFilter {
    
    private static final String[] CHAT_LOGGER_KEYWORDS = {
        "chat", "Chat", "CHAT",
        "gui.components.ChatComponent",
        "net.minecraft.client.gui"
    };
    
    private static final String[] NETWORK_LOGGER_KEYWORDS = {
        "network", "Network", "NETWORK",
        "connection", "Connection",
        "net.minecraft.network",
        "handshake", "login", "status"
    };
    
    private static final String[] PACKET_LOGGER_KEYWORDS = {
        "packet", "Packet", "PACKET",
        "send", "Send", "SEND",
        "receive", "Receive", "RECEIVE"
    };
    
    private static final String[] CHAT_MESSAGE_KEYWORDS = {
        "said:", "说：", "whispers:", "whispered:", "to",
        "msg", "tell", "w", "me", "actionbar"
    };
    
    public static boolean shouldFilter(LogEvent event) {
        String loggerName = event.getLoggerName();
        String message = event.getMessage().getFormattedMessage();
        Level level = event.getLevel();
        
        if (level.isMoreSpecificThan(Level.INFO)) {
            return false;
        }
        
        if (ConsoleLoggerConfig.shouldFilterChatLogs() && isChatLog(loggerName, message)) {
            return true;
        }
        
        if (ConsoleLoggerConfig.shouldFilterNetworkLogs() && isNetworkLog(loggerName, message)) {
            return true;
        }
        
        if (ConsoleLoggerConfig.shouldFilterPacketLogs() && isPacketLog(loggerName, message)) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isChatLog(String loggerName, String message) {
        for (String keyword : CHAT_LOGGER_KEYWORDS) {
            if (loggerName.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        String lowerMessage = message.toLowerCase();
        for (String keyword : CHAT_MESSAGE_KEYWORDS) {
            if (lowerMessage.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        if (message.matches(".*<.*>.*") || 
            message.matches(".*\\[.*\\].*:.*") ||
            message.matches(".*→.*:.*") ||
            message.contains("whispered") ||
            message.contains("shouted") ||
            message.contains("broadcast")) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isNetworkLog(String loggerName, String message) {
        for (String keyword : NETWORK_LOGGER_KEYWORDS) {
            if (loggerName.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("lost connection") ||
               lowerMessage.contains("connection reset") ||
               lowerMessage.contains("disconnected") ||
               lowerMessage.contains("connected") ||
               lowerMessage.contains("joining") ||
               lowerMessage.contains("left the game");
    }
    
    private static boolean isPacketLog(String loggerName, String message) {
        for (String keyword : PACKET_LOGGER_KEYWORDS) {
            if (loggerName.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("packet") ||
               lowerMessage.contains("sending packet") ||
               lowerMessage.contains("receiving packet") ||
               lowerMessage.contains("payload") ||
               lowerMessage.contains("protocol");
    }
    
    public static String truncateLogMessage(String message) {
        int maxLength = ConsoleLoggerConfig.getMaxLogLength();
        
        if (maxLength <= 0 || message.length() <= maxLength) {
            return message;
        }
        
        return message.substring(0, maxLength) + "...";
    }
}