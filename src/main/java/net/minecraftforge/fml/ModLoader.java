package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ModLoader {
    private static final ModLoader instance = new ModLoader();

    private final Logger logger = LoggerFactory.getLogger("ModLoader");
    private final IEventBus eventBus = IEventBus.create();
    private final Map<String, ModContainer> modContainerMap = new HashMap<>();

    public void postEvent(Event event) {
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

    public ModContainer createModContainer(String modid) {
        return modContainerMap.computeIfAbsent(modid, ModContainer::new);
    }

    public <T extends Event> T postEventWithReturn(T event) {
        postEvent(event);
        return event;
    }

    public static ModLoader get() {
        return instance;
    }
}
