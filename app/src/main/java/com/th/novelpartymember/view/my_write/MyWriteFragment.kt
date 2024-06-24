package com.th.novelpartymember.view.my_write

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.th.novelpartymember.UserInfoViewModel
import com.th.novelpartymember.databinding.FragmentMyWriteBinding
import com.th.novelpartymember.model.UserEpisode
import com.th.novelpartymember.view.my_write_detail.MyWriteDetailActivity

class MyWriteFragment : Fragment(), MyWriteInterface {

    private lateinit var userInfoViewModel: UserInfoViewModel

    private var _binding: FragmentMyWriteBinding? = null
    private val binding get() = _binding!!
    private val userEpisodes = mutableListOf<UserEpisode>()
    private lateinit var myWriteAdapter: MyWriteAdapter

    private var nickName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userInfoViewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]

        userInfoViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            nickName = userInfo.nickName
        }
        init()
    }

    private fun init() {
        binding.apply {
            myWriteAdapter = MyWriteAdapter(userEpisodes, this@MyWriteFragment)

            recyclerMyWrite.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myWriteAdapter
            }
        }
        loadUserEpisodes()
    }

    private fun loadUserEpisodes() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userEpisodes = mutableListOf<UserEpisode>()

        db.collection("books")
            .get()
            .addOnSuccessListener { bookQuerySnapshot ->
                for (document in bookQuerySnapshot.documents) {
                    val bookId = document.id

                    val subCollections = (1..100).map { it.toString() }

                    for (subCollection in subCollections) {
                        db.collection("books")
                            .document(bookId)
                            .collection("userEpisode")
                            .document(subCollection)
                            .collection(userId)
                            .get()
                            .addOnSuccessListener { userEpisodeQuerySnapshot ->
                                for (userDocument in userEpisodeQuerySnapshot.documents) {
                                    val userEpisode = userDocument.toObject(UserEpisode::class.java)?.copy(title = bookId, nickName = nickName)
                                    userEpisode?.let {
                                        userEpisodes.add(it)
                                    }
                                }
                                updateCommentVisibility()

                                myWriteAdapter.updateData(userEpisodes)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error getting user episodes from $subCollection", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting books collection", e)
            }
    }

    private fun updateCommentVisibility() {
        val hasContent = myWriteAdapter.itemCount > 0
        binding.apply {
            binding.textNotContent.visibility = if (hasContent) View.GONE else View.VISIBLE
            binding.recyclerMyWrite.visibility = if (hasContent) View.VISIBLE else View.GONE
        }
    }

    override fun onContentBookDetailListener(userEpisode: UserEpisode) {
        val intent = Intent(requireContext(), MyWriteDetailActivity::class.java)
        intent.putExtra("userEpisode", userEpisode)
        startActivity(intent)
    }
}
