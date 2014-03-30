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
package org.apps8os.logger.android.storage;

import java.util.Observer;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public abstract class AbstractCursorLoader extends AsyncTaskLoader<Cursor> {

	private final String TAG = AbstractCursorLoader.class.getSimpleName();

	private Observer mObserver = null;
	private Cursor mData = null;

	/**
	 * Create new task for loading cursor in background.
	 * 
	 * @param context
	 */
	public AbstractCursorLoader(Context context) {
		super(context);
	}

	@Override
	public void deliverResult(Cursor data) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			onReleaseResources(data);
			return;
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// The old data may still be in use (i.e. bound to an adapter, etc.), so
		// we must protect it until the new data has been delivered.
		Cursor oldData = mData;
		mData = data;

		if (isStarted()) {
			// If the Loader is in a started state, deliver the results to the
			// client. The superclass method does this for us.
			super.deliverResult(data);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldData != null && oldData != data) {
			onReleaseResources(oldData);
		}

		super.deliverResult(data);
	}

	@Override
	protected void onStartLoading() {
		Log.v(TAG, "onStartLoading()");
		if (mData != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mData);
		}

		// Begin monitoring the underlying data source.
		if (mObserver == null) {
			mObserver = newObserver();
		}

		if (takeContentChanged() || mData == null) {
			// When the observer detects a change, it should call onContentChanged()
			// on the Loader, which will cause the next call to takeContentChanged()
			// to return true. If this is ever the case (or if the current data is
			// null), we force a new load.
			forceLoad();
		}

		super.onStartLoading();
	}

	@Override
	protected void onStopLoading() {
		Log.v(TAG, "onStopLoading()");

		// The Loader is in a stopped state, so we should attempt to cancel the
		// current load (if there is one).
		cancelLoad();

		// Note that we leave the observer as is; Loaders in a stopped state
		// should still monitor the data source for changes so that the Loader
		// will know to force a new load if it is ever started again.

		super.onStopLoading();
	}

	@Override
	protected void onReset() {
		Log.v(TAG, "onReset()");

		// Ensure the loader has been stopped.
		onStopLoading();

		// At this point we can release the resources associated with 'mData'.
		if (mData != null) {
			onReleaseResources(mData);
			mData = null;
		}

		// The Loader is being reset, so we should stop monitoring for changes.
		if (mObserver != null) {
			mObserver = null;
		}

		super.onReset();
	}

	@Override
	public void onCanceled(Cursor data) {
		Log.v(TAG, "onCanceled()");

		// Attempt to cancel the current asynchronous load.
		super.onCanceled(data);

		// The load has been canceled, so we should release the resources
		// associated with 'data'.
		onReleaseResources(data);
	}

	protected void onReleaseResources(Cursor data) {
		// For a simple List, there is nothing to do. For something like a Cursor, we
		// would close it in this method. All resources associated with the Loader
		// should be released here.

		if (data != null) {
			data.close(); 
		}
	}

	protected abstract Observer newObserver();
	
}