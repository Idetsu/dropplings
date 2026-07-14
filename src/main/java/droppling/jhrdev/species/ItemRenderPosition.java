package droppling.jhrdev.species;

/**
 * Position and rotation configuration for rendering an item inside a Droppling's body.
 */
public record ItemRenderPosition(
    float offsetX,
    float offsetY,
    float offsetZ,
    float rotationX,
    float rotationY,
    float rotationZ,
    float scale,
    float rotationSpeed
) {
    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 2.0f;
    private static final float MIN_SPEED = 0.0f;
    private static final float MAX_SPEED = 5.0f;

    public ItemRenderPosition {
        scale = clampScale(scale);
        rotationSpeed = clampSpeed(rotationSpeed);
    }

    public static ItemRenderPosition of(float offsetX, float offsetY, float offsetZ, float rotationX, float rotationY, float rotationZ, float scale) {
        return new ItemRenderPosition(offsetX, offsetY, offsetZ, rotationX, rotationY, rotationZ, scale, 1.0f);
    }

    public static ItemRenderPosition of(float offsetX, float offsetY, float offsetZ, float rotationX, float rotationY, float rotationZ, float scale, float rotationSpeed) {
        return new ItemRenderPosition(offsetX, offsetY, offsetZ, rotationX, rotationY, rotationZ, scale, rotationSpeed);
    }

    private static float clampScale(float value) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, value));
    }

    private static float clampSpeed(float value) {
        return Math.max(MIN_SPEED, Math.min(MAX_SPEED, value));
    }
}
