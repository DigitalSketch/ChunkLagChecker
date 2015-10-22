package me.ds.chunklagcheck;

public class EntityData 
{
	public int totalEntities = 0;
	public String worldName = "";
	public int entityX = 0;
	public int entityY = 0;
	public int entityZ = 0;
	
	@Override
	public String toString()
	{
		return "Count: " + totalEntities + ", World: " + worldName + " | " + entityX + " " + entityY + " " + entityZ;		
	}
}
