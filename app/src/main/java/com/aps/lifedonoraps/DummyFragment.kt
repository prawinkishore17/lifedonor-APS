package com.aps.lifedonoraps

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class DummyFragment : Fragment(R.layout.fragment_dummy) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title") ?: ""

        val textView = view.findViewById<TextView>(R.id.dummy_text)
        textView.text = title
    }

    companion object {
        fun newInstance(title: String): DummyFragment {
            val fragment = DummyFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            fragment.arguments = bundle
            return fragment
        }
    }
}