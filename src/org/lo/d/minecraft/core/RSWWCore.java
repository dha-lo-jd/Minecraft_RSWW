package org.lo.d.minecraft.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import org.lo.d.minecraft.block.BlockRSWWReceiver;
import org.lo.d.minecraft.block.BlockRSWWSender;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(
		modid = "BlockRSWW",
		name = "Block RedStone Wireless Watcher",
		version = "0.0.1")
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false)
public class RSWWCore {

	@Instance("RSWWCore")
	public static RSWWCore instance;

	public static final Block blockRSWWReceiver = (new BlockRSWWReceiver(2120, Material.iron)).setUnlocalizedName(
			"rsww:blockRSWWReceiver")
			.setCreativeTab(CreativeTabs.tabRedstone);

	public static final Block blockRSWWSender = (new BlockRSWWSender(2121, Material.iron)).setUnlocalizedName(
			"rsww:blockRSWWSender")
			.setCreativeTab(CreativeTabs.tabRedstone);

	@Mod.Init
	public void init(FMLInitializationEvent event)
	{

		GameRegistry.registerBlock(blockRSWWReceiver, "RSWWBlockReceiver");
		LanguageRegistry.addName(blockRSWWReceiver, "RedStone Wireless Receiver");
		LanguageRegistry.instance().addNameForObject(blockRSWWReceiver, "ja_JP", "ワイヤレスRSレシーバー");

		GameRegistry.registerBlock(blockRSWWSender, "RSWWBlockSender");
		LanguageRegistry.addName(blockRSWWSender, "RedStone Wireless Sender");
		LanguageRegistry.instance().addNameForObject(blockRSWWSender, "ja_JP", "ワイヤレスRSセンダー");
	}

}
