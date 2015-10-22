package me.ds.chunklagcheck;

import java.util.Comparator;

public class EntityCountComparator implements Comparator
{	
	@Override
	public int compare(Object o1, Object o2) 
	{
		ChunkData obj1 = (ChunkData) o1;
		ChunkData obj2 = (ChunkData) o2;
		
		return obj2.totalLivingEntities - obj1.totalLivingEntities;
	}	
}
