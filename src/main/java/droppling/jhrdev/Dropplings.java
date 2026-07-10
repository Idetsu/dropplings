package droppling.jhrdev;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import droppling.jhrdev.registry.ModEntities;
import droppling.jhrdev.registry.ModItems;
import droppling.jhrdev.registry.ModSounds;

import software.bernie.geckolib.GeckoLib;


public class Dropplings implements ModInitializer {
	public static final String MOD_ID = "dropplings";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		LOGGER.info("Inicializando Dropplings");
		ModEntities.registerModEntities();
		ModItems.registerModItems();
		ModSounds.registerModSounds();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
