package com.psk.nlpmod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ChatListener {

    private static final Set<UUID> listeningPlayers = new HashSet<>();

    public static void startListening(Player player) {
        listeningPlayers.add(player.getUUID());
    }

    public static void stopListening(ServerPlayer player) {
        listeningPlayers.remove(player.getUUID());
    }

    public static boolean isListening(ServerPlayer player) {
        return listeningPlayers.contains(player.getUUID());
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        if (listeningPlayers.contains(player.getUUID())) {
            String message = String.valueOf(event.getMessage());

            // Reply to a player's message
            respondToPlayerMessage(player, message);
        }
    }

    private static void respondToPlayerMessage(ServerPlayer player, String message) {
        new Thread(() -> {
            try {
                String rawResponse = FlaskRequestHandler.getAIResponse(message);
                String response = FlaskResponseHandler.parseFlaskResponse(rawResponse);
                String entityName = EntityInteractionHandler.getLastInteractedEntityName();
                // Check if the answer starts with the specified text
                if (response.startsWith("Error")) {
                    // Display response in red
                    Component errorResponse = Component.literal(response)
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)));
                    player.displayClientMessage(errorResponse, false);
                } else {
                    // Normal response
                    player.displayClientMessage(Component.literal("<" + entityName + "> " + response), false);
                }
            } catch (IOException e) {
                Component errorMessage = Component.literal("Error while processing response: " + e.getMessage())
                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)));
                player.displayClientMessage(errorMessage, false);
            }
        }).start();
    }
}
