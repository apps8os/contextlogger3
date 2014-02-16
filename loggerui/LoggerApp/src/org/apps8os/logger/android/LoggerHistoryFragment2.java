/**
 * Copyright (c) 2013 Aalto University and the authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 *  
 * Authors:
 * Chao Wei (chao.wei@aalto.fi)
 */
package org.apps8os.logger.android;

import java.util.Observable;
import java.util.Observer;

import org.apps8os.logger.android.app.BaseFragment;
import org.apps8os.logger.android.manager.AppManager;
import org.apps8os.logger.android.storage.AbstractCursorLoader;
import org.apps8os.logger.android.widget.adapter.ActionEventHistoryCursorAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Display the record history in a list view
 * 
 * @author Chao Wei
 *
 */
public class LoggerHistoryFragment2 extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private static int LOGGER_HISTORY_LIST_LOADER_ID = 0x59100;

	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks = null;
	private ActionEventHistoryCursorAdapter mAdapter = null;
	private ListView mListView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		final ActionBar actionBar = getSupportActivity().getSupportActionBar();
		actionBar.setTitle(R.string.history);
		actionBar.getCustomView().setVisibility(View.GONE);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_logger_history, container, false); 
		mListView = (ListView)view.findViewById(android.R.id.list);
		mListView.setEmptyView(view.findViewById(R.id.text_veiw_no_history_data));
		mAdapter =  new ActionEventHistoryCursorAdapter(getApplicationContext());
		mListView.setAdapter(mAdapter);
		mCallbacks = this;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOGGER_HISTORY_LIST_LOADER_ID , null, mCallbacks);
	}
	
	@Override
	public void onDestroy() {
		if(mAdapter != null) {
			Cursor cursor = mAdapter.getCursor();
			if (cursor != null) {
				cursor.close();			
			}			
		}
		super.onDestroy();
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(menu != null){
			menu.clear();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new ActionEventHistoryLoader(getApplicationContext());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// A switch-case is useful when dealing with multiple Loaders/IDs
		if (loader.getId() == LOGGER_HISTORY_LIST_LOADER_ID) {
			// The asynchronous load is complete and the data
			// is now available for use. Only now can we associate
			// the queried Cursor with the SimpleCursorAdapter.
			// Log.w(TAG, "loader same: " + loader_id);
			mAdapter.swapCursor(cursor);
		} else {
			// Log.w(TAG, "loader differ: " + loader_id + " / " + loader.getId());
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	static class ActionEventHistoryLoader extends AbstractCursorLoader {

		public ActionEventHistoryLoader(Context context) {
			super(context);			
		}
		
		@Override
		protected Observer newObserver() {
			return new ActionEventObserver();
		}

		@Override
		public Cursor loadInBackground() {
			return AppManager.getActionEventDatabase().getAllActionEventCursor(true);
		}
	}
	
	static class ActionEventObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
		}
	}
}