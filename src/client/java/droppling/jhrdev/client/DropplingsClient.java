package droppling.jhrdev.client;

import droppling.jhrdev.registry.ModEntities;
import droppling.jhrdev.client.renderer.DropplingRenderer;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DropplingsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		EntityRendererRegistry.register(
				ModEntities.DROPPLING,
				DropplingRenderer::new
		);

	}
}