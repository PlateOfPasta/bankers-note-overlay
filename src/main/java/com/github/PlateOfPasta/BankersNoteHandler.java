package com.github.PlateOfPasta;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
@Slf4j
public class BankersNoteHandler
{
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final Gson gson;
    private final Pattern pattern;

    @Getter(AccessLevel.PACKAGE)
    private BankersNoteItem storedBankersNoteItem;

    @Inject
    public BankersNoteHandler(ConfigManager configManager, ItemManager itemManager, Gson gson)
    {
        this.configManager = configManager;
        this.itemManager = itemManager;
        this.gson = gson;

        String regex = "<col=ff9040>(.*?)</col>";
        this.pattern = Pattern.compile(regex);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getItemId() != ItemID.BANKERS_NOTE) return;

        var menuAction = event.getMenuAction();

        if (menuAction == null) return;

        var menuActionId = menuAction.getId();

        if (menuActionId != MenuAction.WIDGET_TARGET_ON_WIDGET.getId()
            && menuActionId != MenuAction.CC_OP.getId()) return;

        var menuTarget = event.getMenuTarget();

        Matcher matcher = pattern.matcher(menuTarget);
        var itemName = matcher.find()
            ? matcher.group(1)
            : "";

        if (storedBankersNoteItem == null || !storedBankersNoteItem.getSearchableName().equals(itemName))
        {
            lookupItem(itemName).ifPresent(this::setStoredBankersNoteItem);
        }
    }

    private void setStoredBankersNoteItem(BankersNoteItem bankersNoteItem)
    {
        storedBankersNoteItem = bankersNoteItem;
        saveCurrentBankersNoteStoredItem();
    }

    private Optional<BankersNoteItem> lookupItem(String itemName)
    {
        var searchResult = itemManager.search(itemName);

        if (searchResult.isEmpty()) return Optional.empty();

        var itemSearchResult = searchResult.get(0);
        var canonId = itemManager.canonicalize(itemSearchResult.getId());

        if (!isNoteable(canonId)) return Optional.empty();

        var result = new BankersNoteItem(itemName, canonId);

        log.debug(result.toString());

        return Optional.of(result);
    }

    private boolean isNoteable(int canonItemId)
    {
        var composition = itemManager.getItemComposition(canonItemId);
        var noteId = composition.getLinkedNoteId();
        return noteId != -1; // getLinkedNoteId returns -1 for "error" (i.e., note variant doesn't exist)
    }

    void loadCurrentBankersNoteStoredItem()
    {
        final String storedBankersNoteItemJson = configManager.getConfiguration(BankersNoteHelperConfig.CONFIG_GROUP, BankersNoteHelperConfig.STORED_ITEM);

        if (!Strings.isNullOrEmpty(storedBankersNoteItemJson))
        {
            final BankersNoteItem storedItemFromJson = gson.fromJson(storedBankersNoteItemJson, new TypeToken<BankersNoteItem>()
            {
            }.getType());

            storedBankersNoteItem = storedItemFromJson;
        }
    }

    void saveCurrentBankersNoteStoredItem()
    {
        final String json = gson.toJson(storedBankersNoteItem);
        configManager.setConfiguration(BankersNoteHelperConfig.CONFIG_GROUP, BankersNoteHelperConfig.STORED_ITEM, json);
    }
}
