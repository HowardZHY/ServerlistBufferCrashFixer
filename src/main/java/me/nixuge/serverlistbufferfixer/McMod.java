package me.nixuge.serverlistbufferfixer;

import java.io.File;

import me.nixuge.serverlistbufferfixer.config.ConfigCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = McMod.MOD_ID,
        name = McMod.NAME,
        version = McMod.VERSION,
        guiFactory = "me.nixuge.serverlistbufferfixer.gui.GuiFactory",
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        useMetadata = true
)
public class McMod {
    public static final String MOD_ID = "serverlistbufferfixer";
    public static final String NAME = "Serverlist Buffer Fixer";
    public static final String VERSION = "1.1.0";

    @Mod.Instance(value = McMod.MOD_ID)
    private static McMod instance;

    private Configuration configuration;

    private String configDirectory;

    private ConfigCache configCache;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setConfigDirectory(String configDirectory) {
        this.configDirectory = configDirectory;
    }

    public void setConfigCache(ConfigCache configCache) {
        this.configCache = configCache;
    }

    public static McMod getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getConfigDirectory() {
        return configDirectory;
    }

    public ConfigCache getConfigCache() {
        return configCache;
    }

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        this.configDirectory = event.getModConfigurationDirectory().toString();
        final File path = new File(this.configDirectory + File.separator + McMod.MOD_ID + ".cfg");
        this.configuration = new Configuration(path);
        this.configCache = new ConfigCache(this.configuration);
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(configCache);
    }
}
