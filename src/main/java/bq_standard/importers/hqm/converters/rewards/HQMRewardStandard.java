package bq_standard.importers.hqm.converters.rewards;

import java.util.ArrayList;
import betterquesting.quests.rewards.RewardBase;
import bq_standard.importers.hqm.HQMUtilities;
import bq_standard.rewards.RewardItem;
import com.google.gson.JsonElement;

public class HQMRewardStandard extends HQMReward
{
	@Override
	public ArrayList<RewardBase> Convert(JsonElement json)
	{
		ArrayList<RewardBase> rList = new ArrayList<RewardBase>();
		
		if(json == null || !json.isJsonArray())
		{
			return null;
		}
		
		RewardItem reward = new RewardItem();
		rList.add(reward);
		
		for(JsonElement je : json.getAsJsonArray())
		{
			if(je == null || !je.isJsonObject())
			{
				continue;
			}
			
			reward.items.add(HQMUtilities.HQMStackT1(je.getAsJsonObject()));
		}
		
		return rList;
	}
	
}
