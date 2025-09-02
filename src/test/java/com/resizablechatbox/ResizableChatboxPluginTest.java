package com.resizablechatbox;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ResizableChatboxPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(ResizableChatboxPlugin.class);
		RuneLite.main(args);
	}
}