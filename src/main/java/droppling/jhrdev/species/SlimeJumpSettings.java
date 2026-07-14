package droppling.jhrdev.species;

/**
 * Configuration for slime-like jumping behavior.
 * Allows species to have different jump characteristics.
 */
public record SlimeJumpSettings(
    boolean enabled,
    double jumpStrength,
    int cooldownTicks,
    double jumpChance
) {
    private static final double MIN_STRENGTH = 0.0;
    private static final double MAX_STRENGTH = 2.0;
    private static final int MIN_COOLDOWN = 0;
    private static final int MAX_COOLDOWN = 100;
    private static final double MIN_CHANCE = 0.0;
    private static final double MAX_CHANCE = 1.0;

    public SlimeJumpSettings {
        jumpStrength = clampStrength(jumpStrength);
        cooldownTicks = clampCooldown(cooldownTicks);
        jumpChance = clampChance(jumpChance);
    }

    public static SlimeJumpSettings disabled() {
        return new SlimeJumpSettings(false, 0.0, 0, 0.0);
    }

    public static SlimeJumpSettings standard() {
        return new SlimeJumpSettings(true, 0.42, 12, 0.8);
    }

    public static SlimeJumpSettings aggressive() {
        return new SlimeJumpSettings(true, 0.5, 8, 0.95);
    }

    public static SlimeJumpSettings of(double jumpStrength, int cooldownTicks, double jumpChance) {
        return new SlimeJumpSettings(true, jumpStrength, cooldownTicks, jumpChance);
    }

    private static double clampStrength(double value) {
        return Math.max(MIN_STRENGTH, Math.min(MAX_STRENGTH, value));
    }

    private static int clampCooldown(int value) {
        return Math.max(MIN_COOLDOWN, Math.min(MAX_COOLDOWN, value));
    }

    private static double clampChance(double value) {
        return Math.max(MIN_CHANCE, Math.min(MAX_CHANCE, value));
    }
}
