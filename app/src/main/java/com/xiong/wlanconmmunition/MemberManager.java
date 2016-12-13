package com.xiong.wlanconmmunition;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class MemberManager {
    public static MemberManager mMemMgr = null;
	private HashMap<String, MemberInfo> members = new HashMap<String, MemberInfo>();
	private String LocalUserName;
	private Drawable LocalUserIcon;
	private ArrayList<String> ips = new ArrayList<String>();
	private HashMap<String ,ArrayList<MemberInfo>> mGroupIP = new HashMap<String, ArrayList<MemberInfo>>();
	
	private MemberManager(){}
	
	public static MemberManager getInstance(){
		if(mMemMgr==null){
			mMemMgr = new MemberManager();
		}
		return mMemMgr;
	}

	public ArrayList<MemberInfo> getGroupAllMembers(String groupId){
		ArrayList<MemberInfo> allIPs = null;
		if(mGroupIP.get(groupId)==null){
			allIPs = new ArrayList<MemberInfo>();
			mGroupIP.put(groupId,allIPs);
		}else{
			allIPs = mGroupIP.get(groupId);
		}

		return allIPs;
	}

	public boolean isOnGroup(String gid,MemberInfo member){
		MemberInfo current = member;
		ArrayList<MemberInfo> members = mGroupIP.get(gid);
		for (int i=0;i<members.size();i++){
			if(current.ip.equals(members.get(i).ip)){
				return true;
			}
		}
		return false;
	}

	public void addGroupMember(String gid,MemberInfo member){
		if(mGroupIP.get(gid)==null){
			mGroupIP.put(gid, new ArrayList<MemberInfo>());

		}
		mGroupIP.get(gid).add(member);

	}
	
	public int addMemberToList(MemberInfo meminfo){
		members.put(meminfo.ip, meminfo);
		return putArray(meminfo.ip);
	}
	
	public void updataSenderName(String ip,String name){
		MemberInfo minfo = members.get(ip);
		minfo.name = name;
	}
	
	public void updateIcon(String ip,Drawable drawable){
		MemberInfo minfo = members.get(ip);
		minfo.iCon = drawable;
	}
	
	public int deleteMemberFromList(String ip){
		members.remove(ip);
		return getIndexofMember(ip);
	}
	
	public boolean hasMemberInfo(String ip){
		return members.get(ip)!=null;
	}
	
	public MemberInfo buildMember(Drawable icon,String name,String hostname,String ip){
		MemberInfo member = new MemberInfo();
		Drawable mDrawable = getMemberIcon(ip);
		member.iCon = mDrawable==null?icon:mDrawable;
		member.name = name.equals("null")?hostname:name;
		member.hostname = hostname;
		member.ip = ip;
		
		XLog.logd("membre:"+member.name+"-------");
		XLog.logd("membre:"+member.hostname+"-------");
		XLog.logd("membre:"+member.ip+"-------");
		return member;
	}

	public MemberInfo buildMember(Drawable icon,String name,String ip){
		GroupInfo group = new GroupInfo();
		Drawable mDrawable = getMemberIcon(ip);
		group.iCon = mDrawable==null?icon:mDrawable;
		group.name = name;
		group.ip = ip;

		XLog.logd("membre:"+group.name+"-------");
		XLog.logd("membre:"+group.ip+"-------");
		return group;
	}
	
	public Drawable getMemberIcon(String ip){
		Drawable drawable = null ;
		if(Util.exists(Util.LOCA_ICON_DIR, ip)){
			drawable = new BitmapDrawable(Util.openOrCreateFile(Util.LOCA_ICON_DIR, ip).getPath());
		}
		return drawable;
	}
	
	public String getLocalUserName() {
		return LocalUserName;
	}

	public void setLocalUserName(String localUserName) {
		LocalUserName = localUserName;
	}
	
	

	public Drawable getLocalUserIcon() {
		return LocalUserIcon;
	}

	public void setLocalUserIcon(Drawable localUserIcon) {
		LocalUserIcon = localUserIcon;
	}

	private int putArray(String ip){
		int index = -1;
//		for (int i = 0; i < ips.length; i++) {
//			if(ips[i]==null){
//				ips[i] = ip;
//				index = i;
//				break;
//			}
//		}
		ips.add(ip);
		index = ips.size()-1;
		return index;
	}
	
	private int getIndexofMember(String ip){
		int index = -1;
//		for (int i = 0; i < ips.length; i++) {
//			if(ips[i] != null && ips[i].equals(ip)){
//				index = i;
//				ips[i] = null;
//				break;
//			}
//		}
		for (int i = 0; i < ips.size(); i++) {
			if(ips.get(i) != null && ips.get(i).equals(ip)){
				index = i;
				ips.remove(i);
				break;
			}
		}
		return index;
	}
	
	public int getIndexByIp(String ip){
		int index = -1;
		for (int i = 0; i < ips.size(); i++) {
			if(ips.get(i) != null && ips.get(i).equals(ip)){
				index = i;
				break;
			}
		}
		return index;
	}
	
	public int getMemberSize(){
		return ips.size();
	}
	
	public String getIpByIndex(int index){
		return ips.get(index);
	}
	
	public MemberInfo getMemberInfiByIp(String ip){
		return members.get(ip);
	}
	
	public String getSenderNameByIp(String ip){
		return getMemberInfiByIp(ip).name;
	}
}
