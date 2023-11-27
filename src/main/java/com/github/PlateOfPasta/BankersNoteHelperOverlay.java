package com.github.PlateOfPasta;

import com.google.inject.Inject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

class BankersNoteHelperOverlay extends WidgetItemOverlay
{
    private final BankersNoteHandler handler;
    private final BankersNoteHelperConfig config;
    private final ItemManager itemManager;

    @Inject
    BankersNoteHelperOverlay(BankersNoteHandler handler, BankersNoteHelperConfig config, ItemManager itemManager)
    {
        this.handler = handler;
        this.config = config;
        this.itemManager = itemManager;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (itemId != ItemID.BANKERS_NOTE) return;

        var currentBankerNoteItem = handler.getStoredBankersNoteItem();

        if (currentBankerNoteItem == null) return;

        var storedItemImage = itemManager.getImage(currentBankerNoteItem.getCanonItemId());
        var widgetRectangle = widgetItem.getCanvasBounds();

        // config scale is a range from 0-100,
        // but the transform range needs to be from 0.0 to 1.0.
        double scale = config.overlayRenderScale() / 100.0;

        var transformOp = getOverlayTransform(
            config.renderCorner(),
            scale,
            storedItemImage.getWidth(),
            storedItemImage.getHeight(),
            widgetRectangle.width,
            widgetRectangle.height
        );

        graphics.drawImage(storedItemImage, transformOp, widgetRectangle.x, widgetRectangle.y);
    }

    private static AffineTransformOp getOverlayTransform(
        OverlayRenderCorner renderCorner, double scale,
        int overlayImageOriginalWidth, int overlayImageOriginalHeight,
        int boundaryRectangleWidth, int boundaryRectangleHeight)
    {
        int newWidth = (int) (overlayImageOriginalWidth * scale);
        int newHeight = (int) (overlayImageOriginalHeight * scale);

        int translateX = 0;
        int translateY = 0;

        switch (renderCorner)
        {
            case TOP_RIGHT:
                translateX = boundaryRectangleWidth - newWidth;
                break;
            case BOTTOM_LEFT:
                translateY = boundaryRectangleHeight - newHeight;
                break;
            case BOTTOM_RIGHT:
                translateX = boundaryRectangleWidth - newWidth;
                translateY = boundaryRectangleHeight - newHeight;
                break;
            case CENTER:
                translateX = (int) ((boundaryRectangleWidth - newWidth) * scale);
                translateY = (int) ((boundaryRectangleHeight - newHeight) * scale);
                break;
            case TOP_LEFT:
            default:
                // No translation needed for TOP_LEFT
                break;
        }

        AffineTransform transform = new AffineTransform();
        // Translate relative to the boundary's top left corner
        transform.translate(translateX, translateY);
        // Then scale the image to "fit properly" within the boundary (
        transform.scale(scale, scale);

        return new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }
}
