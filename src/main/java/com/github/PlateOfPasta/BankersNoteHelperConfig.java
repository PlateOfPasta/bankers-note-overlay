package com.github.PlateOfPasta;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("bankersNoteOverlay")
public interface BankersNoteHelperConfig extends Config
{
    String CONFIG_GROUP = "bankersNoteOverlay";
    String STORED_ITEM = "storedBankersNoteItem";

    @ConfigItem(
        keyName = "renderCorner",
        name = "Overlay Render Corner",
        description = "Selects which corner to render the overlay representing the stored item",
        position = 2
    )
    default OverlayRenderCorner renderCorner()
    {
        return OverlayRenderCorner.TOP_LEFT;
    }

    @Range(min = 1, max = 100)
    @ConfigItem(
        keyName = "overlayRenderScale",
        name = "Overlay Render Scale Percent",
        description = "Scale from 1% to 100% controlling the size of the rendered overlay",
        position = 3
    )
    default int overlayRenderScale()
    {
        return 80;
    }
}
