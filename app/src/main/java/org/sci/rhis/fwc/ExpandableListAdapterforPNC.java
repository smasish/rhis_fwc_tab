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

public class ExpandableListAdapterforPNC extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;


	public ExpandableListAdapterforPNC(Context context, List<String> listDataHeader,
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

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);

		if (childPosition == 1 && childText.length()>2 ) {
			txtListChild.setText("" + _context.getString(R.string.complicationsign) +  _context.getString(R.string.detail));
		}else if (childPosition == 12 && childText.length()>2) {
			txtListChild.setText("" + _context.getString(R.string.danger_signs) + _context.getString(R.string.detail));
		}else if (childPosition == 13 && childText.length()>2) {
			txtListChild.setText("" + _context.getString(R.string.disease) +  _context.getString(R.string.detail));
		}else if (childPosition == 14 && childText.length()>2) {
			txtListChild.setText("" + _context.getString(R.string.treatment) +  _context.getString(R.string.detail));
		}else if (childPosition == 15 && childText.length()>2) {
			txtListChild.setText("" + _context.getString(R.string.advice) +  _context.getString(R.string.detail));
		}else if (childPosition == 17 && childText.length()>2) {
			txtListChild.setText("" + _context.getString(R.string.referReason) +  _context.getString(R.string.detail));
		}else
		txtListChild.setText(childText);

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String[] details;
				Resources res1 = _context.getResources();
				details = res1.getStringArray(R.array.PNC_Mother_Danger_Sign_DropDown);

				//Toast.makeText(_context, "" + childText, Toast.LENGTH_SHORT).show();
				String str = childText;
				Log.d("childText","========="+childText.length());
				str = str.replaceAll("[^0-9]+", " ");
				//Log.d("oooooooooo13+++" + s, "" + s.trim().split(" "));


				if (childPosition == 1 && childText.length()>2) {


					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.PNC_Mother_Drawback_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));


					if(temp.length()>5)
					AlertMessage.showMessage(_context, "Details", temp);


				}
				else if (childPosition == 12 && childText.length()>2){
					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.PNC_Mother_Danger_Sign_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));


					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}

				else if (childPosition == 13 && childText.length()>2){
					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.PNC_Mother_Disease_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));


					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}

				else if (childPosition == 14 && childText.length()>2){
					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.Treatment_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));


					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}

				else if (childPosition == 15 && childText.length()>2){
					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.PNC_Mother_Advice_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));


					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}
				else if (childPosition == 17 && childText.length()>2){


					String[] animals = str.split(" ");
					String temp = "";
					details = res1.getStringArray(R.array.PNC_Mother_Refer_Reason_DropDown);
					for (String animal : animals) {
						System.out.println(animal);
						if(animal.length()>0)
							temp = temp+"\n"+details[Integer.parseInt(animal)];
					}
					Log.d("oooooooooo13+++" + str, "" + str.trim().split(" "));


					if(temp.length()>5)
						AlertMessage.showMessage(_context, "Details", temp);
				}




				//here I need to do some things that require me to manipulate the categoriesList from the Activity class - but it is out of scope
			}
		});

		return convertView;
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
