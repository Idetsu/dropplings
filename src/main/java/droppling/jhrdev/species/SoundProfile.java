package droppling.jhrdev.species;

import java.util.Optional;
import net.minecraft.sound.SoundEvent;

public record SoundProfile(
        Optional<SoundEvent> ambient,
        Optional<SoundEvent> hurt,
        Optional<SoundEvent> death,
        SoundEvent step
) {
}
