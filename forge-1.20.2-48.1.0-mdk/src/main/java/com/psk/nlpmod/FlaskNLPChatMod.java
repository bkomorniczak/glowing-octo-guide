package com.psk.nlpmod;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(FlaskNLPChatMod.MODID)
public class FlaskNLPChatMod {

    public static final String MODID = "flasknlpchat";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FlaskNLPChatMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(NLPChatCommand.class); // Rejestruje NLPChatCommand
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Konfiguracja wspólna
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        // Konfiguracja klienta
    }
}
