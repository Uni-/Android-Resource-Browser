package kr.aury.resource.browser;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public static ArrayList<SimpleEntry<String, Integer>> themeCodes;

	public static final String ARG_SECTION_NUMBER = "section_number";
	
	static {

		themeCodes = new ArrayList<SimpleEntry<String, Integer>>();

		for (Field field : android.R.style.class.getFields())
			if (field.getName().startsWith("Theme")
					&& !field.getName().contains("Bar")
					&& !field.getName().contains("Dialog")
					&& !field.getName().contains("Panel"))
				try {
					themeCodes.add(new SimpleEntry<String, Integer>(field
							.getName(), field.getInt(field)));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new ResourceListFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, i);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return themeCodes.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return themeCodes.get(position).getKey();
		}
	}

	public static class ResourceListFragment extends Fragment {
		ArrayList<Map.Entry<String, Integer>> drawables = new ArrayList<Map.Entry<String, Integer>>();

		public ResourceListFragment() {
			for (Field i : android.R.drawable.class.getFields())
				try {
					drawables.add(new AbstractMap.SimpleEntry<String, Integer>(
							i.getName(), i.getInt(android.R.drawable.class)));
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Bundle args = getArguments();
			int themeCode = themeCodes.get(args.getInt(ARG_SECTION_NUMBER)).getValue();
			
			ContextThemeWrapper wrapper = new ContextThemeWrapper(
					getActivity(), themeCode);
			ListView listView = new ListView(wrapper);
			listView.setAdapter(new EntryArrayAdapter<String, Integer>(
					getActivity(), R.layout.rb_list_item, R.id.imageResource,
					R.id.textResource,
					new ArrayList<Map.Entry<String, Integer>>(drawables)));
			return listView;
		}
	}

	public static class EntryArrayAdapter<K, V> extends ArrayAdapter<K> {
		private int firstViewResourceId;
		private int secondViewResourceId;
		private List<Map.Entry<K, V>> objects;

		public EntryArrayAdapter(Context context, int resource) {
			super(context, resource);
		}

		public EntryArrayAdapter(Context context, int resource,
				int firstViewResourceId, int secondViewResourceId,
				List<Map.Entry<K, V>> objects) {
			super(context, resource, secondViewResourceId);
			for (Map.Entry<K, V> o : objects)
				add(o.getKey());
			this.firstViewResourceId = firstViewResourceId;
			this.secondViewResourceId = secondViewResourceId;
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			((TextView) v.findViewById(secondViewResourceId)).setText(String
					.valueOf(objects.get(position).getKey()));
			((ImageView) v.findViewById(firstViewResourceId))
					.setImageResource(Integer.valueOf(String.valueOf(objects
							.get(position).getValue())));
			return v;
		}

	}
}
