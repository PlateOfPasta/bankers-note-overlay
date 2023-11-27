package com.github.PlateOfPasta;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
    name = "Bankers Note Overlay"
)
public class BankersNoteHelperPlugin extends Plugin
{
    @Inject
    private EventBus eventBus;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BankersNoteHelperOverlay overlay;

    @Inject
    private BankersNoteHandler handler;

    @Provides
    BankersNoteHelperConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(BankersNoteHelperConfig.class);
    }

    @Override
    protected void startUp()
    {
        handler.loadCurrentBankersNoteStoredItem();

        eventBus.register(handler);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        eventBus.unregister(handler);

        handler.saveCurrentBankersNoteStoredItem();
    }
}
