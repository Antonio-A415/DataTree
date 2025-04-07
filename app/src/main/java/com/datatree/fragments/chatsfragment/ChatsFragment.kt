package com.datatree.fragments.chatsfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.datatree.R
import com.datatree.core.repositories.MainRepository
import com.datatree.core.services.MainService
import com.datatree.core.services.MainServiceRepository
import com.datatree.core.utils.DataModel
import com.datatree.core.utils.DataModelType
import com.datatree.core.utils.getCameraAndMicPermission
import com.datatree.databinding.FragmentChatsBinding
import com.datatree.infraestructure.adapters.MainRecyclerViewAdapter
import com.datatree.ui.CallActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChatsFragment : Fragment(), MainRecyclerViewAdapter.Listener, MainService.Listener {

    private val TAG = "ListaUsuarios"

    private var _binding: FragmentChatsBinding? = null
    private val views get() = _binding!!

    private var username: String? = null

    @Inject
    lateinit var mainRepository: MainRepository

    @Inject
    lateinit var mainServiceRepository: MainServiceRepository

    private var mainAdapter: MainRecyclerViewAdapter? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username = activity?.intent?.getStringExtra("username")
        if (username == null) activity?.finish()
        changeStatusBarColor()

        init()
    }

    private fun init() {
        subscribeObservers()
        startMyService()
    }

    private fun subscribeObservers() {
        setupRecyclerView()
        MainService.listener = this
        mainRepository.observeUsersStatus {
            Log.d(TAG, "subscribeObservers: $it")
            mainAdapter?.updateList(it)
        }
    }

    private fun setupRecyclerView() {
        mainAdapter = MainRecyclerViewAdapter(this)
        views.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        views.mainRecyclerView.adapter = mainAdapter
    }

    private fun startMyService() {
        username?.let { mainServiceRepository.startService(it) }
    }



    override fun onVideoCallClicked(username: String) {
        (requireActivity() as AppCompatActivity).
        getCameraAndMicPermission() {
            mainRepository.sendConnectionRequest(username, true) {
                if (it) {
                    startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                        putExtra("target", username)
                        putExtra("isVideoCall", true)
                        putExtra("isCaller", true)
                    })
                }
            }
        }
    }

    override fun onAudioCallClicked(username: String) {
        (requireActivity() as AppCompatActivity)
            .getCameraAndMicPermission() {
            mainRepository.sendConnectionRequest(username, false) {
                if (it) {
                    startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                        putExtra("target", username)
                        putExtra("isVideoCall", false)
                        putExtra("isCaller", true)
                    })
                }
            }
        }
    }

    override fun onCallReceived(model: DataModel) {
        activity?.runOnUiThread {
            val isVideoCall = model.type == DataModelType.StartVideoCall
            views.incomingCallTitleTv.text = if (isVideoCall)
                "Solicitud de video llamada de: ${model.sender}"
            else
                "Solicitud de llamada de: ${model.sender}"
            views.incomingCallLayout.isVisible = true
            views.acceptButton.setOnClickListener {

                (requireActivity() as AppCompatActivity).getCameraAndMicPermission() {
                    views.incomingCallLayout.isVisible = false
                    startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                        putExtra("target", model.sender)
                        putExtra("isVideoCall", isVideoCall)
                        putExtra("isCaller", false)
                    })
                }
            }
            views.declineButton.setOnClickListener {
                views.incomingCallLayout.isVisible = false
            }
        }
    }

    private fun changeStatusBarColor() {
        activity?.window?.statusBarColor = requireContext().getColor(R.color.green_low)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainServiceRepository.stopService()
    }
}
