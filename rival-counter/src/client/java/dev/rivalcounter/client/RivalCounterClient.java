package dev.rivalcounter.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;

public final class RivalCounterClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("rivalcounter", "controls")
    );

    @Override
    public void onInitializeClient() {
        KeyMapping openHistory = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.rivalcounter.open_history", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RivalStore.ensureServer(client);
            while (openHistory.consumeClick()) {
                if (client.player != null && client.gui.screen() == null) {
                    client.gui.setScreen(new RivalScreen());
                }
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> recordDeathMessage(message));
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, timestamp) ->
                recordDeathMessage(message));
    }

    private static void recordDeathMessage(Component message) {
        if (!(message.getContents() instanceof TranslatableContents translated)) return;
        String key = translated.getKey();
        if (!key.equals("death.attack.player") && !key.equals("death.attack.player.item")) return;

        Object[] args = translated.getArgs();
        if (args.length < 2) return;
        String victim = textValue(args[0]);
        String killer = textValue(args[1]);
        if (victim.isBlank() || killer.isBlank() || victim.equals(killer)) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        String localName = client.player.getName().getString();
        if (victim.equalsIgnoreCase(localName)) {
            RivalStore.recordDeath(killer);
        } else if (killer.equalsIgnoreCase(localName)) {
            RivalStore.recordKill(victim);
        }
    }

    private static String textValue(Object value) {
        if (value instanceof Component component) return component.getString().trim();
        if (value instanceof String string) return string.trim();
        return "";
    }
}
