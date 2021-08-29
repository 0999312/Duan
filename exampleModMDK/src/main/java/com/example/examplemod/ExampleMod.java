package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// 这个值应该与 META-INF/mods.toml 文件中定义的其中一个条目匹配。
@Mod("examplemod")
public class ExampleMod
{
	// 一个直接调用的 Log4j logger 实例。
	// 译者注：我们强烈建议写一个Getter来使其他类获取到这个Logger，这也是最推荐的做法。
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() 
    {
		// 注册模组加载中的启动方法
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// 注册模组加载中的“跨模组通信入队”方法
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// 注册模组加载中的“跨模组通信处理” 方法
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// 注册模组加载中的客户端加载方法
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// 将我们自己注册在服务器或其他我们有意图使用的游戏事件当中
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		// 一些预加载代码
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
		// 做一些只有客户端可以做的事情
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
		// 一些用于给其他模组发送“跨模组通信信息”的示例代码
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
		// 一些用于收集并处理“跨模组通信信息”的示例代码
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
	// 你可以使用@SubscribeEvent来让“事件总线”来发现需要调用的方法。
	// 译者注：使用它之后方法的参数只能是单一事件。
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) 
    {
		// 在服务器启动时搞点事情
        LOGGER.info("HELLO from server starting");
    }

	// 你可以使用@Mod.EventBusSubscriber来使容器类自动加载订阅的事件。
	// （这里向 Mod “事件总线”订阅事件来获取注册事件）
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) 
	{
			// 这里注册一个新方块。
			// 译者注：实际上并没有注册任何东西，但是我们成功写了一个 Hello World ！
            LOGGER.info("HELLO from Register Block");
        }
    }
}
