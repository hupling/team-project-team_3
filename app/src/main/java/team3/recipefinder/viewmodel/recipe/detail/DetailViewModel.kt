package team3.recipefinder.viewmodel.recipe.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import team3.recipefinder.dao.RecipeDao
import team3.recipefinder.database.getAppDatabase
import team3.recipefinder.model.Recipe

class DetailViewModel(private val recipeKey: Int = 0, dataSource: RecipeDao, application: Application) :
    AndroidViewModel(application) {


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val database = dataSource


  val recipe =  database.get(recipeKey)
    init {
    //    recipe.value=Recipe(0,"Load")
      //  getK(recipeKey)
    }
    /*
    fun getK(name: Int) {
        uiScope.launch {

            get(name)
        }
    }

    private suspend fun get(t: Int) {
        withContext(Dispatchers.IO) {
            recipe.value = database.get(t)        }
    }
*/
}
