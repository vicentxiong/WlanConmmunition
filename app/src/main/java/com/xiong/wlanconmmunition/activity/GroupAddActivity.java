package com.xiong.wlanconmmunition.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.xiong.wlanconmmunition.DataPacketProtocol;
import com.xiong.wlanconmmunition.MemberInfo;
import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.MessageManager;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.GroupInfo;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.service.MemberAdapter;

import java.util.ArrayList;

/**
 * Created by eshion on 16-9-26.
 */
public class GroupAddActivity extends ListActivity{
    private String mGroupID,mGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupID = getIntent().getStringExtra(SessionManager.CHATIP_EXTRA);
        mGroupName = getIntent().getStringExtra(SessionManager.GROUP_NAME);
        setListAdapter(new MemberAdapter(this,initMembers()));
    }

    private ArrayList<MemberInfo> initMembers(){
        ArrayList<MemberInfo> all = new ArrayList<MemberInfo>();
        int count = MemberManager.getInstance().getMemberSize();
        for (int i=0;i<count;i++){
            String ip = MemberManager.getInstance().getIpByIndex(i);
            MemberInfo member = MemberManager.getInstance().getMemberInfiByIp(ip);
            if(member instanceof GroupInfo){
                Log.d("xx","group dont add");
            }else{
                if(!MemberManager.getInstance().isOnGroup(mGroupID,member) && !Util.getLocalIpAdress(this).equals(member.ip))
                    all.add(member);
            }
        }
        return all;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MemberInfo member = (MemberInfo) l.getItemAtPosition(position);
        sendMessage(member);
        MemberManager.getInstance().addGroupMember(mGroupID,member);
        setResult(RESULT_OK);
        finish();
    }

    private void sendMessage(MemberInfo member){
        MessageManager msgMgr = MessageManager.getInstance();
        DataPacketProtocol dp = msgMgr.createUdpDataPacketProtocol(member.ip, MessageManager.IPMSG_GROUP_REQUEST, buildIpMessage());
        msgMgr.sendUdpMessage(dp);
    }

    private String buildIpMessage(){
        StringBuffer sb = new StringBuffer();
        sb.append(mGroupName).append("_");
        sb.append(mGroupID);
        ArrayList<MemberInfo> groupMember = MemberManager.getInstance().getGroupAllMembers(mGroupID);
        int N = groupMember.size();
        for (int i=0;i<N;i++){
            sb.append("_").append(groupMember.get(i).ip);
        }
        return sb.toString();
    }
}
