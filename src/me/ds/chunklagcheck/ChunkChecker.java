package me.ds.chunklagcheck;

/*
 * Chunk Lag Checker
 * Checks chunks for large amount of entities
 * Developer: James Carson
 * Date: 2015-OCT-16
 * 
 * Change Log
 * 1.1.0 = added the list # argument option to allow for list of multiple chunks
 * 1.1.1 = updated permissions (plugin.yml)
 * 1.1.2 = Shortened sendMessage to fit to one line in MC Chat
 * 1.2.0 = Added chunk radius, ID-10-T error function, added argument to search around player, added argument to check for ALL entities, added argument to get details
 * 1.2.1 = Added the 'nearby' argument to allow the moderator to see what players are in render distance of them.  Deafulted the list argument to be set to 5 if no number is passed.  Cleaned up some code
 * 1.2.2 = Added the Wither checker :)
 * 
 */

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkChecker extends JavaPlugin
{

	Player player = null;
	
	// Logger for chat messages
	public final Logger logger = Logger.getLogger("Minecraft");
	
	// Server Plug in Description File
	PluginDescriptionFile pdFile = getDescription();

	// Method called when the plug in is enabled.
	public void onEnable()
	{
		this.logger.info(pdFile.getName() + " ver " + pdFile.getVersion() + " Enabled");
	}	
	
	// Method called when plug in is disabled.
	public void onDisable()
	{
		this.logger.info(pdFile.getName() + " Disabled");
	}

	// Function called when a command is entered into the MC chat
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		
		// Adds a log to the console
		this.logger.info(sender.getName() + " used ChunkLagChecker: " + label);
		
		// Checks to see if an actual player is sending the command
		if(!(sender instanceof Player)) 
		{
			sender.sendMessage("Who are you?  Can't run this command from console.");
			return false;
		}
		
		// Assigns the player from the sender of the command
		player = (Player) sender; // Grab the player class from the sender of the command
		
		/*
		 * 
		 * Check for the commands passed
		 * 
		 */
		
		// passed command runs the wither checker
		if(label.equalsIgnoreCase("wither"))
		{
			if(!player.hasPermission("entitycheck.clc"))
			{
				player.sendMessage("You don't have the permission to use this command.");
				return false;
			}
			
			checkForEntityType("wither");
			return true;
			
		}
		
		// passed command runs the nearby command
		else if(label.equalsIgnoreCase("nearby"))
		{
			if(!player.hasPermission("entitycheck.clc"))
			{
				player.sendMessage("You don't have the permission to use this command.");
				return false;
			}
			
			checkNearbyPlayers();
			return true;
			
		}
		
		// passed command runs the ChunkLagChecker command
		else if(label.equalsIgnoreCase("clc")) // If the user passes the clc command
		{
			// Check to see if the player has permissions
			if(!player.hasPermission("entitycheck.clc")) 
			{
				player.sendMessage("Dude, you don't have the access!");
				return false;
			}

			
			/*
			 * 
			 * All values are good, run the command
			 * 
			 */
			
			int listCount = 1; // If the list argument is passed, this value stores the number in the list.  Default must = 1;
			int defaultListCount = 5; // The default list count if /clc l is passed without a number
			int chunkRadius = 0; // If doChunkRadius is true, this value is used to search a radius (max 2). Default must = 0;	
			boolean usePlayer = false; // If we want to check the chunks around the mod executing the command
			boolean checkAllEntities = false; // If we want to check all entities, not just living ones
			boolean showDetails = false; // Shows the details of the entities.  checkAllEntities will be defaulted to true, and list count will be seet to 1
			
			
			if(args.length >= 1) // If we have arguments
			{
				
				if(args.length == 1 && strIsNumber(args[0].toString())) // If the only argument is a number, execute the list argument
				{
					listCount = Integer.parseInt(args[0].toString());
				}
				else
				{
				
					// Loops through the arguments
					for(int a = 0; a < args.length; a++) 
					{
						String myArg = args[a].toString(); // Put the argument in a var
						
					// HELP
						// Display the help for the plug in. arg: help or h
						if(myArg.equalsIgnoreCase("help") || myArg.equalsIgnoreCase("h"))
						{
							player.sendMessage(ChatColor.GOLD + "Help for " + pdFile.getName() + " ver " + pdFile.getVersion());
							player.sendMessage(ChatColor.GOLD + "/clc i" + ChatColor.WHITE + " : Displays plugin info");
							player.sendMessage(ChatColor.GOLD + "/clc l <#>" + ChatColor.WHITE + " : Displays # of chunks from top to bottom");
							player.sendMessage(ChatColor.GOLD + "/clc r <#>" + ChatColor.WHITE + " : Searches radius around the chunks found (1 or 2)");
							player.sendMessage(ChatColor.GOLD + "/clc a" + ChatColor.WHITE + " : Counts all entities, not just living");
							player.sendMessage(ChatColor.GOLD + "/clc d" + ChatColor.WHITE + " : Displays details of all entities in top chunk");
							player.sendMessage(ChatColor.GOLD + "/clc p" + ChatColor.WHITE + " : Searches chunks around the player");
							player.sendMessage(ChatColor.GOLD + "/clc n" + ChatColor.WHITE + " : Returns a list of all players nearby");
							
							return false;
						}
						
					// INFO
						// Display Info arg: info or i
						else if(myArg.equalsIgnoreCase("info") || myArg.equalsIgnoreCase("i")) 
						{
							player.sendMessage(ChatColor.GREEN + pdFile.getName() + " ver " + pdFile.getVersion() + " by your favorite DigitalSketch!");
							return false;
						}
						
					// Plug-in LOVE
						// Display love! arg: <3
						else if(myArg.equalsIgnoreCase("<3")) 
						{
							player.sendMessage(ChatColor.AQUA + "Ahhh DigitalSketch <3's you too!!!");
							return false;
						}
						
					// NEARBY
						// Check for the players in a radius of the chunks where the moderator is
						else if(myArg.equalsIgnoreCase("nearby") || myArg.equalsIgnoreCase("n"))
						{
							checkNearbyPlayers();
							return false;
						}
						
					// WITHER
						// Checks to see where withers are
						else if(myArg.equalsIgnoreCase("wither") || myArg.equalsIgnoreCase("w"))
						{
							checkForEntityType("wither");
							return false;
						}
						
					// LIST
						// List the # of checks arg: list or l
						else if(myArg.equalsIgnoreCase("list") || myArg.equalsIgnoreCase("l")) 
						{
							a++;
							
							if(a >= args.length)
							{
								listCount = defaultListCount;
							}
							else if(strIsNumber(args[a].toString())) // Check to see if the second arg is a number 
							{
								listCount = Integer.parseInt(args[a].toString());
								if(listCount > 10) { listCount = 10; }
							}
							else if (nextArgumentIsValid(args[a].toString()))
							{
								a--;
								listCount = defaultListCount;
							}
							else // If there is a second argument, but it's not a number....
							{
								displayIdiotError(args[a].toString() + " aint bein' a number!");
								return false;
							}
						}
						
					// RADIUS
						// Display a radius of chunks arg: radius or r
						else if(myArg.equalsIgnoreCase("radius") || myArg.equalsIgnoreCase("r")) 
						{
							a++;
							
							if(args[a] == null) // No argument entered for radius
							{
								displayIdiotError("You need to enter a radius. (1 or 2)");
								return false;
							}
							else if(strIsNumber(args[a].toString())) // Check to see if the second arg is a number 
							{
								chunkRadius = Integer.parseInt(args[a].toString());
							}
							else // If there is a second argument, but it's not a number....
							{
								displayIdiotError(args[a].toString() + " aint bein' a number!");
								return false;
							}
						}
						
					// ALL
						// If we want to check all entities arg: all or a
						else if(myArg.equalsIgnoreCase("all") || myArg.equalsIgnoreCase("a"))
						{
							checkAllEntities = true;
						}
						
					// PLAYER
						// If we want to check around the moderator throwing the command only arg: player or p
						else if(myArg.equalsIgnoreCase("player") || myArg.equalsIgnoreCase("p"))
						{
							usePlayer = true;
							listCount = 1;
							chunkRadius = 2;
						}
						
					// DETAIL
						// List the details of all entities in a chunk arg: detail or d
						else if(myArg.equalsIgnoreCase("detail") || myArg.equalsIgnoreCase("d"))
						{
							showDetails = true;
							checkAllEntities = true;
							listCount = 1;
							//chunkRadius = 0;
						}
						
					// Catch invalid args
						// We don't recognize the argument
						else
						{
							displayIdiotError("'" + myArg + "' isn't a valid argument, did you mean to do that?");
							return false;
						}	
						
					} // End of the for(int a = 0; a < args.length; a++) loop					
				}
			}
			
			// Calculate the data based on the args sent
			getLoadedEntities(listCount, chunkRadius, usePlayer, checkAllEntities, showDetails);
			
		}
		
		return true;

	}
	
	// Function to check if the next argument is valid
	public boolean nextArgumentIsValid(String arg)
	{
		switch(arg) {
			case "a":
			case "all":
			case "r":
			case "radius":
			case "l":
			case "list":
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	// Checks to see if a string is an integer
	public boolean strIsNumber(String str)
	{
		try
		{
			int num = Integer.parseInt(str);
		} 
		catch (NumberFormatException err) 
		{
			return false;
		}
		
		return true;
	}
	
	// Display an ID-10-T Error in chat
	public void displayIdiotError(String err)
	{
		player.sendMessage(ChatColor.LIGHT_PURPLE + "ID-10-T Error: " + err);
	}
	
	// Checks to see if an item exists in a current ArrayList
	public int checkValueInEntityDetailsArray(String val, ArrayList<EntityDetails> list)
	{
		int returnIndex = -1;
		
		if(list.size() == 0) { 
			return -1; 
		}
		
		for(int i = 0; i < list.size(); i++)
		{
			EntityDetails myDets = (EntityDetails) list.get(i);
			if(myDets.entityName.equalsIgnoreCase(val))
			{
				returnIndex = i;
				break;
			}
		}
		
		return returnIndex;
	}
	
	// Function that calculates the entity counts (New)
	public void getLoadedEntities(int listTot, int radiusDistance, boolean usePlayer, boolean checkAll, boolean showDetails)
	{
		
		player.sendMessage(ChatColor.DARK_AQUA + "Getting results of query...");		
		
		
		// Setup the array
		ArrayList<ChunkData> chunkDataList = new ArrayList<ChunkData>(); // Array list for the chunk data
		
		
		/*
		 * 
		 * Get all the chunk data depending on args passed
		 * 
		 */
		
		
		// If they didn't enter a chunk radius, get single chunks...
		if(radiusDistance == 0) 
		{
			chunkDataList = getChunksWithNoRadius(checkAll);
		}
		
		// Else get the radius of chunks
		else 
		{
			if(radiusDistance > 2) { radiusDistance = 2; }			
			chunkDataList = getChunksWithRadius(radiusDistance, checkAll, usePlayer);
		}
		
		
		/*
		 * 
		 * Display the data found
		 * 
		 */
		

	    // Sort the array to keep the largest entity counts first
	    Collections.sort(chunkDataList, new EntityCountComparator());
	    
	    
	    // Display the details header message
	    if (showDetails) 
	    {
	    	player.sendMessage(ChatColor.DARK_AQUA + ((usePlayer) ? "Detailed results of the chunks around you:" : "Details of the chunk with the most entities:"));
	    }
	    
	    // Display the header message based on the arguments passed	    
	    else if(usePlayer) // If we're omly using the chunks around the player
	    {
	    	player.sendMessage(ChatColor.DARK_AQUA + "Results of the chunks around you:");
	    }	    
	    else if(listTot == 1) // User only wants the top chunk, so display this message
	    {	    	
	    	player.sendMessage(ChatColor.DARK_AQUA + "The " + ((radiusDistance==0) ? "chunk" : "chunks in a radius of " + radiusDistance) + " with the most " + ((checkAll) ? "" : "living ") + "entities:");
	    }
	    else // There are more than one chunks being displayed
	    {
	    	player.sendMessage(ChatColor.DARK_AQUA + ((radiusDistance == 0) ? "The " + listTot + " chunks with the most " + ((checkAll) ? "" : "living ") + "entities are:" : "The " + listTot + " chunks in a radius of " + radiusDistance + " with the most " + ((checkAll) ? "" : "living ") + "entities are:"));
	    }
	    	 
	    
	    displayDataToUser(chunkDataList, listTot, radiusDistance, showDetails);
	    
	}
	
	// Function that doesn't use the Radius arg and returns the chunk data in an Array
	public ArrayList<ChunkData> getChunksWithNoRadius(boolean allEntities) 
	{
		List<World> worlds = Bukkit.getWorlds(); // Gets all the worlds loaded	
	    int a, b, i; // ints for 'for' loops
	    ArrayList<ChunkData> chunkDataList = new ArrayList<ChunkData>(); // Array list for the chunk data
	    
	    // Loop through the worlds
	    for(a = 0; a < worlds.size(); a++)
	    {
	    	World currentWorld = (World) worlds.get(a); // Current world in the list
	    	Chunk[] loadedChunks = currentWorld.getLoadedChunks(); // All of the chunks in the current world
	    	
	    	
	    	// Loop through all the loaded chunks of the selected world
		    for (i = 0; i < loadedChunks.length; i++)
		    {
		    	Chunk currentChunk = loadedChunks[i]; // Current loaded chunk in the list			    	
		    	Entity[] ents = currentChunk.getEntities(); // All the entities in the current chunk			    	
		    	int totalLiving = 0; // Max count of the LIVING entities (if 'a' isn't selected)

		    	
		    	if(allEntities) 
		    	{
	    			totalLiving = ents.length;
	    		}
	    		else
	    		{		    	
		    		// Show only the living entities
			    	for(b = 0; b < ents.length; b++)
			    	{			    		
			    		if(ents[b] instanceof LivingEntity) // if the entity is living
			    		{
			    			totalLiving += 1; // Add 1 to the total living entities
			    		}
			    	}
		    	}
		    	
		    	// Store the data so it can be sorted in a multidimensional array
		    	ChunkData subData = new ChunkData();
		    	
		    	int chunkX = ((currentChunk.getX() * 16) + 8); // X Center of the chunk
				int chunkZ = ((currentChunk.getZ() * 16) + 8); // Z Center of the chunk
		    	
		    	subData.totalLivingEntities = totalLiving; // Total Count
		    	subData.worldName = currentWorld.getName(); // Name of the world the count is in
		    	subData.chunkCenterX = chunkX; // Center X of the chunk
		    	subData.chunkCenterZ = chunkZ; // Center X of the chunk
		    	subData.entities = ents; // The array of entities
		    	
		    	chunkDataList.add(subData);

		    }
	    }
	    
	    return chunkDataList;
	}
	
	// Function uses the Radius arg and returns the array of the chunk data
	public ArrayList<ChunkData> getChunksWithRadius(int radius, boolean allEntities, boolean aroundPlayer)
	{
		List<World> worlds = Bukkit.getWorlds(); // Gets all the worlds loaded	
	    int a, c; // ints for 'for' loops
	    ArrayList<ChunkData> chunkDataList = new ArrayList<ChunkData>(); // Array list for the chunk data
	    int chunkRadius=(radius >> 4) + 1;
	    
	    if(aroundPlayer) // We're searching around the player running the command
	    {
	    	World playerWorld = player.getWorld();
	    	Chunk playerChunk = playerWorld.getChunkAt(player.getLocation());
	    	
	    	// Get the range of chunks based on the X and Z values of the player
	    	int startX = playerChunk.getX() - chunkRadius;
			int startZ = playerChunk.getZ() - chunkRadius;
			int endX = playerChunk.getX() + chunkRadius;
			int endZ = playerChunk.getZ() + chunkRadius;
			int totalLiving = 0; // Max count of the LIVING entities (if 'a' isn't selected)
			ArrayList<Entity> entityArray = new ArrayList<Entity>();
			
			for (int x=startX; x <= endX; ++x) 
			{
				for (int z=startZ; z <= endZ; ++z) 
				{
					Chunk daChunk = playerWorld.getChunkAt(x,z);
					
					if (daChunk == null) continue;
					
					for (Entity entity : daChunk.getEntities()) 
					{
						if(allEntities)
						{
							entityArray.add(entity);
							totalLiving++;
						}
						else
						{
							if (entity instanceof LivingEntity)
							{
								entityArray.add(entity);
								totalLiving++;
							}
						}
					}
				}
			}
			
			// Store the data so it can be sorted in a multidimensional array
	    	ChunkData subData = new ChunkData();
	    	Entity[] myEntities = new Entity[entityArray.size()];
	    	myEntities = entityArray.toArray(myEntities);
	    	
	    	int centerX = (int) Math.round(startX + ((endX - startX) * 0.5));
	    	int centerZ = (int) Math.round(startZ + ((endZ - startZ) * 0.5));
	    	
	    	int chunkX = ((centerX * 16) + 8); // X Center of the chunk
			int chunkZ = ((centerZ * 16) + 8); // Z Center of the chunk
	    	
			// Insert the data into the ChunkData class
	    	subData.totalLivingEntities = totalLiving; // Total Count
	    	subData.worldName = playerWorld.getName(); // Name of the world the count is in
	    	subData.chunkCenterX = chunkX; // Center X of the chunk
	    	subData.chunkCenterZ = chunkZ; // Center X of the chunk
	    	//subData.entities = chunk.getEntities(); // The array of entities
	    	subData.entities = myEntities;
	    	
	    	chunkDataList.add(subData);
	    }
	    else // We're not searching around a player, so get all the available chunk data.
	    {
	    
		    // Loop through the worlds
		    for(a = 0; a < worlds.size(); a++)
		    {
		    	World currentWorld = (World) worlds.get(a); // Current world in the list
		    	Chunk[] loadedChunks = currentWorld.getLoadedChunks(); // All of the chunks in the current world
		    	
		    	for (c = 0; c < loadedChunks.length; c++)
		    	{
		    		Chunk chunk = (Chunk)loadedChunks[c];
		    		
		    		// Get the range of chunks based on the X and Z values
					int startX = chunk.getX() - chunkRadius;
					int startZ = chunk.getZ() - chunkRadius;
					int endX = chunk.getX() + chunkRadius;
					int endZ = chunk.getZ() + chunkRadius;
					int totalLiving = 0; // Max count of the LIVING entities (if 'a' isn't selected)
					
					ArrayList<Entity> entityArray = new ArrayList<Entity>();
					
					for (int x=startX; x <= endX; ++x) 
					{
						for (int z=startZ; z <= endZ; ++z) 
						{
							chunk = currentWorld.getChunkAt(x,z);
							
							if (chunk == null) continue;
							
							for (Entity entity : chunk.getEntities()) 
							{
								if(allEntities)
								{
									entityArray.add(entity);
									totalLiving++;
								}
								else
								{
									if (entity instanceof LivingEntity)
									{
										entityArray.add(entity);
										totalLiving++;
									}
								}
							}
						}
					}
					
					// Store the data so it can be sorted in a multidimensional array
			    	ChunkData subData = new ChunkData();
			    	Entity[] myEntities = new Entity[entityArray.size()];
			    	myEntities = entityArray.toArray(myEntities);
			    	
			    	int centerX = (int) Math.round(startX + ((endX - startX) * 0.5));
			    	int centerZ = (int) Math.round(startZ + ((endZ - startZ) * 0.5));
			    	
			    	int chunkX = ((centerX * 16) + 8); // X Center of the chunk
					int chunkZ = ((centerZ * 16) + 8); // Z Center of the chunk
			    	
					// Insert the data into the ChunkData class
			    	subData.totalLivingEntities = totalLiving; // Total Count
			    	subData.worldName = currentWorld.getName(); // Name of the world the count is in
			    	subData.chunkCenterX = chunkX; // Center X of the chunk
			    	subData.chunkCenterZ = chunkZ; // Center Z of the chunk
			    	//subData.entities = chunk.getEntities(); // The array of entities
			    	subData.entities = myEntities;
			    	
			    	chunkDataList.add(subData);
					
		    	}
		    }
	    }
	    
	    return chunkDataList;
	}
	
	// Displays the data found to the user
	public void displayDataToUser(ArrayList<ChunkData> chunkDataList, int listTot, int radiusDistance, boolean showDetails)
	{
		
		ChunkData data; // The chunk data
	    
	    // Loop through the chunks and display them in chat
	    for(int p = 0; p < listTot; p++)
	    {
	    	data = (ChunkData) chunkDataList.get(p); 
	    	
	    	// Truncate the start of Mumbo's server world names
	    	String trunkWorld = data.worldName;
	    	if(trunkWorld.indexOf("patreonServer") != -1)
	    	{
	    		trunkWorld = trunkWorld.replace("patreonServer", "");
	    	}
	    	
	    	// Generate the message
	    	String modMsg = ChatColor.WHITE + "In " + ChatColor.AQUA + trunkWorld + ChatColor.WHITE + ": ";    	
	    	modMsg += "chunk" + ((radiusDistance == 0) ? " has " : "s have ");	    	
	    	modMsg += "" + ChatColor.RED + data.totalLivingEntities + ChatColor.WHITE + " entities. Center: " + ChatColor.AQUA + "X=" + data.chunkCenterX + ", Z=" + data.chunkCenterZ;
	    	
	    	player.sendMessage(modMsg);		    	
	    }
	    
	    // If we are getting details, show the details	    
	    if(showDetails)
	    {
	    	data = (ChunkData) chunkDataList.get(0);
	    	ArrayList<EntityDetails> detailsArray = new ArrayList<EntityDetails>();

	    	// Loop through the entities to get the details of each
	    	for(int e = 0; e < data.entities.length; e++)
	    	{
	    		Entity curEntity = (Entity) data.entities[e];
	    		EntityDetails eDets = new EntityDetails();

	    		// If the entity is a player, grab the user's name so we know who it is, then Sig can BAN!
	    		eDets.entityName = curEntity.getType().name().toLowerCase();
	    		if(curEntity instanceof Player)
	    		{
	    			Player thePlayer = (Player) curEntity;
	    			eDets.entityName = curEntity.getType().name().toLowerCase() + " (" + thePlayer.getDisplayName() + ")";
	    		}
	    		
	    		// Grabs the current index in the array if there is currently one there, or returns -1 if it's not found
	    		int itemAt = checkValueInEntityDetailsArray(eDets.entityName, detailsArray);
	    		
	    		if(itemAt != -1) // Entity was found, so we can update the count
	    		{
	    			EntityDetails newDets = (EntityDetails)detailsArray.get(itemAt);	    			
	    			int tempVal = newDets.entityCount;	    			
	    			tempVal++;
	    			
	    			newDets.entityCount = tempVal;
	    			detailsArray.set(itemAt, newDets);

	    		}
	    		else // Entity wasn't found, so lets a new one
	    		{
	    			eDets.entityCount = 1;
	    			detailsArray.add(eDets);
	    		}
	    	}

	    	// Sorts the array based on the count per entity
	    	Collections.sort(detailsArray, new DictionaryComparator());
	    	
	    	// We have the data to be listed, so show it to the uer
	    	player.sendMessage(ChatColor.WHITE + "There are/is:");
	    	for(int j = 0; j < detailsArray.size(); j++)
	    	{
	    		player.sendMessage("" + ChatColor.RED + detailsArray.get(j).entityCount + " " + ChatColor.WHITE + detailsArray.get(j).entityName + ((detailsArray.get(j).entityCount > 1) ? "s" : ""));
	    	}
	    	
	    }
	}
	
	// Checks for the players near the moderator within 10 chunks running the command
	public void checkNearbyPlayers()
	{
		World playerWorld = player.getWorld();
    	Chunk playerChunk = playerWorld.getChunkAt(player.getLocation());
    	int viewableChunks = Bukkit.getViewDistance();
    	
    	// Get the range of chunks based on the X and Z values of the player
    	int startX = playerChunk.getX() - viewableChunks;
		int startZ = playerChunk.getZ() - viewableChunks;
		int endX = playerChunk.getX() + viewableChunks;
		int endZ = playerChunk.getZ() + viewableChunks;
		ArrayList<Entity> entityArray = new ArrayList<Entity>();
		
		for (int x=startX; x <= endX; ++x) 
		{
			for (int z=startZ; z <= endZ; ++z) 
			{
				Chunk daChunk = playerWorld.getChunkAt(x,z);
				
				if (daChunk == null) continue;
				
				for (Entity entity : daChunk.getEntities()) 
				{
					if (entity instanceof Player)
					{
						if(entity != player)
						{
							entityArray.add(entity);
						}
					}
				}
			}
		}
		
		// Store the data so it can be sorted in a multidimensional array
    	Entity[] myEntities = new Entity[entityArray.size()];
    	myEntities = entityArray.toArray(myEntities);
    	
    	if(myEntities.length == 0)
    	{
    		player.sendMessage(ChatColor.AQUA + "There are currently no players in the area. (besides  you)");
    	}
    	else
    	{
    		player.sendMessage(ChatColor.WHITE + "There " + ((myEntities.length == 1) ? "is " : "are ") + ChatColor.RED + myEntities.length + ChatColor.WHITE + " player" + ((myEntities.length == 1) ? "" : "s") + " around you:");
    		
    		// Loop through the players and message the player
        	for(int i = 0; i < myEntities.length; i++)
        	{
        		Player thePlayer = (Player) myEntities[i];        		
        		player.sendMessage(" - " + thePlayer.getDisplayName());
        	}
    	}
	}
	
	// Checks for the players near the moderator within 10 chunks running the command
	public void checkForEntityType(String entityToSearchFor)
	{
		List<World> worlds = Bukkit.getWorlds(); // Gets all the worlds loaded	
	    int a, b, i; // ints for 'for' loops
	    EntityData entityData = new EntityData();
	    
	    // Loop through the worlds
	    for(a = 0; a < worlds.size(); a++)
	    {
	    	World currentWorld = (World) worlds.get(a); // Current world in the list
	    	Chunk[] loadedChunks = currentWorld.getLoadedChunks(); // All of the chunks in the current world
	    	
	    	// Loop through all the loaded chunks of the selected world
		    for (i = 0; i < loadedChunks.length; i++)
		    {
		    	Chunk currentChunk = loadedChunks[i]; // Current loaded chunk in the list			    	
		    	Entity[] ents = currentChunk.getEntities(); // All the entities in the current chunk
	    	
	    		// Show only the living entities
		    	for(b = 0; b < ents.length; b++)
		    	{			    		
		    		if(entityToSearchFor == "wither" && ents[b] instanceof Wither)
		    		{
		    			entityData.entityX = ents[b].getLocation().getBlockX();
		    			entityData.entityY = ents[b].getLocation().getBlockY();
		    			entityData.entityZ = ents[b].getLocation().getBlockZ();
		    			entityData.worldName = currentWorld.getName();
		    			entityData.totalEntities++;
		    		}
		    	}
		    }
	    }
	    
	    if(entityData.totalEntities == 0)
	    {
	    	player.sendMessage(ChatColor.AQUA + "No " + entityToSearchFor + "s were found.");
	    }
	    else
	    {
	    	player.sendMessage(ChatColor.WHITE + "There " + ((entityData.totalEntities > 1) ? "are" : "is") + " " + ChatColor.RED + (entityData.totalEntities + 1) + ChatColor.AQUA + " " + entityToSearchFor + ((entityData.totalEntities > 1) ? "s" : "") + ChatColor.WHITE + " in the " + ChatColor.AQUA + entityData.worldName + ChatColor.WHITE + " at about " + ChatColor.AQUA + entityData.entityX + " " + entityData.entityY + " " + entityData.entityZ);
	    	
	    }
	}

}
