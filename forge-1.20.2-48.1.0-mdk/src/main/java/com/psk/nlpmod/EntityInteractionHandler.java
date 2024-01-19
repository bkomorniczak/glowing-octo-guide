package com.psk.nlpmod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = FlaskNLPChat.MODID)
public class EntityInteractionHandler {

    private static final WeakHashMap<Entity, Long> lastInteractionTimes = new WeakHashMap<>();
    private static String lastInteractedEntityName = "";

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity(); // Corrected player retrieval
        Entity targetEntity = event.getTarget();
        InteractionHand hand = event.getHand();

        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            long currentTime = System.currentTimeMillis();
            Long lastInteractionTime = lastInteractionTimes.get(targetEntity);

            if (lastInteractionTime == null || (currentTime - lastInteractionTime) > 1000) { // 1-second delay between interactions
                lastInteractionTimes.put(targetEntity, currentTime);

                String entityName = targetEntity.getType().getDescription().getString(); // Get the mob's name
                String response = "<" + entityName + "> The start of the conversation"; // Include the mob's name in the response
                player.displayClientMessage(Component.literal(response), false);
                lastInteractedEntityName = targetEntity.getType().getDescription().getString(); // Save the mob's name
                ChatListener.startListening(player);
            }
        }
    }
    public static String getLastInteractedEntityName() {
        return lastInteractedEntityName;
    }
}