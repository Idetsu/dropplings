package droppling.jhrdev.species;

/**
 * Movement configuration for a species.
 * Contains all movement-related settings including jump behavior.
 */
public record MovementProfile(
    SlimeJumpSettings slimeJump
) {
    public static MovementProfile standard() {
        return new MovementProfile(SlimeJumpSettings.standard());
    }

    public static MovementProfile aggressive() {
        return new MovementProfile(SlimeJumpSettings.aggressive());
    }

    public static MovementProfile disabled() {
        return new MovementProfile(SlimeJumpSettings.disabled());
    }
}
