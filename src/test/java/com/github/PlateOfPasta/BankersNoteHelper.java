package com.github.PlateOfPasta;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BankersNoteHelper
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BankersNoteHelperPlugin.class);
		RuneLite.main(args);
	}
}