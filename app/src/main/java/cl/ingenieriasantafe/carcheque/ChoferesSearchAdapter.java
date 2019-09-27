package cl.ingenieriasantafe.carcheque;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChoferesSearchAdapter extends ArrayAdapter<Choferes> {

    private Context context;
    private int LIMIT = 5;
    private List<Choferes> choferes;

    public ChoferesSearchAdapter(Context context, List<Choferes> choferes){
        super(context, R.layout.choferes_search,choferes);
        this.context = context;
        this.choferes = choferes;
    }

    @Override
    public int getCount(){
        return Math.min(LIMIT, choferes.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.choferes_search,null);
        Choferes vh = choferes.get(position);
        TextView textviewchoferes = view.findViewById(R.id.textViewChoferesSearch);
        textviewchoferes.setText(vh.getNombreApellido());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new PatentFilter(this,context);
    }

    private class PatentFilter extends Filter{

        private ChoferesSearchAdapter choferesSearchAdapter;
        private Context context;

        public PatentFilter(ChoferesSearchAdapter choferesSearchAdapter, Context context){
            super();
            this.choferesSearchAdapter = choferesSearchAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            choferesSearchAdapter.choferes.clear();
            FilterResults filterResults = new FilterResults();
            if (charSequence == null || charSequence.length() == 0){
                filterResults.values = new ArrayList<Vehiculos>();
                filterResults.count = 0;

            }else{
                DatabaseHelper db = new DatabaseHelper(context);
                List<Choferes> patents = db.searchchoferes(charSequence.toString());
                filterResults.values = patents;
                filterResults.count = patents.size();

            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            choferesSearchAdapter.choferes.clear();
            if ((List<Choferes>)filterResults.values != null){
                choferesSearchAdapter.choferes.addAll((List<Choferes>)filterResults.values);
                choferesSearchAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Choferes choferes = (Choferes) resultValue;
            return choferes.getNombreApellido();
        }
    }
}
