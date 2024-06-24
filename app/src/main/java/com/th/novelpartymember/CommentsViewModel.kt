import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.th.novelpartymember.model.CommentData

class CommentsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _commentCount = MutableLiveData<Int>()
    val commentCount: LiveData<Int> get() = _commentCount

    init {
        _commentCount.value = 0
    }

//    fun fetchCommentCount(bookId: String, bookEpisode: String) {
//        db.collection("comments")
//            .document(bookId)
//            .collection("comment")
//            .document(bookEpisode)
//            .collection("userComment")
//            .get()
//            .addOnSuccessListener { documents ->
//                _commentCount.value = documents.size()
//            }
//            .addOnFailureListener { exception ->
//                // 에러 처리
//            }
//    }

    fun fetchCommentCount(bookId: String, bookEpisode: String) {
        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode)
            .collection("userComment")
            .get()
            .addOnSuccessListener { documents ->
                // documents가 null이 아니고, 데이터가 포함되어 있을 때 size() 메서드 호출
                if (documents != null && !documents.isEmpty) {
                    _commentCount.value = documents.size()
                } else {
                    // documents가 null이거나 비어 있을 경우 예외 처리
                    // 예를 들어, 문서가 없을 때 처리할 내용을 기술할 수 있음
                    _commentCount.value = 0
                }
            }
            .addOnFailureListener { exception ->
                // 에러 처리
                Log.w(TAG, "Error fetching comment count", exception)
            }
    }


    fun listenToCommentChanges(bookId: String, bookEpisode: String) {
        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode)
            .collection("userComment")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // 에러 처리
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _commentCount.value = snapshot.size()
                }
            }
    }

    fun addComment(bookId: String, bookEpisode: String, comment: CommentData) {
        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode)
            .collection("userComment")
            .add(comment)
            .addOnSuccessListener {
                // 추가 성공 시 작업
            }
            .addOnFailureListener { e ->
                // 에러 처리
            }
    }

    fun deleteComment(bookId: String, bookEpisode: String, commentId: String) {
        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode)
            .collection("userComment")
            .document(commentId)
            .delete()
            .addOnSuccessListener {
                // 삭제 성공 시 작업
            }
            .addOnFailureListener { e ->
                // 에러 처리
            }
    }
}
