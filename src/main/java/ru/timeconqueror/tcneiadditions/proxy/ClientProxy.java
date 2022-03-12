package ru.timeconqueror.tcneiadditions.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import ru.timeconqueror.tcneiadditions.HandlerRemover;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;
import ru.timeconqueror.tcneiadditions.nei.NEIConfig;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(TCNAClient.getInstance());
        MinecraftForge.EVENT_BUS.register(new HandlerRemover());
        MinecraftForge.EVENT_BUS.register(new NEIConfig());
        super.preInit(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}

