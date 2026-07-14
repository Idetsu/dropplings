package droppling.jhrdev.species;

/**
 * Configuration for rendering items inside a Droppling's body.
 * Defines positions for each inventory slot.
 */
public record ItemRenderConfig(
    ItemRenderPosition[] slotPositions
) {
    public ItemRenderConfig {
        if (slotPositions == null) {
            slotPositions = new ItemRenderPosition[0];
        }
    }

    public static ItemRenderConfig of(ItemRenderPosition[] slotPositions) {
        return new ItemRenderConfig(slotPositions);
    }

    public static ItemRenderConfig empty() {
        return new ItemRenderConfig(new ItemRenderPosition[0]);
    }
}
