package net.neoforged.bus.api;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class EventBusImpl implements IEventBus {

    private final Logger logger = LoggerFactory.getLogger("EventBusImpl");
    private final Multimap<EventPriority, EventHandler> eventHandlers = MultimapBuilder.hashKeys().arrayListValues().build();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Override
    public <T> void register(Class<T> clazz) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
                if (declaredAnnotation instanceof SubscribeEvent subscribeEvent) {
                    if (declaredMethod.getParameters().length == 1) {
                        Class<?> paramType = declaredMethod.getParameters()[0].getType();
                        try {
                            EventPriority phase = subscribeEvent.priority();
                            MethodHandle handle = lookup.unreflect(declaredMethod);
                            eventHandlers.put(phase, new EventHandler(paramType, handle));
                        } catch (Throwable throwable) {
                            logger.error("Could not register method {} as event handler.", declaredMethod, throwable);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void post(EventPriority phase, Event event) {
        for (EventHandler eventHandler : eventHandlers.get(phase)) {
            if (event.getClass() == eventHandler.eventType) {
                try {
                    eventHandler.mt.invoke(event);
                } catch (Throwable e) {
                    logger.error("An exception was thrown while posing event {} to {}.", e, eventHandler.mt, e);
                }
            }
        }
    }

    private record EventHandler(Class<?> eventType, MethodHandle mt) {
    }
}
