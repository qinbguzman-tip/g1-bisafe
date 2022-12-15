package com.example.g1_final_project.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.example.g1_final_project.models.HistoryItemModel
import com.google.firebase.database.DatabaseError
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.g1_final_project.R
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.g1_final_project.databinding.FragmentHistoryBinding
import com.example.g1_final_project.utils.Constants
import java.util.ArrayList

class HistoryFragment : Fragment() {
    private var binding: FragmentHistoryBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        Constants.databaseReference().child(Constants.auth().uid!!)
            .child(Constants.HISTORY)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && isAdded) {
                        historyArrayList!!.clear()
                        for (dataSnapshot in snapshot.children) {
                            historyArrayList.add(
                                dataSnapshot.getValue(
                                    HistoryItemModel::class.java
                                )
                            )
                        }
                        initRecyclerView()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        return root
    }

    private val historyArrayList: ArrayList<HistoryItemModel?>? = ArrayList()
    private var historyRecy: RecyclerView? = null
    private var adapter: RecyclerViewAdapterMessages? = null
    private fun initRecyclerView() {
        historyRecy = binding!!.historyRecy
        adapter = RecyclerViewAdapterMessages()
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        linearLayoutManager.reverseLayout = true
        historyRecy!!.layoutManager = linearLayoutManager
        historyRecy!!.setHasFixedSize(true)
        historyRecy!!.isNestedScrollingEnabled = false
        historyRecy!!.adapter = adapter
    }

    private inner class RecyclerViewAdapterMessages :
        RecyclerView.Adapter<RecyclerViewAdapterMessages.ViewHolderRightMessage>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRightMessage {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
            return ViewHolderRightMessage(view)
        }

        override fun onBindViewHolder(holder: ViewHolderRightMessage, position: Int) {
            val model = historyArrayList!![position]
            holder.title.text = model!!.title
            holder.time.text = "Time: " + model.time
            holder.distance.text = "Distance: " + model.distance + "km"
        }

        override fun getItemCount(): Int {
            return historyArrayList?.size ?: 0
        }

        inner class ViewHolderRightMessage(v: View) : RecyclerView.ViewHolder(v) {
            var title: TextView
            var time: TextView
            var distance: TextView

            init {
                title = v.findViewById(R.id.title_history)
                time = v.findViewById(R.id.time)
                distance = v.findViewById(R.id.distance)
            }
        }
    }
}