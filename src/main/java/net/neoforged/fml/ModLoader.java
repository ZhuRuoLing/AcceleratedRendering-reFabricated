package net.neoforged.fml;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ModLoader {
    private static final Logger logger = LoggerFactory.getLogger("ModLoader");
    private static final IEventBus eventBus = IEventBus.create();
    private static final Map<String, ModContainer> modContainerMap = new HashMap<>();

    public static void postEvent(Event event) {
        if (event instanceof IModBusEvent) {
            for (EventPriority phase : EventPriority.values()) {
                for (ModContainer container : modContainerMap.values()) {
                    container.acceptEvent(phase, (Event & IModBusEvent) event);
                }
            }
            return;
        }
        for (EventPriority phase : EventPriority.values()) {
            try {
                eventBus.post(phase, event);
            } catch (Throwable ex) {
                logger.error("An exception was thrown while posing event {}.", event, ex);
            }
        }
    }

    public static ModContainer createModContainer(String modid) {
        return modContainerMap.computeIfAbsent(modid, ModContainer::new);
    }

    public static <T extends Event> T postEventWithReturn(T event) {
        postEvent(event);
        return event;
    }
}
