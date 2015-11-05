package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jamil.zaman on 05/11/15.
 */
public class PersonAdapter extends ArrayAdapter<Person> {

    Context context;
    int layoutResourceId;
    Person[] personData = null;

    public PersonAdapter(Context context, int layoutResourceId, Person[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.personData = data;
    }

    public PersonAdapter(Context context, int layoutResourceId, ArrayList<Person> data) {
        //
        super(context, layoutResourceId, data.toArray(new Person[]{}));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        Person[] personArray = {};
        this.personData = data.toArray(personArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PersonHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PersonHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.name = (TextView)row.findViewById(R.id.advSearch_name);
            holder.healthId = (TextView)row.findViewById(R.id.advSearch_healthId);

            row.setTag(holder);
        }
        else
        {
            holder = (PersonHolder)row.getTag();
        }

        Person person = personData[position];
        holder.name.setText(person.getName());
        holder.healthId.setText(person.getHealthId());
        holder.imgIcon.setImageResource(person.getIcon());

        return row;
    }

    static class PersonHolder
    {
        ImageView imgIcon;
        TextView name;
        TextView healthId;
    }

}
