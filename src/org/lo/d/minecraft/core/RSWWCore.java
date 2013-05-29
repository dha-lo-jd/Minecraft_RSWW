package org.lo.d.minecraft.core;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lo.d.commons.configuration.ConfigurationSupport;
import org.lo.d.commons.configuration.ConfigurationSupport.BlockIdConfig;
import org.lo.d.minecraft.block.BlockRSWWReceiver;
import org.lo.d.minecraft.block.BlockRSWWSender;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "RSWW", name = "Block RedStone Wireless Watcher", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
@ConfigurationSupport.ConfigurationMod
public class RSWWCore {

	@Instance("RSWW")
	public static RSWWCore instance;

	public static Block blockRSWWReceiver;

	public static Block blockRSWWSender;

	@BlockIdConfig(defaultValue = 2120, name = "blockReceiverId")
	private static int blockRSWWReceiverId;

	@BlockIdConfig(defaultValue = 2121, name = "blockSenderId")
	private static int blockRSWWSenderId;

	@Mod.Init
	public void init(FMLInitializationEvent event) {
		blockRSWWReceiver = (new BlockRSWWReceiver(blockRSWWReceiverId, Material.iron)).setUnlocalizedName(
				"rsww:blockRSWWReceiver").setCreativeTab(CreativeTabs.tabRedstone);
		blockRSWWSender = (new BlockRSWWSender(blockRSWWSenderId, Material.iron)).setUnlocalizedName(
				"rsww:blockRSWWSender").setCreativeTab(CreativeTabs.tabRedstone);

		GameRegistry.registerBlock(blockRSWWReceiver, "RSWWBlockReceiver");
		LanguageRegistry.addName(blockRSWWReceiver, "RedStone Wireless Receiver");
		LanguageRegistry.instance().addNameForObject(blockRSWWReceiver, "ja_JP", "ワイヤレスRSレシーバー");

		GameRegistry.addRecipe(new ItemStack(blockRSWWReceiver, 2), new Object[] {
				"BBB",
				"BPB",
				"BBB",
				Character.valueOf('P'),
				Item.redstoneRepeater,
				Character.valueOf('B'),
				Item.redstone
		});

		GameRegistry.registerBlock(blockRSWWSender, "RSWWBlockSender");
		LanguageRegistry.addName(blockRSWWSender, "RedStone Wireless Sender");
		LanguageRegistry.instance().addNameForObject(blockRSWWSender, "ja_JP", "ワイヤレスRSセンダー");

		GameRegistry.addShapelessRecipe(new ItemStack(blockRSWWSender, 1), new Object[] {
			new ItemStack(blockRSWWReceiver, 1)
		});
		GameRegistry.addShapelessRecipe(new ItemStack(blockRSWWReceiver, 1), new Object[] {
			new ItemStack(blockRSWWSender, 1)
		});
	}

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent event) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		ConfigurationSupport.load(getClass(), event);
	}

}
