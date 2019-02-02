package com.mentalmachines.droidcon_boston.views.speaker


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mentalmachines.droidcon_boston.R
import com.mentalmachines.droidcon_boston.data.FirebaseDatabase.EventSpeaker
import com.mentalmachines.droidcon_boston.utils.ServiceLocator.Companion.gson
import com.mentalmachines.droidcon_boston.views.detail.SpeakerDetailFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import kotlinx.android.synthetic.main.speaker_fragment.*


class SpeakerFragment : Fragment(), FlexibleAdapter.OnItemClickListener {

    private lateinit var speakerAdapter: FlexibleAdapter<SpeakerAdapterItem>
    private val speakerViewModel: SpeakerViewModel by lazy {
        ViewModelProviders.of(this).get(SpeakerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.speaker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speakerViewModel.setup()
        speakerViewModel.rows.observe(this, Observer { speakers ->
            setupSpeakerAdapter(speakers)
        })
    }

    override fun onDestroyView() {
        speakerViewModel.cleanUp()
        super.onDestroyView()
    }

    private fun setupSpeakerAdapter(rows: List<EventSpeaker>) {
        val items = rows.map { SpeakerAdapterItem(it) }

        speaker_recycler.layoutManager = LinearLayoutManager(speaker_recycler.context)
        speakerAdapter = FlexibleAdapter(items)
        with (speakerAdapter) {
            this.addListener(this@SpeakerFragment)
            speaker_recycler.adapter = this
            this.expandItemsAtStartUp().setDisplayHeadersAtStartUp(false)
        }
        speaker_recycler.addItemDecoration(FlexibleItemDecoration(speaker_recycler.context).withDefaultDivider())
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        if (speakerAdapter.getItem(position) is SpeakerAdapterItem) {
            val item = speakerAdapter.getItem(position)
            val itemData = item?.itemData

            val arguments = Bundle().apply {
                putString(
                    EventSpeaker.SPEAKER_ITEM_ROW,
                    gson.toJson(itemData, EventSpeaker::class.java)
                )
            }

            SpeakerDetailFragment.instantiate(arguments, activity)
        }

        return true
    }
}
