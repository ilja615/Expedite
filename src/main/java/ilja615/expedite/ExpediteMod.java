package ilja615.expedite;

import ilja615.expedite.init.ModBlocks;
import ilja615.expedite.init.ModItems;
import ilja615.expedite.init.ModProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

import static ilja615.expedite.ExpediteMod.MOD_ID;

@Mod(MOD_ID)
public class ExpediteMod
{
    public static final String MOD_ID = "expedite";
    private static final List<Item> ITEMS_TO_REMOVE_CRAFTING = Arrays.asList(Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_SWORD,
        Items.STONE_AXE, Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_SWORD);

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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEvents {
        @SubscribeEvent
        public static void removeCraftingFromServer(RecipesUpdatedEvent event) {
            System.out.println("deleting recipes");
            event.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).removeIf(craftingRecipe -> ITEMS_TO_REMOVE_CRAFTING.contains(craftingRecipe.getResultItem()));
        }
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
    }
}
