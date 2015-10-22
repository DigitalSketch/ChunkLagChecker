package me.ds.chunklagcheck;

import java.util.Comparator;

public class EntityCountComparator implements Comparator<ChunkData>
{	
	@Override
	public int compare(ChunkData o1, ChunkData o2) 
	{
		ChunkData obj1 = (ChunkData) o1;
		ChunkData obj2 = (ChunkData) o2;
		
		return obj2.totalLivingEntities - obj1.totalLivingEntities;
	}	
}
