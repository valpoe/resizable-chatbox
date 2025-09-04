package com.resizablechatbox;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "Resizable Chatbox", description = "Allows to change chatbox dimensions when playing with resizable layout.", tags = {
		"layout", "ui", "chat", "chatbox", "resizable" })
public class ResizableChatboxPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ResizableChatboxConfig config;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws Exception {
		log.info("Resizable Chatbox started!");
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Resizable Chatbox stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		log.info("Game state changed to: {}", gameStateChanged.getGameState());
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
		if (widgetLoaded.getGroupId() == InterfaceID.CHATBOX) {
			log.info("Chatbox widget loaded, setting size");
			clientThread.invoke(this::setChatboxSizeFromConfig);
		}

		if (widgetLoaded.getGroupId() == InterfaceID.TOPLEVEL_OSRS_STRETCH ||
				widgetLoaded.getGroupId() == InterfaceID.TOPLEVEL_PRE_EOC) {
			log.info("Resizable viewport loaded, setting chatbox size");
			clientThread.invoke(this::setChatboxSizeFromConfig);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals("resizablechatbox")) {
			return;
		}

		if (configChanged.getKey().equals("chatboxHeight") || configChanged.getKey().equals("chatboxWidth")) {
			log.info("Config changed: {} = {}", configChanged.getKey(), configChanged.getNewValue());

			clientThread.invoke(this::setChatboxSizeFromConfig);
		}
	}

	Widget getViewportChatboxParent() {
		Widget resizableModernChatboxParent = client
				.getWidget(InterfaceID.ToplevelPreEoc.CHAT_CONTAINER);
		Widget resizableClassicChatboxParent = client.getWidget(InterfaceID.ToplevelOsrsStretch.CHAT_CONTAINER);

		if (resizableModernChatboxParent != null && !resizableModernChatboxParent.isHidden()) {
			return resizableModernChatboxParent;
		}

		if (resizableClassicChatboxParent != null && !resizableClassicChatboxParent.isHidden()) {
			return resizableClassicChatboxParent;
		}

		return null;
	}

	private void setChatboxSizeFromConfig() {
		if (client.getGameState() != GameState.LOGGED_IN || !client.isResized()) {
			return;
		}

		Widget chatboxParent = getViewportChatboxParent();

		if (chatboxParent == null) {
			log.info("Chatbox widget not available yet");
			return;
		}

		log.info("Setting chatbox size: width={}, height={}", config.chatboxWidth(), config.chatboxHeight());

		int heightPadding = 32;
		int widthPadding = 4;

		chatboxParent.setOriginalHeight(config.chatboxHeight() + heightPadding);
		chatboxParent.setOriginalWidth(config.chatboxWidth() + widthPadding);
		chatboxParent.setHeightMode(WidgetSizeMode.ABSOLUTE);
		chatboxParent.setWidthMode(WidgetSizeMode.ABSOLUTE);
		chatboxParent.revalidate();
	}

	@Provides
	ResizableChatboxConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ResizableChatboxConfig.class);
	}
}
