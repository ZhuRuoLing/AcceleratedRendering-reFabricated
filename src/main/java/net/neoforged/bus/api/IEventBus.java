package net.neoforged.bus.api;

public interface IEventBus {

    static IEventBus create() {
        return new EventBusImpl();
    }

    <T> void register(Class<T> tClass);

    void post(EventPriority phase, Event event) throws Throwable;
}
