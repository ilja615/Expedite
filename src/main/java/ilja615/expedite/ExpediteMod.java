package ilja615.expedite;

import ilja615.expedite.init.ModBlocks;
import ilja615.expedite.init.ModItems;
import ilja615.expedite.init.ModProperties;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.resource.PathResourcePack;

import java.io.IOException;

import static ilja615.expedite.ExpediteMod.MOD_ID;

@Mod(MOD_ID)
public class ExpediteMod
{
    public static final String MOD_ID = "expedite";

    public ExpediteMod() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(ExpediteMod::afterCommonSetup);
    }

    static void afterCommonSetup()
    {
        System.out.println("ExpediteMod afterCommonSetup now run.");
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(ExpediteMod::afterClientSetup);
    }

    static void afterClientSetup()
    {
        System.out.println("ExpediteMod afterClientSetup now run.");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            final IForgeRegistry<Item> registry = event.getRegistry();
            ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block ->
            {
                final BlockItem blockItem = new BlockItem(block, ModProperties.BLOCK_ITEM_PROPERTY);
                blockItem.setRegistryName(block.getRegistryName());
                registry.register(blockItem); //Register item for all the blocks
            });
            //ModEntities.registerEntitySpawnEggs(event); //It registers the spawn egg items
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void registerParticles(ParticleFactoryRegisterEvent event) {
            //ModParticles.registerParticles(event); //It registers particles
        }

        @SubscribeEvent
        public static void entityAttributes(final EntityAttributeCreationEvent event) {
            //ModEntities.CreateEntityAttributes(event); //It creates entity attributes
        }

        @SubscribeEvent
        public static void addPackFinder(final AddPackFindersEvent event) {
            System.out.println("adding pack finder");
            try
            {
                if (event.getPackType() == PackType.SERVER_DATA)
                {
                    IModFile file = ModList.get().getModFileById(MOD_ID).getFile();
                    var resourcePath = file.findResource("expedite_vanilla_overrides");
                    var pack = new PathResourcePack(file.getFileName() + ":" + resourcePath, resourcePath);
                    var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                    if (metadataSection != null)
                    {
                        event.addRepositorySource((packConsumer, packConstructor) ->
                                packConsumer.accept(packConstructor.create(
                                        "expedite_vanilla_overrides", new TextComponent("vanilla recipe overrides"), false,
                                        () -> pack, metadataSection, Pack.Position.BOTTOM, PackSource.BUILT_IN, false)));
                    }
                }
            }
            catch(IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }
}
