package com.gmail.berndivader.mmcustomskills26;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mmcustomskills26.metaTagValue.ValueTypes;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.SkillString;
import net.minecraft.server.v1_12_R1.Material;

public class SetMetatagMechanic extends SkillMechanic
implements
ITargetedLocationSkill,
ITargetedEntitySkill {
	protected String tag;
	protected metaTagValue mtv;

	public SetMetatagMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.ASYNC_SAFE=false;
		String ms =  mlc.getString(new String[]{"meta","m"});
		ms = ms.substring(1, ms.length()-1);
		ms = SkillString.parseMessageSpecialChars(ms);
		String parse[] = ms.split(";");
		String t=null, v=null, vt=null;
		for (String p : parse) {
			if (p.startsWith("tag=")) {
				t = p.substring(4);
				continue;
			} else if (p.startsWith("value=")) {
				v = p.substring(6);
				continue;
			} else if (p.startsWith("type=")) {
				vt = p.substring(5);
				continue;
			}
		}
		if (t!=null) {
			this.tag = t;
			mtv = new metaTagValue(v, vt);
		}
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (this.tag==null || this.tag.isEmpty()) return false;
		String parsedTag = SkillString.parseMobVariables(this.tag, data.getCaster(), target, data.getTrigger());
		Object vo = this.mtv.getType().equals(ValueTypes.STRING)
				?SkillString.parseMobVariables((String)this.mtv.getValue(), data.getCaster(), target, data.getTrigger())
				:this.mtv.getValue();
		target.getBukkitEntity().setMetadata(parsedTag, new FixedMetadataValue(Main.getPlugin(),vo));
		return true;
	}

	@Override
	public boolean castAtLocation(SkillMetadata data, AbstractLocation location) {
		Block target = BukkitAdapter.adapt(location).getBlock();
		if (target.getType().equals(Material.AIR) 
				||this.tag==null 
				|| this.tag.isEmpty()) return false;
		String parsedTag = SkillString.parseMobVariables(this.tag, data.getCaster(), null, data.getTrigger());
		Object vo = this.mtv.getType().equals(ValueTypes.STRING)
				?SkillString.parseMobVariables((String)this.mtv.getValue(), data.getCaster(), null, data.getTrigger())
				:this.mtv.getValue();
		target.setMetadata(parsedTag, new FixedMetadataValue(Main.getPlugin(),vo));
		return true;
	}

}