package com.psk.nlpmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

public class NLPChatCommand {

    private static ServerPlayer player;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nlpchat")
                .then(Commands.literal("enable")
                        .executes(context -> {
                            player = context.getSource().getPlayer();
                            player.displayClientMessage(Component.literal("NLPChat enabled"), true);
                            return 1;
                        }))
                .then(Commands.literal("disable")
                        .executes(context -> {
                            if (player != null) {
                                player.displayClientMessage(Component.literal("NLPChat disabled"), true);
                                player = null;
                            }
                            return 1;
                        }))
                .then(Commands.literal("ask")
                        .then(Commands.argument("query", StringArgumentType.string())
                                .executes(context -> {
                                    String query = StringArgumentType.getString(context, "query");
                                    try {
                                        String response = FlaskRequestHandler.getAIResponse(query);
                                        context.getSource().sendFailure(Component.literal(response));
                                    } catch (IOException e) {
                                        context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
                                    }
                                    return 1;
                                }))
                )
        );
    }
}
