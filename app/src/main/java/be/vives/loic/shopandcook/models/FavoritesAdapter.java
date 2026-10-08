package be.vives.loic.shopandcook.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.vives.loic.shopandcook.R;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> favorites;
    private final OnRecipeClickListener listener;

    public FavoritesAdapter(List<Recipe> favorites, OnRecipeClickListener listener) {
        this.favorites = favorites;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_recipe_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = favorites.get(position);
        holder.title.setText(recipe.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public Recipe removeAt(int position) {
        Recipe removed = favorites.remove(position);
        notifyItemRemoved(position);
        return removed;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }
    }
}
