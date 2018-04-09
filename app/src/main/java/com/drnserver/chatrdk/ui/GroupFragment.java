package com.drnserver.chatrdk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drnserver.chatrdk.MainActivity;
import com.drnserver.chatrdk.model.GroupData;
import com.drnserver.chatrdk.service.ChatQueue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.drnserver.chatrdk.R;
import com.drnserver.chatrdk.data.FriendDB;
import com.drnserver.chatrdk.data.GroupDB;
import com.drnserver.chatrdk.data.StaticConfig;
import com.drnserver.chatrdk.model.Group;
import com.drnserver.chatrdk.model.ListFriend;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.drnserver.chatrdk.helper.*;
import android.support.annotation.Nullable;


public class GroupFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnStartDragListener{
    private RecyclerView recyclerListGroups;
    public FragGroupClickFloatButton onClickFloatButton;
    private ArrayList<Group> listGroup;
    private ListGroupsAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton startSearch;
    public static final int CONTEXT_MENU_DELETE = 1;
    public static final int CONTEXT_MENU_EDIT = 2;
    public static final int CONTEXT_MENU_LEAVE = 3;
    public static final int REQUEST_EDIT_GROUP = 0;
    public static final String CONTEXT_MENU_KEY_INTENT_DATA_POS = "pos";
    private boolean inQueue;
    LovelyProgressDialog progressDialog, waitingLeavingGroup;

    private ItemTouchHelper mItemTouchHelper;
    SwipeController swipeController = null;

    //chat queue
    public static final ChatQueue myChatQueue = new ChatQueue();

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group, container, false);

        listGroup = GroupDB.getInstance(getContext()).getListGroups();
        recyclerListGroups = (RecyclerView) layout.findViewById(R.id.recycleListGroup);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerListGroups.setLayoutManager(layoutManager);
        adapter = new ListGroupsAdapter(getContext(), listGroup, this);
        recyclerListGroups.setAdapter(adapter);
        inQueue = false;
        //test fragment
        System.out.println("begin test");
        startSearch = (ImageButton) layout.findViewById(R.id.startSearch);
        startSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(inQueue == false) {
                    myChatQueue.enterQueue();
                    inQueue = true;
                }

            }
        });
        System.out.println("end test");
        //end test fragment
        //ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        //mItemTouchHelper = new ItemTouchHelper(callback);
        //mItemTouchHelper.attachToRecyclerView(recyclerListGroups);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //ChatActivity post = dataSnapshot.getValue(ChatActivity.class);
                // ...
                onRefresh();
                inQueue = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        FirebaseDatabase.getInstance().getReference().child("user/"+ StaticConfig.UID+"/group").addValueEventListener(postListener);


        /*
        ##########################################################################################
        Hi Ryan!
        This is my solution that works for swiping.

        https://stackoverflow.com/questions/40089542/add-swipe-right-to-delete-listview-item

        It seams to be working great! We can implement extra functionality to left direction if we NEED to
        I didn't remove any of your code cuz I didn't quite know what and where did you add <3
        Call me on Whatsup if you have questions, or else I'll see you on discord tonight <3

        recyclerListGroups.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerListGroups.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Remove item from backing list here
                // swipeDir breakdown:
                // RIGHT = 8
                // LEFT = 4
                // link to others int representations of swiped movement or actions
                // https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.html#RIGHT
                if (swipeDir == 8) {
                    // remove item from the list
                    //myChatQueue.enterQueue();
                    Group leavingGroup = listGroup.get(viewHolder.getAdapterPosition());
                    System.out.println("removing " + listGroup.get(viewHolder.getAdapterPosition()).id);
                    leaveGroup(leavingGroup);
                    listGroup.remove(viewHolder.getAdapterPosition());
                    // update the list
                    // okay
                    adapter.notifyDataSetChanged();
                    myChatQueue.setNumberOfChats(listGroup.size());
                }
                else {
                    // update the list, aka do nothing
                    adapter.notifyDataSetChanged();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerListGroups);
        //##########################################################################################
        */

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Group leavingGroup = listGroup.get(position);
                leaveGroup(leavingGroup);
                System.out.println("IT WORKED!!!");
                adapter.notifyDataSetChanged();
                myChatQueue.setNumberOfChats(listGroup.size());
            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerListGroups);

        recyclerListGroups.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });



        onClickFloatButton = new FragGroupClickFloatButton();
        progressDialog = new LovelyProgressDialog(getContext())
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_delete_group)
                .setTitle("Deleting....")
                .setTopColorRes(R.color.colorAccent);

        waitingLeavingGroup = new LovelyProgressDialog(getContext())
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_delete_group)
                .setTitle("Group leaving....")
                .setTopColorRes(R.color.colorAccent);


            //mSwipeRefreshLayout.setRefreshing(true);
            //getListGroup();

        return layout;
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void getListGroup(){
        myChatQueue.setNumberOfChats(listGroup.size());
        FirebaseDatabase.getInstance().getReference().child("user/"+ StaticConfig.UID+"/group").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    HashMap mapListGroup = (HashMap) dataSnapshot.getValue();
                    Iterator iterator = mapListGroup.keySet().iterator();
                    while (iterator.hasNext()){
                        String idGroup = (String) mapListGroup.get(iterator.next().toString());
                        Group newGroup = new Group();
                        newGroup.id = idGroup;
                        listGroup.add(newGroup);
                    }
                    getGroupInfo(0);
                    System.out.println("refreshing");
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mSwipeRefreshLayout.setRefreshing(false);
                myChatQueue.setNumberOfChats(listGroup.size());
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_EDIT_GROUP && resultCode == Activity.RESULT_OK) {
            listGroup.clear();
            ListGroupsAdapter.listFriend = null;
            GroupDB.getInstance(getContext()).dropDB();
            getListGroup();
        }
    }

    private void getGroupInfo(final int indexGroup){
        if(indexGroup == listGroup.size()){
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }else {
            FirebaseDatabase.getInstance().getReference().child("group/"+listGroup.get(indexGroup).id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        HashMap mapGroup = (HashMap) dataSnapshot.getValue();
                        ArrayList<String> member = (ArrayList<String>) mapGroup.get("member");
                        ArrayList<String> MatchedPreferences = (ArrayList<String>) mapGroup.get("MatchedPreferences");
                        HashMap mapGroupInfo = (HashMap) mapGroup.get("groupInfo");
                        for(String idMember: member){
                            listGroup.get(indexGroup).member.add(idMember);
                        }
                        for(String currentpref: MatchedPreferences){
                            listGroup.get(indexGroup).MatchedPreferences.add(currentpref);
                        }
                        listGroup.get(indexGroup).groupInfo.put("name", (String) mapGroupInfo.get("name"));
                        listGroup.get(indexGroup).groupInfo.put("admin", (String) mapGroupInfo.get("admin"));
                    }
                    GroupDB.getInstance(getContext()).addGroup(listGroup.get(indexGroup));
                    Log.d("GroupFragment", listGroup.get(indexGroup).id +": " + dataSnapshot.toString());
                    getGroupInfo(indexGroup +1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onRefresh() {
        listGroup.clear();
        ListGroupsAdapter.listFriend = null;
        GroupDB.getInstance(getContext()).dropDB();
        adapter.notifyDataSetChanged();
        getListGroup();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case CONTEXT_MENU_DELETE:
                int posGroup = item.getIntent().getIntExtra(CONTEXT_MENU_KEY_INTENT_DATA_POS, -1);
                if(((String)listGroup.get(posGroup).groupInfo.get("admin")).equals(StaticConfig.UID)) {
                    Group group = listGroup.get(posGroup);
                    listGroup.remove(posGroup);
                    if(group != null){
                        deleteGroup(group, 0);
                    }
                }else{
                    Toast.makeText(getActivity(), "You are not admin", Toast.LENGTH_LONG).show();
                }
                break;
            case CONTEXT_MENU_EDIT:
                int posGroup1 = item.getIntent().getIntExtra(CONTEXT_MENU_KEY_INTENT_DATA_POS, -1);
                if(((String)listGroup.get(posGroup1).groupInfo.get("admin")).equals(StaticConfig.UID)) {
                    Intent intent = new Intent(getContext(), AddGroupActivity.class);
                    intent.putExtra("groupId", listGroup.get(posGroup1).id);
                    startActivityForResult(intent, REQUEST_EDIT_GROUP);
                }else{
                    Toast.makeText(getActivity(), "You are not admin", Toast.LENGTH_LONG).show();
                }

                break;

            case CONTEXT_MENU_LEAVE:
                int position = item.getIntent().getIntExtra(CONTEXT_MENU_KEY_INTENT_DATA_POS, -1);
                if(((String)listGroup.get(position).groupInfo.get("admin")).equals(StaticConfig.UID)) {
                    Toast.makeText(getActivity(), "Admin cannot leave group", Toast.LENGTH_LONG).show();
                }else{
                    waitingLeavingGroup.show();
                    Group groupLeaving = listGroup.get(position);
                    leaveGroup(groupLeaving);
                }
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void deleteGroup(final Group group, final int index){
        if(index == group.member.size()){
            FirebaseDatabase.getInstance().getReference().child("group/"+group.id).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            GroupDB.getInstance(getContext()).deleteGroup(group.id);
                            listGroup.remove(group);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Deleted group", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            new LovelyInfoDialog(getContext())
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_dialog_delete_group)
                                    .setTitle("False")
                                    .setMessage("Cannot delete group right now, please try again.")
                                    .setCancelable(false)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
            ;
        }else{
            FirebaseDatabase.getInstance().getReference().child("user/"+group.member.get(index)+"/group/"+group.id).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            deleteGroup(group, index + 1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            new LovelyInfoDialog(getContext())
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_dialog_delete_group)
                                    .setTitle("False")
                                    .setMessage("Cannot connect server")
                                    .setCancelable(false)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
            ;
        }

    }

    public void leaveGroup(final Group group){
        try {
            FirebaseDatabase.getInstance().getReference().child("group/" + group.id + "/member")
                    .orderByValue().equalTo(StaticConfig.UID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.getValue() == null) {
                                    //email not found
                                    waitingLeavingGroup.dismiss();
                                    new LovelyInfoDialog(getContext())
                                            .setTopColorRes(R.color.colorAccent)
                                            .setTitle("Error")
                                            .setMessage("Error occurred during leaving group")
                                            .show();
                                } else {
                                    String memberIndex = "";
                                    ArrayList<String> result = ((ArrayList<String>) dataSnapshot.getValue());
                                    for (int i = 0; i < result.size(); i++) {
                                        if (result.get(i) != null) {
                                            memberIndex = String.valueOf(i);
                                        }
                                    }

                                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                                            .child("group").child(group.id).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("group/" + group.id + "/member")
                                            .child(memberIndex).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    waitingLeavingGroup.dismiss();

                                                    listGroup.remove(group);
                                                    adapter.notifyDataSetChanged();
                                                    GroupDB.getInstance(getContext()).deleteGroup(group.id);
                                                    new LovelyInfoDialog(getContext())
                                                            .setTopColorRes(R.color.colorAccent)
                                                            .setTitle("Success")
                                                            .setMessage("Group leaving successfully")
                                                            .show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    waitingLeavingGroup.dismiss();
                                                    new LovelyInfoDialog(getContext())
                                                            .setTopColorRes(R.color.colorAccent)
                                                            .setTitle("Error")
                                                            .setMessage("Error occurred during leaving group")
                                                            .show();
                                                }
                                            });
                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                                        .child("group").child(group.id).removeValue();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //email not found
                            waitingLeavingGroup.dismiss();
                            new LovelyInfoDialog(getContext())
                                    .setTopColorRes(R.color.colorAccent)
                                    .setTitle("Error")
                                    .setMessage("Error occurred during leaving group")
                                    .show();
                        }
                    });
        }catch(Exception e) {

        }
    }

    public class FragGroupClickFloatButton implements View.OnClickListener{

        Context context;
        public FragGroupClickFloatButton getInstance(Context context){
            this.context = context;
            return this;
        }

        @Override
        public void onClick(View view) {
            startActivity(new Intent(getContext(), AddGroupActivity.class));
        }
    }
}

class ListGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter  {

    private ArrayList<Group> listGroup;
    public static ListFriend listFriend = null;
    private Context context;
    private final OnStartDragListener mDragStartListener;

    public ListGroupsAdapter(Context context,ArrayList<Group> listGroup, OnStartDragListener dragStartListener){
        mDragStartListener = dragStartListener;
        this.context = context;
        this.listGroup = listGroup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_group, parent, false);
        return new ItemGroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String groupName = listGroup.get(position).groupInfo.get("name");
        final ArrayList<String> groupPref = listGroup.get(position).MatchedPreferences;

        String preferences = "";
        for(int i =0; i < groupPref.size(); i++) {
            preferences = preferences + groupPref.get(i) + "\n";
        }
        if(groupName != null && groupName.length() > 0) {
            ((ItemGroupViewHolder) holder).txtGroupName.setText(groupName);
            ((ItemGroupViewHolder) holder).txtGroupName.setVisibility(View.INVISIBLE);
            ((ItemGroupViewHolder) holder).iconGroup.setText((groupName.charAt(0) + "").toUpperCase());
            ((ItemGroupViewHolder) holder).txtGroupName.setText(groupName);
            ((ItemGroupViewHolder) holder).preferencesTextView.setText(preferences);
        }
        ((ItemGroupViewHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(new Object[]{groupName, position});
                view.getParent().showContextMenuForChild(view);
            }
        });


        ((RelativeLayout)((ItemGroupViewHolder) holder).txtGroupName.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listFriend == null){
                    listFriend = FriendDB.getInstance(context).getListFriend();
                }
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, groupName);
                ArrayList<CharSequence> idFriend = new ArrayList<>();
                ChatActivity.bitmapAvataFriend = new HashMap<>();
                for(String id : listGroup.get(position).member) {
                    idFriend.add(id);
                    String avata = listFriend.getAvataById(id);
                    if(!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                        byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
                        ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    }else if(avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                        ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avata));
                    }else {
                        ChatActivity.bitmapAvataFriend.put(id, null);
                    }
                }
                intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
                intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, listGroup.get(position).id);
                context.startActivity(intent);
            }
        });
        // Start a drag whenever the handle view it touched
        final ItemGroupViewHolder temp = (ItemGroupViewHolder)holder;

     /*   temp.iconGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(temp);
                    //onItemDismiss(temp.getAdapterPosition());
                }
                else {
                    System.out.println("FREE");
                }
                return false;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return listGroup.size();
    }

    @Override
    public void onItemDismiss(int position) {
        try {
            System.out.println("dismiss");
            listGroup.remove(position);
            notifyItemRemoved(position);
        }catch(Exception e) {

        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        System.out.println("onItemMove");
        Collections.swap(listGroup, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public static class ItemGroupViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, ItemTouchHelperViewHolder {
        public TextView iconGroup, txtGroupName;
        public TextView preferencesTextView;
        public TextView iconGroup2, iconGroup3, iconGroup4, iconGroup5;
        public ImageButton btnMore;
        public CardView cardAdapter;
        public ItemGroupViewHolder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            iconGroup = (TextView) itemView.findViewById(R.id.icon_group);
            iconGroup2 = (TextView) itemView.findViewById(R.id.icon_group2);
            iconGroup3 = (TextView) itemView.findViewById(R.id.icon_group3);
            iconGroup4 = (TextView) itemView.findViewById(R.id.icon_group4);
            iconGroup5 = (TextView) itemView.findViewById(R.id.icon_group5);
            preferencesTextView = (TextView) itemView.findViewById(R.id.preferencesTextView);
            txtGroupName = (TextView) itemView.findViewById(R.id.txtName);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMoreAction);
            cardAdapter = (CardView) itemView.findViewById(R.id.cardAdapter);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle((String) ((Object[])btnMore.getTag())[0]);
            Intent data = new Intent();
            data.putExtra(GroupFragment.CONTEXT_MENU_KEY_INTENT_DATA_POS, (Integer) ((Object[])btnMore.getTag())[1]);
            menu.add(Menu.NONE, GroupFragment.CONTEXT_MENU_EDIT, Menu.NONE, "Edit group").setIntent(data);
            menu.add(Menu.NONE, GroupFragment.CONTEXT_MENU_DELETE, Menu.NONE, "Delete group").setIntent(data);
            menu.add(Menu.NONE, GroupFragment.CONTEXT_MENU_LEAVE, Menu.NONE, "Leave group").setIntent(data);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}


