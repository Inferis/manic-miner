# Manic Miner

This is a vein miner mod. To activate, sneak and mine. No special enchantment needed (although I could add it upon request).

The mod works serverside, so you (or your players) do not need to install this to work.

## Modes

This mod features 3 vein mining modes:
* regular ore veins (up to 128 blocks). This will also remove one layer of blocks next to the ore blocks to make it easier to collect ores. Includes nether ores.
* common block vein mining: makes it easy to create tunnels. Will mine up to 128 blocks of the same type in a cilinder shape in front of you (or above or below you). This works for a number of common building blocks (e.g. stone, andesite, deep slate, netherrack, end stone, tuff, ice, ...).
* trees: cut down whole trees in one go (192 block maximum).

You need to be using the right tool for vein mining to work (e.g. pickaxe for ores, shovel for dirt, axe for logs, ...). The mod respects the durability of the tool: once it runs out, you're done vein mining (obviously).

### Configurable

* The amount of blocks for each type is configurable
* The mining of the extra layer of blocks around ore veins can be turned off.
* The mod can be configured to require an enchantment. This is disabled by default.

#### Config options

```
{
  "mustSneak": true,
  "allowCommonBlocks": true,
  "removeCommonBlocksAroundOre": true,
  "requireEnchantment": false,
  "maxVeinSize": 128,
  "maxCommonSize": 128,
  "maxWoodSize": 192,
  "summonItems": true
}
```

*mustSneak*: if enabled, the player must sneak to vein mine. Otherwise the normal mining operation is vein mining. You probably don't want that, but hey. Defaults to true.

*allowCommonBlocks*: allow non-ore blocks (stone, granite, deepslate etc) to be vein mined. In this mode, the vein mining works a bit like creating a tunnel in the available type of block. It will still limit itself to the type of the originally vein mined block. Defaults to true.

*removeCommonBlocksAroundOre*: if enabled, will remove normal blocks around an ore vein to create more space and make it easier to get the vein mined ore blocks. Defaults to true.

*requireEnchantment*: indicates if the "Manic Mining" enchantment has to be present on the tool in order for vein mining to work. Defaults to false. 

*maxVeinSize*: the maximum number of blocks to mine in an ore vein. Defaults to 128.

*maxCommonSize*: the maximum number of blocks to mine in the "allowCommonBlocks" scenario. Defaults to 128.

*maxWoodSize*: the maximum number of wood blocks when vein mining a tree. This is slightly more than the standard sizes: trees can contain a lot of wood! Defaults to 192.

*summonItems*: determines if all vein mined blocks should be teleported to the initial vein mining location (where you mine the first block).

## Modpacks

Feee free to use this in a modpack, just send me a ping, it's always nice to know if someone uses your code.

## Download

This mod is available on [Modrinth](https://modrinth.com/project/manic-miner).