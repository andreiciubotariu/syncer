package com.ciubotariu.syncer;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.SyncAdapterType;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//			.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new AvailableAccountsFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public static class AccountAuthorityPair{
		public Account account;
		public String authority;

		@Override
		public String toString(){
			return account.name + " ("+authority+")";
		}
	}

	public static class AvailableAccountsFragment extends ListFragment{
		ArrayList <AccountAuthorityPair> accountList = new ArrayList <AccountAuthorityPair>();
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);

			Account[] accounts = AccountManager.get(getActivity()).getAccounts();
			SyncAdapterType[] syncAdapterTypes = ContentResolver.getSyncAdapterTypes();

			for (Account account: accounts){
				for (SyncAdapterType syncAdaptertype : syncAdapterTypes) {
					if (account.type.equals(syncAdaptertype.accountType) && ContentResolver.getIsSyncable(account, syncAdaptertype.authority) > 0 && syncAdaptertype.isUserVisible()) {
						AccountAuthorityPair a = new AccountAuthorityPair();
						a.account = account;
						a.authority = syncAdaptertype.authority;
						accountList.add(a);
					}
				}
			}
			
			ListAdapter adapter = new ArrayAdapter<AccountAuthorityPair>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, accountList);
			setListAdapter(adapter);
		}
		
		@Override
		public void onListItemClick (ListView l, View row, int position, long id){
			AccountAuthorityPair a = accountList.get(position);
			Bundle extras = new Bundle();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.requestSync(a.account, a.authority, extras);
		}
	}

}
