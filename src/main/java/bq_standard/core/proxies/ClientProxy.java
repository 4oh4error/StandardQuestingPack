package bq_standard.core.proxies;

import java.awt.Color;
import net.minecraft.util.ResourceLocation;
import betterquesting.client.themes.ThemeRegistry;
import betterquesting.client.themes.ThemeStandard;
import bq_standard.core.BQ_Standard;
import bq_standard.nei.NEIRegister;
import bq_standard.network.PacketStandard;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public void registerHandlers()
	{
		super.registerHandlers();
		
    	BQ_Standard.instance.network.registerMessage(PacketStandard.HandlerClient.class, PacketStandard.class, 0, Side.CLIENT);
    	
    	if(Loader.isModLoaded("NotEnoughItems"))
    	{
    		NEIRegister.instance.registerHandler();
    	}
	}
	
	@Override
	public void registerThemes()
	{
		ThemeRegistry.RegisterTheme(new ThemeStandard("Standard Light", new ResourceLocation("betterquesting", "textures/gui/editor_gui.png")), "light");
		ThemeRegistry.RegisterTheme(new ThemeStandard("Standard Dark", new ResourceLocation("bq_standard", "textures/gui/editor_gui_dark.png")).setTextColor(Color.WHITE), "dark");
		ThemeRegistry.RegisterTheme(new ThemeStandard("Stronghold", new ResourceLocation("bq_standard", "textures/gui/editor_gui_stronghold.png")).setTextColor(Color.WHITE), "stronghold");
		ThemeRegistry.RegisterTheme(new ThemeStandard("Overworld", new ResourceLocation("bq_standard", "textures/gui/editor_gui_overworld.png")).setTextColor(Color.WHITE), "overworld");
		ThemeRegistry.RegisterTheme(new ThemeStandard("Nether", new ResourceLocation("bq_standard", "textures/gui/editor_gui_nether.png")).setTextColor(Color.WHITE), "nether");
		ThemeRegistry.RegisterTheme(new ThemeStandard("End", new ResourceLocation("bq_standard", "textures/gui/editor_gui_end.png")).setTextColor(Color.WHITE), "end");
		ThemeRegistry.RegisterTheme(new ThemeStandard("Vanilla", new ResourceLocation("bq_standard", "textures/gui/editor_gui_vanilla.png")), "vanilla");
	}
}
