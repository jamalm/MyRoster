thpackage com.example.deadmadness.myroster;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by deadmadness on 24/11/15.
 */
public class ListCursor extends CursorAdapter {

    public ListCursor(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    //newView inflates a new view and returns it
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.timetable, parent, false);

    }

    //bindView binds all data to a specific view
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //fixing textviews
        TextView dayText = (TextView) view.findViewById(R.id.dayColumn);
        TextView startText = (TextView) view.findViewById(R.id.startColumn);
        TextView endText = (TextView) view.findViewById(R.id.endColumn);

        // getting cursor data/column and putting into variables
        String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
        String start = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
        String end = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));

        //setting the output textviews on the listview to show the column data
        dayText.setText(day);
        startText.setText(start);
        endText.setText(end);

    }
}
