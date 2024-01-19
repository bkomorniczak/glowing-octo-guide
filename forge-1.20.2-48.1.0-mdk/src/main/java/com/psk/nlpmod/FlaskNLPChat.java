package com.psk.nlpmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FlaskNLPChat.MODID)
public class FlaskNLPChat {

    public static final String MODID = "flasknlpchat";

    public FlaskNLPChat() {
        // Register setup method to be called during mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register this mod's event handler with MinecraftForge
        MinecraftForge.EVENT_BUS.register(this);
        // Register NLPChatCommand to handle commands
        MinecraftForge.EVENT_BUS.register(NLPChatCommand.class);
        // Load configuration settings from API
        ApiConfig.loadConfig();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Common setup configuration
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        // Client setup configuration
    }
}
