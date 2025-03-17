package net.neoforged.fml;

import lombok.Getter;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModContainer {
    private final Logger logger = LoggerFactory.getLogger("ModContainer");
    private final String modid;
    @Getter
    private final IModInfo modInfo;
    @Getter
    private final IEventBus modEventBus = IEventBus.create();

    public ModContainer(String modid) {
        this.modid = modid;
        this.modInfo = new ModInfo(modid);
    }

    public final <T extends Event & IModBusEvent> void acceptEvent(EventPriority phase, T e) {
        try {
            modEventBus.post(phase, e);
        } catch (Throwable ex) {
            logger.error("An exception was thrown while posing event {}.", e, ex);
        }
    }

    public String getModId() {
        return modid;
    }

}
