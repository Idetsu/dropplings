package droppling.jhrdev.registry;

import droppling.jhrdev.Dropplings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent DROPPLING_PLOP =
            Registry.register(
                    Registries.SOUND_EVENT,
                    Dropplings.id("entity.droppling.plop"),
                    SoundEvent.of(Dropplings.id("entity.droppling.plop"))
            );


    public static void registerModSounds() {
        Dropplings.LOGGER.info("Registrando sonidos...");
    }
}