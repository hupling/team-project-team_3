package team3.recipefinder.viewmodel.recipe.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import team3.recipefinder.R
import team3.recipefinder.database.getAppDatabase
import team3.recipefinder.databinding.ActivityRecipeDetailBinding
import team3.recipefinder.model.Ingredient
import team3.recipefinder.viewmodel.recipe.overview.AddRecipeFragment

class EditRecipeActivity : AppCompatActivity(), AddRecipeFragment.EditRecipeListener,
    AddIngrFragment.EditListListener {
    private lateinit var viewModel: EditViewModel

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        var binding: ActivityRecipeDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val recipeKey = message.toLong()

        val application = requireNotNull(this).application

        // Get DAO instance
        val dataSource = getAppDatabase(application).recipeDao()

        // Create ViewModel
        val viewModelFactory =
            EditViewModelFactory(
                recipeKey,
                dataSource,
                application
            )

        viewModel = ViewModelProvider(this, viewModelFactory).get(EditViewModel::class.java)

        binding.model = viewModel

        viewModel.recipe.observe(this, Observer {
            Log.i("MainActivity", "OBSERVER CALLED RR ${it.name}")
            val textview = findViewById<TextView>(R.id.recipe_name)
            textview.text = it.name
        })


        viewModel.stepsRecipe.observe(this, Observer { it ->
            val listView = findViewById<ListView>(R.id.stepList)

            val adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1,
                it.map { s -> s.description }.toList()
            )

            listView.adapter = adapter
        })


        viewModel.ingredientRecipe.observe(this, Observer { it ->
            val listView = findViewById<ListView>(R.id.ingredientList)

            val adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1,
                it.map { i -> i.name }.toList()
            )
            listView.adapter = adapter
        })

        viewModel.editMode.observe(this, Observer {

            if (it) {
                editButton.setVisibility(View.GONE);
                addStepButton.setVisibility(View.VISIBLE);
                doneEditButton.setVisibility(View.VISIBLE);
                addIngredientButton.setVisibility(View.VISIBLE);
            } else {
                editButton.setVisibility(View.VISIBLE);
                addStepButton.setVisibility(View.GONE);
                doneEditButton.setVisibility(View.GONE);
                addIngredientButton.setVisibility(View.GONE);
            }
        })


    }


    fun showAddRecipeDialog(view: View) {
        val id: String = view.getTag().toString()
        val args = Bundle()
        args?.putString("name", id)

        val editTimerFragment = AddRecipeFragment()
        editTimerFragment.arguments = args
        editTimerFragment.show(supportFragmentManager, "Edit Timer")
    }

    fun showAddRecipeDialog1(view: View) {
        val args = Bundle()


        args?.putParcelableArrayList("name", viewModel.ingredients.value?.let { ArrayList(it) })
        val editTimerFragment = AddIngrFragment()
        editTimerFragment.arguments = args
        editTimerFragment.show(supportFragmentManager, "Edit Timer")
    }


    override fun onDialogPositiveClick(id: String?, value: String?) {
        when (id) {
            getString(R.string.text_stepName) -> viewModel.addStep(value!!)
        }
    }
    override fun onDialogPositiveClick1(id: String?, value: String?) {
        Toast.makeText(this, "bframgment ${value}", Toast.LENGTH_SHORT).show()
    }

    override fun onDialogNegativeClick() {

    }
}