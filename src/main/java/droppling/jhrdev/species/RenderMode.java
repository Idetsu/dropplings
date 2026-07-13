package droppling.jhrdev.species;

/**
 * Base material type for a bone. Emission is configured separately via
 * {@link BoneRenderSettings#emissiveStrength()}, not as a render mode.
 */
public enum RenderMode {
    OPAQUE,
    CUTOUT,
    TRANSLUCENT
}
