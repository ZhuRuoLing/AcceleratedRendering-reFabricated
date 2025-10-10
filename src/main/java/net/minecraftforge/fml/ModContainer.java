package net.minecraftforge.fml;

import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.neoforgespi.language.IModInfo;
import net.minecraftforge.neoforgespi.language.ModInfo;
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
