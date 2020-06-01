package team3.recipefinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import team3.recipefinder.activity.LoginActivity
import team3.recipefinder.database.getAppDatabase
import team3.recipefinder.databinding.MainActivityBinding
import team3.recipefinder.dialog.AddItemFragment
import team3.recipefinder.viewModelFactory.RecipeViewModelFactory
import team3.recipefinder.viewmodel.RecipeViewModel

class MainActivity : AppCompatActivity(), AddItemFragment.EditRecipeListener {
    private lateinit var viewModel: RecipeViewModel

    private lateinit var auth: FirebaseAuth

    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }

        // Setup DataBinding
        val binding: MainActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)

        val application = requireNotNull(this).application

        // Get DAO instance
        val dataSource = getAppDatabase(application).recipeDao()

        // Create ViewModel
        val viewModelFactory =
            RecipeViewModelFactory(
                dataSource,
                application
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)

        // Bind model to layout
        binding.lifecycleOwner = this
        binding.recipeViewModel = viewModel

        // Register Adapter for the RecyclerView
        val adapter =
            RecipeAdapter(RecipeListener(viewModel))
        binding.recipeView.adapter = adapter

        // Observe LiveData
        viewModel.recipes.observe(this, Observer {
            Log.i("MainActivity", "OBSERVER CALLED")
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }


    fun showAddRecipeDialog(view: View) {
        val pageNumber: String = view.tag.toString()
        val args = Bundle()
        args.putString("name", pageNumber)

        val editTimerFragment = AddItemFragment()
        editTimerFragment.arguments = args
        editTimerFragment.show(supportFragmentManager, "Edit Timer")
    }

    override fun saveItem(id: String?, name: String?) {
        when (id) {
            getString(R.string.text_recipeName) -> {
                if (name != null) {
                    viewModel.addRecipe(name)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val mainToolbar: Toolbar = findViewById(R.id.toolbar)
        val mainMenu: Menu = mainToolbar.menu
        val userNameItem: MenuItem = mainMenu.findItem(R.id.user_name_settings)

        userNameItem.title = FirebaseAuth.getInstance().currentUser?.email
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.user_logout_settings -> {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_LONG).show()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }


}
