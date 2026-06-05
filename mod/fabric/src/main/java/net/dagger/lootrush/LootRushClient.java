package net.dagger.lootrush;

import net.fabricmc.api.ClientModInitializer;

public class LootRushClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LootRush.LOGGER.info("LootRush client entrypoint initialized");
    }
}
