package net.fallenflower.consolelogger;

import net.fallenflower.consolelogger.util.LogFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "ChatAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class ChatAppender extends AbstractAppender {

    protected ChatAppender(String name, Filter filter, PatternLayout layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static ChatAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") PatternLayout layout) {

        if (layout == null) {
            layout = PatternLayout.newBuilder()
                    .withPattern("%d{HH:mm:ss} [%t/%level] [%logger{36}]: %msg")
                    .build();
        }

        return new ChatAppender(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        if (LogFilter.shouldFilter(event)) {
            return; 
        }
        
        String formattedMessage = getLayout().toSerializable(event).toString();
        
        new Thread(() -> {
            LogToChatManager.processLogMessage(event.getLevel(), formattedMessage);
        }, "LogToChat-Thread").start();
    }
}