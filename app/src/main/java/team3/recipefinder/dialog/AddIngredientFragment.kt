package team3.recipefinder.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import team3.recipefinder.R
import team3.recipefinder.model.Ingredient


class AddIngredientFragment() : DialogFragment() {

    // Use this instance of the interface to deliver action events
    private lateinit var listener: CreateIngredientListener


    interface CreateIngredientListener {
        fun onDialogPositiveClick1(id: String?, name: String?)
        fun onDialogNegativeClick()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_ingredient_list, null)

            val a = requireArguments().getParcelableArrayList<Ingredient>("name")
            var mRgAllButtons = view.findViewById<RadioGroup>(R.id.radiogroup);

            if (a != null) {


                for (item: Ingredient in a) {
                    val rdbtn = RadioButton(context)
                    rdbtn.id = item.id.toInt()
                    rdbtn.text = item.name
                    rdbtn.isChecked = true
                    mRgAllButtons.addView(rdbtn)
                }
            }

            builder.setView(view)
                .setPositiveButton(
                    R.string.text_edit
                ) { _, _ ->
                    var ab = mRgAllButtons.checkedRadioButtonId.toString()

                    listener.onDialogPositiveClick1("i", ab)
                }
                .setNegativeButton(
                    R.string.text_cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as CreateIngredientListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement CreateIngredientListener")
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
    }
}