package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardsSyncedEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.ui.adapters.DashboardAdapter;
import org.hisp.dhis.mobile.datacapture.ui.views.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewPagerFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<CursorHolder<List<DBItemHolder<Dashboard>>>>,
        ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final int LOADER_ID = 826752394;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private DashboardAdapter mAdapter;
    private ImageButton mEditButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh_dashboards) {
            BusProvider.getInstance().post(new DashboardSyncEvent());
            Toast.makeText(getActivity(), "Refreshing dashboards", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard_view_pager, container, false);
        mEditButton = (ImageButton) root.findViewById(R.id.dashboard_edit_button);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final int blue = Color.parseColor(getString(R.color.navy_blue));
        final int gray = Color.parseColor(getString(R.color.darker_grey));

        mAdapter = new DashboardAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return blue;
            }

            @Override
            public int getDividerColor(int position) {
                return gray;
            }

        });

        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(this);

        mEditButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<CursorHolder<List<DBItemHolder<Dashboard>>>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            final String SELECTION = DBContract.DashboardColumns.STATE + " != " + '"' + State.DELETING + '"';
            return new DashboardListLoader(
                    getActivity(), DBContract.DashboardColumns.CONTENT_URI,
                    DashboardHandler.PROJECTION, SELECTION, null, null
            );
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DBItemHolder<Dashboard>>>> loader,
                               CursorHolder<List<DBItemHolder<Dashboard>>> data) {
        if (loader != null && loader.getId() == LOADER_ID && data != null) {
            mAdapter.setData(data.getData());
            mSlidingTabLayout.setViewPager(mViewPager);
        }
    }


    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DBItemHolder<Dashboard>>>> loader) {
        // reset state of views
    }

    @Subscribe
    public void onDashboardSyncedEvent(OnDashboardsSyncedEvent event) {
        if (event.getException() != null) {
            Toast.makeText(getActivity(), "Refresh Failed", Toast.LENGTH_SHORT).show();
            event.getException().printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        DBItemHolder<Dashboard> dbItem = mAdapter.getDashboard(position);
        if (dbItem != null) {
            Dashboard dashboard = dbItem.getItem();
            boolean isDashboardEditable = dashboard.getAccess().isManage();
            setEditButtonVisibility(isDashboardEditable);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View view) {
        int position = mViewPager.getCurrentItem();
        DBItemHolder<Dashboard> dbItem = mAdapter.getDashboard(position);
        Toast.makeText(getActivity(), "Name: " + dbItem.getItem().getName(), Toast.LENGTH_SHORT).show();
    }

    private void setEditButtonVisibility(boolean isEditable) {
        if (isEditable) {
            mEditButton.setVisibility(View.VISIBLE);
        } else {
            mEditButton.setVisibility(View.GONE);
        }
    }

    public static class DashboardListLoader extends AbsCursorLoader<List<DBItemHolder<Dashboard>>> {
        public DashboardListLoader(Context context, Uri uri, String[] projection,
                                   String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DBItemHolder<Dashboard>> readDataFromCursor(Cursor cursor) {
            List<DBItemHolder<Dashboard>> dashboards = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    dashboards.add(DashboardHandler.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            return dashboards;
        }
    }
}
