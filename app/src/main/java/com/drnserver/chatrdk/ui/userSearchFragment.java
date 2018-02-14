package com.drnserver.chatrdk.ui;

import android.view.View;

import com.drnserver.chatrdk.R;

/**
 * Created by DrN on 2/13/2018.
 */

public class userSearchFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * onCreatevView - Creates the friend fragment for usage in tabs for main activity UI
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_friends, container, false);
        return layout;
    }
}
