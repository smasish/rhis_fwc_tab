package org.sci.rhis.fwc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
								 HashMap<String, List<String>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_item, null);
		}
		Resources res1 = _context.getResources();
		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);

		String str = childText;

		str = str.replaceAll("[^0-9]+", " ");
		str = str.trim();



		txtListChild.setText(childText);

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String[] details;
				Toast.makeText(_context,""+childText, Toast.LENGTH_SHORT).show();

				//here I need to do some things that require me to manipulate the categoriesList from the Activity class - but it is out of scope

				String str = null;

				//str = childText;
				//str = ""+list1.get(childPosition);

				//Log.d("----------lenth---||" + str, "" + str.length());

				Resources res1 = _context.getResources();
				if(childPosition == 12 && str.length()>4) {
					str = parseString(childText);
					Log.d("-------------||" + str, "" + str.trim().split(" "));
					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.ANC_Danger_Sign_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)-1];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));
					//System.out.println(Arrays.asList(s.trim().split(" ")));
					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}
				else if(childPosition == 13 && str.length()>4) {

					str = parseString(childText);

					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.ANC_Drawback_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)-1];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));

					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);


				}else if(childPosition == 14 && str.length()>4) {
					str = parseString(childText);

					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.ANC_Disease_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if (animal.length() > 0)
							temp = temp + "\n" + details[Integer.parseInt(animal) - 1];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));

					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}
				else if(childPosition == 15 && str.length()>4) {
					str = parseString(childText);

					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.Treatment_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)-1];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));

					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);

				}
				else if(childPosition == 16 && str.length()>4) {
					str = parseString(childText);

					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.ANC_Advice_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)-1];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));

					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);

				}

				else if(childPosition == 19 && str.length()>4) {
					str = parseString(childText);

					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.ANC_Refer_Reason_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)-1];
					}
					Log.d("oooooooooo17+++" + str, "" + str.trim().split(" "));

					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);

				}

			}
		});

		return convertView;
	}

	private String parseString(String st) {

		String s = null;

			s = st;
			s = s.replaceAll("[^0-9]+", " ");

		//AlertMessage.showMessage(con,"Details",temp);
		return s;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}


	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);
		Log.d("::::----"+headerTitle, "ontest===== ");

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
