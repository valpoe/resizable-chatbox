package com.resizablechatbox;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("resizablechatbox")
public interface ResizableChatboxConfig extends Config {
	@ConfigItem(keyName = "greeting", name = "Welcome Greeting", description = "The message to show to the user when they login")
	default String greeting() {
		return "Hello";
	}

	@Range(min = 28, max = 350)
	@ConfigItem(keyName = "chatboxHeight", name = "Chatbox Height", description = "Set the height of the chatbox")
	default int chatboxHeight() {
		return 142;
	}

	@Range(min = 300, max = 519)
	@ConfigItem(keyName = "chatboxWidth", name = "Chatbox Width", description = "Set the width of the chatbox")
	default int chatboxWidth() {
		return 519;
	}
}
