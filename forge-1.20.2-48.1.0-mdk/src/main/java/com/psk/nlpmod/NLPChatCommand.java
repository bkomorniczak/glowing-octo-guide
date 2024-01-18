package com.psk.nlpmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.Objects;

public class NLPChatCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void spawnFirework(ServerPlayer player) {
        // Create a fireworks object with the specified data
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        CompoundTag fireworkTag = new CompoundTag();
        CompoundTag fireworkExplosions = new CompoundTag();
        ListTag explosionsList = new ListTag();

        fireworkExplosions.putString("Type", "Burst"); // Explosion type
        fireworkExplosions.putIntArray("Colors", new int[]{DyeColor.RED.getFireworkColor()}); // Colors
        explosionsList.add(fireworkExplosions);

        fireworkTag.put("Explosions", explosionsList);
        fireworkTag.putByte("Flight", (byte) 1); // Flight time

        CompoundTag fireworkItemTag = new CompoundTag();
        fireworkItemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(fireworkItemTag);

        // Obtaining references to the world in which the player is located
        ServerLevel world = Objects.requireNonNull(player.getServer()).overworld();

        // Creating fireworks in the game world
        FireworkRocketEntity firework = new FireworkRocketEntity(world, player.getX(), player.getY() + 1, player.getZ(), fireworkStack);

        // Adding fireworks to the game world
        world.addFreshEntity(firework);
    }



    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nlpchat")
                .executes(context -> {
                    // Displays the menu
                    ServerPlayer player = context.getSource().getPlayerOrException(); //the player executing the command
                    spawnFirework(player);
                    displayMenu(context.getSource());
                    return 1;
                })
                .then(Commands.literal("reset")
                        .executes(context -> {
                            ApiConfig.resetApiEndpointUrl();
                            SecurityConfig.resetSecurityToken();
                            context.getSource().sendSuccess(() -> Component.literal("Endpoint API and Security Token have been reset to default values."), true);
                            return 1;
                        })
                )
                .then(Commands.literal("getToken")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            String securityTokenKey = SecurityConfig.getSecurityTokenKey();
                            String securityTokenValue = SecurityConfig.getSecurityTokenValue();
                            source.sendSuccess(() -> Component.literal("Current security token key:" + securityTokenKey), true);
                            source.sendSuccess(() -> Component.literal("Current security token value:" + securityTokenValue), true);
                            return 1;
                        }))
                .then(Commands.literal("enable")
                        .executes(context -> {
                            // Activation code
                            enableNLPChat(context.getSource());
                            return 1;
                        }))
                .then(Commands.literal("disable")
                        .executes(context -> {
                            // Deactivation code
                            disableNLPChat(context.getSource());
                            return 1;
                        }))
                .then(Commands.literal("ask")
                        .then(Commands.argument("query", StringArgumentType.string())
                                .executes(context -> {
                                    String query = StringArgumentType.getString(context, "query");
                                    askAI(context.getSource(), query);
                                    return 1;
                                }))
                )
                .then(Commands.literal("setApiUrl")
                        .then(Commands.argument("url", StringArgumentType.string())
                                .executes(context -> {
                                    String url = StringArgumentType.getString(context, "url");
                                    ApiConfig.setApiEndpointUrl(url);
                                    context.getSource().sendSuccess(() -> Component.literal("API URL updated to: " + url), false);
                                    return 1;
                                }))
                )
                .then(Commands.literal("setToken")
                        .then(Commands.argument("key", StringArgumentType.string())
                                .then(Commands.argument("value", StringArgumentType.string())
                                        .executes(context -> {
                                            String key = StringArgumentType.getString(context, "key");
                                            String value = StringArgumentType.getString(context, "value");
                                            SecurityConfig.setSecurityToken(key, value);
                                            context.getSource().sendSuccess(() -> Component.literal("Security token updated to: " + key + ":" + value), false);
                                            return 1;
                                        }))
                        )
                )
                .then(Commands.literal("help")
                        .executes(context -> {
                            // Support code
                            displayHelp(context.getSource());
                            return 1;
                        }))
        );
    }

    private static void displayMenu(CommandSourceStack source) {
        boolean isTokenSet = SecurityConfig.isTokenSet();
        boolean apiConnectionStatus = FlaskRequestHandler.checkApiConnection();
        boolean isListening = ChatListener.isListening((ServerPlayer) source.getEntity());

        Component connectionStatusComponent = Component.literal(apiConnectionStatus ? "true" : "false")
                .setStyle(Style.EMPTY.withColor(apiConnectionStatus ? TextColor.fromRgb(0x00FF00) : TextColor.fromRgb(0xFF0000)));
        Component tokenStatusComponent = Component.literal(isTokenSet ? "true" : "false")
                .setStyle(Style.EMPTY.withColor(isTokenSet ? TextColor.fromRgb(0x00FF00) : TextColor.fromRgb(0xFF0000)));
        Component apiEndpointComponent = Component.literal(ApiConfig.getApiEndpointUrl())
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00)));
        Component listeningStatusComponent = Component.literal(isListening ? "Enabled" : "Disabled")
                .setStyle(Style.EMPTY.withColor(isListening ? TextColor.fromRgb(0x00FF00) : TextColor.fromRgb(0xFF0000)));

        source.sendSuccess(() -> Component.literal("NLP Chat Menu:\n")
                .append("Endpoint API: ").append(apiEndpointComponent).append("\n")
                .append("Token Set: ").append(tokenStatusComponent).append("\n")
                .append("API Connection Status: ").append(connectionStatusComponent).append("\n")
                .append("Listening Status: ").append(listeningStatusComponent).append("\n")
                .append("Use /nlpchat help for help"), false);
    }
    private static void displayHelp(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal(
                """
                        Commands:
                        /nlpchat - Display the main menu
                        /nlpchat ask "query" - Ask a question to the AI
                        /nlpchat enable - Enable the listening status for NLP chat
                        /nlpchat disable - Disable the listening status for NLP chat
                        /nlpchat reset - Resets the current settings to default
                        /nlpchat getToken - Retrieves the current authentication token
                        /nlpchat setApiUrl "url" - Sets the NLP API endpoint URL
                        /nlpchat setToken "key" "value" - Sets the NLP API authentication token
                        /nlpchat help - Display this help message"""), false);
    }
    private static void enableNLPChat(CommandSourceStack source) {
        // Code to activate NLP Chat
        ServerPlayer executingPlayer = source.getPlayer();
        if (executingPlayer != null) {
            ChatListener.startListening(executingPlayer);
            executingPlayer.displayClientMessage(Component.literal("NLPChat enabled"), true);
        }
    }
    private static void disableNLPChat(CommandSourceStack source) {
        // Code to deactivate NLP Chat
        ServerPlayer executingPlayer = source.getPlayer();
        if (executingPlayer != null) {
            ChatListener.stopListening(executingPlayer);
            executingPlayer.displayClientMessage(Component.literal("NLPChat disabled"), true);
        }
    }
    private static void askAI(CommandSourceStack source, String query) {
        // Code to handle the request to AI
        try {
            String rawResponse = FlaskRequestHandler.getAIResponse(query);
            String response = FlaskResponseHandler.parseFlaskResponse(rawResponse);
            source.sendSuccess(() -> Component.literal("<AI> " + response), false);
        } catch (IOException e) {
            source.sendFailure(Component.literal("Error: " + e.getMessage()));
        }
    }
}