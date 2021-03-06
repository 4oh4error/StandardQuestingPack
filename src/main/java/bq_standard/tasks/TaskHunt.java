package bq_standard.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import org.apache.logging.log4j.Level;
import betterquesting.client.gui.GuiQuesting;
import betterquesting.client.gui.misc.GuiEmbedded;
import betterquesting.quests.tasks.advanced.AdvancedTaskBase;
import betterquesting.utils.ItemComparison;
import betterquesting.utils.JsonHelper;
import betterquesting.utils.NBTConverter;
import bq_standard.client.gui.editors.GuiHuntEditor;
import bq_standard.client.gui.tasks.GuiTaskHunt;
import bq_standard.core.BQ_Standard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TaskHunt extends AdvancedTaskBase
{
	public HashMap<UUID, Integer> userProgress = new HashMap<UUID, Integer>();
	public String idName = "Zombie";
	public int required = 1;
	public boolean ignoreNBT = true;
	public boolean subtypes = true;
	
	/**
	 * NBT representation of the intended target. Used only for NBT comparison checks
	 */
	public NBTTagCompound targetTags = new NBTTagCompound();
	
	@Override
	public String getUnlocalisedName()
	{
		return "bq_standard.task.hunt";
	}
	
	@Override
	public void onKilledByPlayer(EntityLivingBase entity, DamageSource source)
	{
		EntityPlayer player = (EntityPlayer)source.getEntity();
		
		if(player == null || entity == null || this.isComplete(player.getUniqueID()))
		{
			return;
		}
		
		Integer progress = userProgress.get(player.getUniqueID());
		progress = progress == null? 0 : progress;
		
		Class<? extends Entity> subject = entity.getClass();
		@SuppressWarnings("unchecked")
		Class<? extends Entity> target = (Class<? extends Entity>)EntityList.stringToClassMapping.get(idName);
		
		if(subject == null || target == null)
		{
			return; // Missing necessary data
		} else if(subtypes && !target.isAssignableFrom(subject))
		{
			return; // This is not the intended target or sub-type
		} else if(!subtypes && !EntityList.getEntityString(entity).equals(idName))
		{
			return; // This isn't the exact target required
		}
		
		NBTTagCompound subjectTags = new NBTTagCompound();
		entity.writeToNBTOptional(subjectTags);
		if(!ignoreNBT && !ItemComparison.CompareNBTTag(targetTags, subjectTags, true))
		{
			return;
		}
		
		progress++;
		
		userProgress.put(player.getUniqueID(), progress);
		
		if(progress >= required)
		{
			this.completeUsers.add(player.getUniqueID());
		}
	}
	
	public void AddKill(EntityPlayer player, int count)
	{
		if(userProgress.containsKey(player.getUniqueID()))
		{
			userProgress.put(player.getUniqueID(), userProgress.get(player.getUniqueID()) + count);
		} else
		{
			userProgress.put(player.getUniqueID(), count);
		}
	}
	
	@Override
	public void writeToJson(JsonObject json)
	{
		super.writeToJson(json);
		
		json.addProperty("target", idName);
		json.addProperty("required", required);
		json.addProperty("subtypes", subtypes);
		json.addProperty("ignoreNBT", ignoreNBT);
		json.add("targetNBT", NBTConverter.NBTtoJSON_Compound(targetTags, new JsonObject()));
		
		JsonArray progArray = new JsonArray();
		for(Entry<UUID,Integer> entry : userProgress.entrySet())
		{
			JsonObject pJson = new JsonObject();
			pJson.addProperty("uuid", entry.getKey().toString());
			pJson.addProperty("value", entry.getValue());
			progArray.add(pJson);
		}
		json.add("userProgress", progArray);
	}
	
	@Override
	public void readFromJson(JsonObject json)
	{
		super.writeToJson(json);
		
		idName = JsonHelper.GetString(json, "target", "Zombie");
		required = JsonHelper.GetNumber(json, "required", 1).intValue();
		subtypes = JsonHelper.GetBoolean(json, "subtypes", true);
		ignoreNBT = JsonHelper.GetBoolean(json, "ignoreNBT", true);
		targetTags = NBTConverter.JSONtoNBT_Object(JsonHelper.GetObject(json, "targetNBT"), new NBTTagCompound());
		
		userProgress = new HashMap<UUID,Integer>();
		for(JsonElement entry : JsonHelper.GetArray(json, "userProgress"))
		{
			if(entry == null || !entry.isJsonObject())
			{
				continue;
			}
			
			UUID uuid;
			try
			{
				uuid = UUID.fromString(JsonHelper.GetString(entry.getAsJsonObject(), "uuid", ""));
			} catch(Exception e)
			{
				BQ_Standard.logger.log(Level.ERROR, "Unable to load user progress for task", e);
				continue;
			}
			
			userProgress.put(uuid, JsonHelper.GetNumber(entry.getAsJsonObject(), "value", 0).intValue());
		}
	}
	
	/**
	 * Returns a new editor screen for this Reward type to edit the given data
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen GetEditor(GuiScreen parent, JsonObject data)
	{
		return new GuiHuntEditor(parent, data);
	}

	@Override
	public void ResetProgress(UUID uuid)
	{
		completeUsers.remove(uuid);
		userProgress.remove(uuid);
	}

	@Override
	public void ResetAllProgress()
	{
		completeUsers = new ArrayList<UUID>();
		userProgress = new HashMap<UUID,Integer>();
	}

	@Override
	public GuiEmbedded getGui(GuiQuesting screen, int posX, int posY, int sizeX, int sizeY)
	{
		return new GuiTaskHunt(this, screen, posX, posY, sizeX, sizeY);
	}
}
