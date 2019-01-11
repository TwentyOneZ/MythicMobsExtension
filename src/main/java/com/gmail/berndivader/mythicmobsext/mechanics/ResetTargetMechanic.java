package com.gmail.berndivader.mythicmobsext.mechanics;

import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.gmail.berndivader.MythicPlayers.Mechanics.TriggeredSkillAP;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.*;

@ExternalAnnotation(name="resettarget,settarget",author="BerndiVader")
public 
class 
ResetTargetMechanic
extends
SkillMechanic
implements
ITargetedEntitySkill,
INoTargetSkill 
{
	boolean event,trigger,set;
	TargetReason reason;
	
	public ResetTargetMechanic(String line, MythicLineConfig mlc) {
		super(line, mlc);
		
		set=line.charAt(0)=='s';
		event=mlc.getBoolean("event",false);
		trigger=mlc.getBoolean("trigger",false);
		try {
			reason=TargetReason.valueOf(mlc.getString("reason","custom").toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			reason=TargetReason.CUSTOM;
		}
	}

	@Override
	public boolean cast(SkillMetadata data) {
		return this.castAtEntity(data, data.getCaster().getEntity());
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		if(target.isLiving()) {
			NMSUtils.setGoalTarget(data.getCaster().getEntity().getBukkitEntity(),set?target.getBukkitEntity():null,reason,event);
			if(trigger&&Utils.mobmanager.isActiveMob(data.getCaster().getEntity())) {
				new TriggeredSkillAP(SkillTrigger.TARGETCHANGE,Utils.mobmanager.getMythicMobInstance(data.getCaster().getEntity()),target);
			}
			return true;
		}
		return false;
	}
}