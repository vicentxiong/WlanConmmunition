package com.xiong.wlanconmmunition.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiong.wlanconmmunition.MemberInfo;
import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.service.MemberAdapter;

import java.util.ArrayList;

/**
 * Created by eshion on 16-9-26.
 */
public class GroupMemberActivity extends ListActivity{
    private String groupId,groupName;
    private MemberAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_layout);
        groupId = getIntent().getStringExtra(SessionManager.CHATIP_EXTRA);
        groupName = getIntent().getStringExtra(SessionManager.GROUP_NAME);
        ArrayList<MemberInfo> memberInfos = MemberManager.getInstance().getGroupAllMembers(groupId);
        mAdapter = new MemberAdapter(this,memberInfos);
        setListAdapter(mAdapter);
        Button add = (Button) findViewById(R.id.addmember);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMember = new Intent(GroupMemberActivity.this, GroupAddActivity.class);
                addMember.putExtra(SessionManager.CHATIP_EXTRA, groupId);
                addMember.putExtra(SessionManager.GROUP_NAME,groupName);
                startActivityForResult(addMember, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<MemberInfo> memberInfos = MemberManager.getInstance().getGroupAllMembers(groupId);
        mAdapter.setList(memberInfos);
        mAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
