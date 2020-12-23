package com.keuin.blame;

import com.google.gson.Gson;
import com.keuin.blame.adapter.*;
import com.keuin.blame.config.BlameConfig;
import com.keuin.blame.util.PrintUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Blame implements ModInitializer {

	private final Logger logger = Logger.getLogger(Blame.class.getName());

	public static BlameConfig config;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		String configFileName = "blame.json";
		try {
			// load config
			File configFile = new File(configFileName);
			if (!configFile.exists()) {
				logger.severe(String.format("Failed to read configuration file %s. Blame will be disabled.", configFileName));
				return;
			}

			Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8);
			config = (new Gson()).fromJson(reader, BlameConfig.class);
		} catch (IOException exception) {
			logger.severe(String.format("Failed to read configuration file %s: %s. " +
					"Blame will be disabled.", configFileName, exception));
			return;
		}

		AttackEntityCallback.EVENT.register(new AttackEntityAdapter(EventHandler.INSTANCE));
		PlayerBlockBreakEvents.AFTER.register(new BreakBlockAdapter(EventHandler.INSTANCE));
		UseBlockCallback.EVENT.register(new UseBlockAdapter(EventHandler.INSTANCE));
		UseEntityCallback.EVENT.register(new UseEntityAdapter(EventHandler.INSTANCE));
		UseItemCallback.EVENT.register(new UseItemAdapter(EventHandler.INSTANCE));

		ServerLifecycleEvents.SERVER_STARTED.register(PrintUtil.INSTANCE);
	}
}
