package org.perfectplay.com.lolspeak.GameInfoActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.perfectplay.com.lolspeak.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constant.Region;
import dto.Static.Champion;
import dto.Static.ChampionList;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ChampionListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private Map<String, Champion> champions;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> data;
    private Handler handler;
    private Toast toast;

    // TODO: Rename and change types of parameters
    public static ChampionListFragment newInstance(String param1, String param2) {
        ChampionListFragment fragment = new ChampionListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChampionListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        data = new ArrayList<String>();
        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_LONG);
    }

    public void FillList(ChampionList list)
    {
        data.clear();
        List<Champion> champs = new ArrayList<Champion>();
        Champion[] dumbList = new Champion[list.getData().values().size()];
        list.getData().values().toArray(dumbList);

        for(int i = 0; i < dumbList.length; i++)
            champs.add(dumbList[i]);

        Collections.sort(champs, new Comparator<Champion>() {
            @Override
            public int compare(Champion champion, Champion champion2) {
                return champion.getName().compareTo(champion2.getName());
            }
        });

        champions = new HashMap<String, Champion>();

        for(int i =0; i < champs.size(); i++) {
            data.add(champs.get(i).getName());
            champions.put(champs.get(i).getName(), champs.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summonerspell, container, false);
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, data);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        Thread t = new Thread()
        {
            @Override
            public void run() {
                RiotApi api = new RiotApi("a29fee68-0e2a-446e-b1ec-211dba50aedc");
                api.setRegion(Region.NA);

                try {
                    FillList(api.getDataChampionList());
                } catch (RiotApiException e) {
                    e.printStackTrace();
                }
            }};

        t.start();

        final Runnable r = new Runnable()
        {
            public void run()
            {
                if(mAdapter.getCount() == 0)
                    handler.postDelayed(this, 500);
                mAdapter.notifyDataSetChanged();
            }
        };

        handler.post(r);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            Intent intent = new Intent(getActivity(), ChampionInfo.class);
            Bundle b = new Bundle();
            b.putInt("key", champions.get(data.get((int) id)).getId()); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
